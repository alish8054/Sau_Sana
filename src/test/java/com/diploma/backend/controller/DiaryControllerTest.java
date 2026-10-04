package com.diploma.backend.controller;

import com.diploma.backend.Entity.DiaryEntry;
import com.diploma.backend.Entity.User;
import com.diploma.backend.repository.DiaryEntryRepository;
import com.diploma.backend.security.AccessControlService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryControllerTest {

    @Mock
    private DiaryEntryRepository diaryRepository;

    @Mock
    private AccessControlService accessControl;

    @InjectMocks
    private DiaryController diaryController;

    @Test
    void getAllEntries_shouldReturnEntriesForCurrentUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = user(7L);
        DiaryEntry entry = diaryEntry(10L, "Yesterday I felt calm.", user);

        when(accessControl.currentUserId(request)).thenReturn(7L);
        when(diaryRepository.findAllByUser_IdOrderByCreatedAtDesc(7L)).thenReturn(List.of(entry));

        List<DiaryEntry> result = diaryController.getAllEntries(null, request);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Yesterday I felt calm.");
        verify(accessControl).requireSelfOrAssignedPsychologist(request, 7L);
    }

    @Test
    void createDiaryEntry_shouldRejectWhenUserAlreadyHasTodayEntry() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = user(3L);
        DiaryEntry existingEntry = diaryEntry(11L, "I am fine today.", user);

        when(accessControl.currentUserId(request)).thenReturn(3L);
        when(accessControl.currentUser(request)).thenReturn(user);
        when(diaryRepository.findByUser_Id(3L)).thenReturn(List.of(existingEntry));

        ResponseEntity<?> response = diaryController.createDiaryEntry(Map.of("text", "Another journal entry"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("You already made the main diary entry today.");
        verify(diaryRepository, never()).save(any(DiaryEntry.class));
    }

    @Test
    void createDiaryEntry_shouldSaveNewEntryForAuthenticatedUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = user(8L);
        DiaryEntry savedEntry = diaryEntry(21L, "I slept well and felt more focused.", user);

        when(accessControl.currentUserId(request)).thenReturn(8L);
        when(accessControl.currentUser(request)).thenReturn(user);
        when(diaryRepository.findByUser_Id(8L)).thenReturn(List.of());
        when(diaryRepository.save(any(DiaryEntry.class))).thenReturn(savedEntry);

        ResponseEntity<?> response = diaryController.createDiaryEntry(Map.of("text", "I slept well and felt more focused."), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(DiaryEntry.class);
        DiaryEntry body = (DiaryEntry) response.getBody();
        assertThat(body.getText()).isEqualTo("I slept well and felt more focused.");
        assertThat(body.getUser()).isEqualTo(user);
        verify(diaryRepository).save(any(DiaryEntry.class));
    }

    @Test
    void updateDiaryEntry_shouldUpdateTextForOwner() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = user(5L);
        DiaryEntry existingEntry = diaryEntry(18L, "Old text", user);

        when(diaryRepository.findById(18L)).thenReturn(Optional.of(existingEntry));
        when(diaryRepository.save(any(DiaryEntry.class))).thenAnswer(invocation -> {
            DiaryEntry entry = invocation.getArgument(0);
            entry.setText("Updated text");
            return entry;
        });

        ResponseEntity<?> response = diaryController.updateDiaryEntry(18L, Map.of("text", "Updated text"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DiaryEntry updated = (DiaryEntry) response.getBody();
        assertThat(updated.getText()).isEqualTo("Updated text");
        verify(accessControl).requireSelf(request, 5L);
        verify(diaryRepository).save(any(DiaryEntry.class));
    }

    @Test
    void deleteDiaryEntry_shouldDeleteOwnedEntry() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = user(12L);
        DiaryEntry entry = diaryEntry(22L, "Delete me", user);

        when(diaryRepository.findById(22L)).thenReturn(Optional.of(entry));

        ResponseEntity<?> response = diaryController.deleteDiaryEntry(22L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(accessControl).requireSelf(request, 12L);
        verify(diaryRepository).delete(entry);
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private DiaryEntry diaryEntry(Long id, String text, User user) {
        DiaryEntry entry = new DiaryEntry();
        entry.setId(id);
        entry.setText(text);
        entry.setUser(user);
        entry.setCreatedAt(LocalDateTime.now());
        return entry;
    }
}
