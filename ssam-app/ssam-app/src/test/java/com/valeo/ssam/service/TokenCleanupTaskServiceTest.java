package com.valeo.ssam.service;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;
import com.valeo.ssam.repository.AssetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

public class TokenCleanupTaskServiceTest {

    @Mock
    private AssetTokenRepository repository;

    @InjectMocks
    private TokenCleanupTaskService cleanupService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // Scenario 1: Token should be deleted
    // ---------------------------------------------------------
    @Test
    void cleanup_ShouldDeleteTokensOlderThan60Seconds() {
        Instant oldTimestamp = Instant.now().minusSeconds(120);

        AssetToken token = new AssetToken();
        token.setStatus(StatusEnum.IN_TERMINATION);
        token.setUpdateCounter(1L);
        token.setLastStatusChange(oldTimestamp);

        when(repository.findAll()).thenReturn(List.of(token));

        cleanupService.cleanup();

        verify(repository, times(1)).delete(token);
    }

    // ---------------------------------------------------------
    // Scenario 2: Token should NOT be deleted (recent)
    // ---------------------------------------------------------
    @Test
    void cleanup_ShouldNotDeleteRecentTokens() {
        Instant recent = Instant.now().minusSeconds(10);

        AssetToken token = new AssetToken();
        token.setStatus(StatusEnum.IN_TERMINATION);
        token.setUpdateCounter(1L);
        token.setLastStatusChange(recent);

        when(repository.findAll()).thenReturn(List.of(token));

        cleanupService.cleanup();

        verify(repository, never()).delete(any());
    }

    // ---------------------------------------------------------
    // Scenario 3: Token should NOT be deleted (diferent status)
    // ---------------------------------------------------------
    @Test
    void cleanup_ShouldNotDeleteTokensWithDifferentStatus() {
        AssetToken token = new AssetToken();
        token.setStatus(StatusEnum.ACTIVE);
        token.setUpdateCounter(1L);
        token.setLastStatusChange(Instant.now().minusSeconds(200));

        when(repository.findAll()).thenReturn(List.of(token));

        cleanupService.cleanup();

        verify(repository, never()).delete(any());
    }

    // ---------------------------------------------------------
    // Scenario 4: Token should NOT be deleted (updateCounter null)
    // ---------------------------------------------------------
    @Test
    void cleanup_ShouldNotDeleteTokensWithNullUpdateCounter() {
        AssetToken token = new AssetToken();
        token.setStatus(StatusEnum.IN_TERMINATION);
        token.setUpdateCounter(null);
        token.setLastStatusChange(Instant.now().minusSeconds(200));

        when(repository.findAll()).thenReturn(List.of(token));

        cleanupService.cleanup();

        verify(repository, never()).delete(any());
    }

    // ---------------------------------------------------------
    // Scenario 5: Lots of mixed tokens
    // ---------------------------------------------------------
    @Test
    void cleanup_ShouldDeleteOnlyExpiredTokens() {
        Instant old = Instant.now().minusSeconds(200);
        Instant recent = Instant.now().minusSeconds(20);

        AssetToken expired = new AssetToken();
        expired.setStatus(StatusEnum.IN_TERMINATION);
        expired.setUpdateCounter(1L);
        expired.setLastStatusChange(old);

        AssetToken recentToken = new AssetToken();
        recentToken.setStatus(StatusEnum.IN_TERMINATION);
        recentToken.setUpdateCounter(1L);
        recentToken.setLastStatusChange(recent);

        AssetToken active = new AssetToken();
        active.setStatus(StatusEnum.ACTIVE);
        active.setUpdateCounter(1L);
        active.setLastStatusChange(old);

        when(repository.findAll()).thenReturn(List.of(expired, recentToken, active));

        cleanupService.cleanup();

        verify(repository, times(1)).delete(expired);
        verify(repository, never()).delete(recentToken);
        verify(repository, never()).delete(active);
    }

}
