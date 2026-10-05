package com.edunest.repository;

import com.edunest.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<Note> findByUserIdAndLessonId(Long userId, Long lessonId);
}
