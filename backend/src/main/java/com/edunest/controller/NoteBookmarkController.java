package com.edunest.controller;

import com.edunest.dto.request.BookmarkRequest;
import com.edunest.dto.request.NoteRequest;
import com.edunest.dto.response.BookmarkDto;
import com.edunest.dto.response.NoteDto;
import com.edunest.service.NoteBookmarkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students/me")
public class NoteBookmarkController {

    private final NoteBookmarkService noteBookmarkService;

    public NoteBookmarkController(NoteBookmarkService noteBookmarkService) {
        this.noteBookmarkService = noteBookmarkService;
    }

    @PostMapping("/notes")
    public ResponseEntity<NoteDto> saveNote(@Valid @RequestBody NoteRequest req,
                                            Authentication authentication) {
        return ResponseEntity.ok(noteBookmarkService.saveOrUpdateNote(req, authentication.getName()));
    }

    @GetMapping("/notes")
    public ResponseEntity<List<NoteDto>> getMyNotes(Authentication authentication) {
        return ResponseEntity.ok(noteBookmarkService.getMyNotes(authentication.getName()));
    }

    @GetMapping("/notes/lesson/{lessonId}")
    public ResponseEntity<NoteDto> getNoteForLesson(@PathVariable Long lessonId,
                                                    Authentication authentication) {
        NoteDto note = noteBookmarkService.getNoteForLesson(lessonId, authentication.getName());
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable Long noteId,
                                                          Authentication authentication) {
        noteBookmarkService.deleteNote(noteId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Note deleted successfully"));
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<BookmarkDto> addBookmark(@Valid @RequestBody BookmarkRequest req,
                                                   Authentication authentication) {
        return ResponseEntity.ok(noteBookmarkService.addOrUpdateBookmark(req, authentication.getName()));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<BookmarkDto>> getMyBookmarks(Authentication authentication) {
        return ResponseEntity.ok(noteBookmarkService.getMyBookmarks(authentication.getName()));
    }

    @DeleteMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<Map<String, String>> deleteBookmark(@PathVariable Long bookmarkId,
                                                              Authentication authentication) {
        noteBookmarkService.deleteBookmark(bookmarkId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Bookmark deleted successfully"));
    }
}
