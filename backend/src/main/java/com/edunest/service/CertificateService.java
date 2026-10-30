package com.edunest.service;

import com.edunest.dto.response.CertificateResponse;
import com.edunest.entity.Certificate;
import com.edunest.entity.Course;
import com.edunest.entity.Enrollment;
import com.edunest.entity.User;
import com.edunest.repository.CertificateRepository;
import com.edunest.repository.EnrollmentRepository;
import com.edunest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public CertificateService(CertificateRepository certificateRepository,
                              EnrollmentRepository enrollmentRepository,
                              UserRepository userRepository) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Certificate generateCertificateIfEligible(User student, Course course, Double finalScore) {
        // Prevent duplicates
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCourseId(student.getId(), course.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        // Generate unique readable certificate ID: EDU-JAVA-XXXXXXXX
        String courseCode = course.getTitle().toUpperCase().replaceAll("[^A-Z]", "");
        if (courseCode.length() > 4) {
            courseCode = courseCode.substring(0, 4);
        } else if (courseCode.isEmpty()) {
            courseCode = "CERT";
        }
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String certificateId = "EDU-" + courseCode + "-" + uniqueSuffix;

        // Double check uniqueness (extremely unlikely to collide, but safe)
        while (certificateRepository.findByCertificateId(certificateId).isPresent()) {
            uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            certificateId = "EDU-" + courseCode + "-" + uniqueSuffix;
        }

        Certificate cert = new Certificate();
        cert.setCertificateId(certificateId);
        cert.setUser(student);
        cert.setCourse(course);
        cert.setFinalScore(finalScore != null ? finalScore : 100.0);
        cert.setIssuedAt(LocalDateTime.now());

        return certificateRepository.save(cert);
    }

    @Transactional
    public List<CertificateResponse> getMyCertificates(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Backfill check: Ensure any completed enrollments have certificates generated
        List<Enrollment> completedEnrollments = enrollmentRepository.findByUserId(student.getId()).stream()
                .filter(Enrollment::isCompleted)
                .toList();

        for (Enrollment e : completedEnrollments) {
            if (!certificateRepository.existsByUserIdAndCourseId(student.getId(), e.getCourse().getId())) {
                generateCertificateIfEligible(student, e.getCourse(), e.getFinalScore());
            }
        }

        return certificateRepository.findByUserIdOrderByIssuedAtDesc(student.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateById(String certId) {
        Certificate cert = certificateRepository.findByCertificateId(certId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        return toResponse(cert);
    }

    private CertificateResponse toResponse(Certificate c) {
        String instructorName = (c.getCourse() != null && c.getCourse().getInstructor() != null)
                ? c.getCourse().getInstructor().getName()
                : "EduNest Academic Board";

        return new CertificateResponse(
                c.getId(),
                c.getCertificateId(),
                c.getUser().getName(),
                c.getUser().getEmail(),
                c.getCourse().getId(),
                c.getCourse().getTitle(),
                instructorName,
                c.getFinalScore(),
                c.getIssuedAt()
        );
    }
}
