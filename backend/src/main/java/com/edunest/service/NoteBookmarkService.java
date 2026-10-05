package com.edunest.service;

import com.edunest.dto.request.BookmarkRequest;
import com.edunest.dto.request.NoteRequest;
import com.edunest.dto.response.BookmarkDto;
import com.edunest.dto.response.NoteDto;
import com.edunest.entity.*;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteBookmarkService {

    private final NoteRepository noteRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public NoteBookmarkService(NoteRepository noteRepository,
                               BookmarkRepository bookmarkRepository,
                               LessonRepository lessonRepository,
                               UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NoteDto saveOrUpdateNote(NoteRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Lesson lesson = lessonRepository.findById(req.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Note note = noteRepository.findByUserIdAndLessonId(student.getId(), lesson.getId())
                .orElseGet(() -> {
                    Note n = new Note();
                    n.setUser(student);
                    n.setLesson(lesson);
                    return n;
                });

        note.setContent(req.getContent().trim());
        note.setUpdatedAt(LocalDateTime.now());

        Note saved = noteRepository.save(note);
        return toNoteDto(saved);
    }

    @Transactional(readOnly = true)
    public NoteDto getNoteForLesson(Long lessonId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return noteRepository.findByUserIdAndLessonId(student.getId(), lessonId)
                .map(this::toNoteDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<NoteDto> getMyNotes(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return noteRepository.findByUserIdOrderByUpdatedAtDesc(student.getId()).stream()
                .map(this::toNoteDto)
                .toList();
    }

    @Transactional
    public void deleteNote(Long noteId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized to delete this note");
        }
        noteRepository.delete(note);
    }

    @Transactional
    public BookmarkDto addOrUpdateBookmark(BookmarkRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Lesson lesson = lessonRepository.findById(req.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Bookmark bookmark = bookmarkRepository.findByUserIdAndLessonId(student.getId(), lesson.getId())
                .orElseGet(() -> {
                    Bookmark b = new Bookmark();
                    b.setUser(student);
                    b.setLesson(lesson);
                    return b;
                });

        String label = (req.getNoteLabel() != null && !req.getNoteLabel().isBlank())
                ? req.getNoteLabel().trim()
                : "Bookmarked for review";
        bookmark.setNoteLabel(label);
        bookmark.setCreatedAt(LocalDateTime.now());

        Bookmark saved = bookmarkRepository.save(bookmark);
        return toBookmarkDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BookmarkDto> getMyBookmarks(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(student.getId()).stream()
                .map(this::toBookmarkDto)
                .toList();
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        if (!bookmark.getUser().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized to delete this bookmark");
        }
        bookmarkRepository.delete(bookmark);
    }

    private NoteDto toNoteDto(Note n) {
        Lesson l = n.getLesson();
        Topic t = l.getTopic();
        Course c = t.getCourse();
        return new NoteDto(
                n.getId(),
                l.getId(),
                l.getTitle(),
                t.getTitle(),
                c.getTitle(),
                n.getContent(),
                n.getUpdatedAt()
        );
    }

    private BookmarkDto toBookmarkDto(Bookmark b) {
        Lesson l = b.getLesson();
        Topic t = l.getTopic();
        Course c = t.getCourse();
        return new BookmarkDto(
                b.getId(),
                l.getId(),
                l.getTitle(),
                t.getTitle(),
                c.getId(),
                c.getTitle(),
                b.getNoteLabel(),
                b.getCreatedAt()
        );
    }
}
