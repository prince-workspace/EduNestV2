package com.edunest.controller;

import com.edunest.dto.response.CertificateResponse;
import com.edunest.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificateResponse>> getMyCertificates(Authentication authentication) {
        return ResponseEntity.ok(certificateService.getMyCertificates(authentication.getName()));
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateResponse> getCertificateById(@PathVariable String certificateId) {
        return ResponseEntity.ok(certificateService.getCertificateById(certificateId));
    }
}
