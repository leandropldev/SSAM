package com.valeo.ssam.service;

import com.valeo.ssam.entity.AssetToken;
import com.valeo.ssam.entity.StatusEnum;
import com.valeo.ssam.exception.GenericException;
import com.valeo.ssam.model.AssetTokenResponse;
import com.valeo.ssam.model.CreateAssetToken;
import com.valeo.ssam.repository.AssetTokenRepository;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AssetTokenServiceTest {

    @Mock
    private AssetTokenRepository repository;

    @InjectMocks
    private AssetTokenService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // createNewToken
    // ---------------------------------------------------------
    @Test
    void createNewToken_ShouldCreateAndReturnId() {
        CreateAssetToken request = new CreateAssetToken("owner123", "secret");

        AssetToken saved = new AssetToken();
        saved.setId(UUID.randomUUID());

        when(repository.save(any(AssetToken.class))).thenReturn(saved);

        UUID result = service.createNewToken(request);

        assertNotNull(result);
        verify(repository, times(1)).save(any(AssetToken.class));
    }

    // ---------------------------------------------------------
    // listAllTokens
    // ---------------------------------------------------------
    @Test
    void listAllTokens_ShouldReturnMappedResponses() {
        AssetToken token = new AssetToken();
        token.setId(UUID.randomUUID());
        token.setStatus(StatusEnum.ACTIVE);
        token.setOwnerId("owner");
        token.setUpdateCounter(1L);

        when(repository.findAll()).thenReturn(List.of(token));

        List<AssetTokenResponse> result = service.listAllTokens();

        assertEquals(1, result.size());
        assertEquals(token.getId(), result.getFirst().id());
        assertEquals(StatusEnum.ACTIVE, result.getFirst().status());
    }

    // ---------------------------------------------------------
    // getAssetTokenById
    // ---------------------------------------------------------
    @Test
    void getAssetTokenById_ShouldReturnOk() {
        UUID id = UUID.randomUUID();
        AssetToken token = new AssetToken();
        token.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(token));

        ResponseEntity<@NonNull AssetToken> response = service.getAssetTokenById(id);

        assertEquals(200, response.getStatusCode().value());
        assert response.getBody() != null;
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void getAssetTokenById_ShouldReturnNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<@NonNull AssetToken> response = service.getAssetTokenById(id);

        assertEquals(404, response.getStatusCode().value());
    }

    // ---------------------------------------------------------
    // suspendToken
    // ---------------------------------------------------------
    @Test
    void suspendToken_ShouldUpdateStatus() {
        UUID id = UUID.randomUUID();

        AssetToken token = new AssetToken();
        token.setId(id);
        token.setStatus(StatusEnum.ACTIVE);

        when(repository.findById(id)).thenReturn(Optional.of(token));

        service.suspendToken(id);

        assertEquals(StatusEnum.SUSPENDED, token.getStatus());
        verify(repository).save(token);
    }

    @Test
    void suspendToken_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.suspendToken(id));
    }

    // ---------------------------------------------------------
    // shareToken
    // ---------------------------------------------------------
    @Test
    void shareToken_ShouldAllocateFirstFreeSlot() {
        UUID parentId = UUID.randomUUID();

        AssetToken parent = new AssetToken();
        parent.setId(parentId);
        parent.setSlotBitmap((byte) 0b00000000);
        parent.setChildren(new ArrayList<>());
        parent.setConfidentialData("secret");

        when(repository.findById(parentId)).thenReturn(Optional.of(parent));
        when(repository.save(any())).thenReturn(parent);

        UUID result = service.shareToken(parentId, "friend@mail.com");

        assertNotNull(result);
        assertEquals(1, parent.getChildren().size());
        assertEquals(0b00000001, parent.getSlotBitmap());
    }

    @Test
    void shareToken_ShouldThrow_WhenNoSlotsAvailable() {
        UUID parentId = UUID.randomUUID();

        AssetToken parent = new AssetToken();
        parent.setId(parentId);
        parent.setSlotBitmap((byte) 0xFF); // all 8 bits used
        parent.setChildren(new ArrayList<>());

        when(repository.findById(parentId)).thenReturn(Optional.of(parent));

        assertThrows(GenericException.class,
                () -> service.shareToken(parentId, "friend@mail.com"));
    }

    @Test
    void shareToken_ShouldThrow_WhenParentNotFound() {
        UUID parentId = UUID.randomUUID();

        when(repository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(GenericException.class,
                () -> service.shareToken(parentId, "friend@mail.com"));
    }

    // ---------------------------------------------------------
    // terminateToken
    // ---------------------------------------------------------
    @Test
    void terminateToken_ShouldCascadeToChildren() {
        UUID id = UUID.randomUUID();

        AssetToken parent = new AssetToken();
        parent.setId(id);
        parent.setStatus(StatusEnum.ACTIVE);

        AssetToken child1 = new AssetToken();
        child1.setStatus(StatusEnum.ACTIVE);

        AssetToken child2 = new AssetToken();
        child2.setStatus(StatusEnum.ACTIVE);

        parent.setChildren(List.of(child1, child2));

        when(repository.findById(id)).thenReturn(Optional.of(parent));

        service.terminateToken(id);

        assertEquals(StatusEnum.TERMINATED, parent.getStatus());
        assertEquals(StatusEnum.IN_TERMINATION, child1.getStatus());
        assertEquals(StatusEnum.IN_TERMINATION, child2.getStatus());

        verify(repository).save(parent);
    }

    @Test
    void terminateToken_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(GenericException.class, () -> service.terminateToken(id));
    }

}
