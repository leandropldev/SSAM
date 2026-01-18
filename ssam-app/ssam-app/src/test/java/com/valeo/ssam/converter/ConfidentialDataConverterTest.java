package com.valeo.ssam.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfidentialDataConverterTest {

    private ConfidentialDataConverter converter;

    @BeforeEach
    void setup() {
        converter = new ConfidentialDataConverter();
    }

    // ---------------------------------------------------------
    // Case 1: Encrypting should return a different value.
    // ---------------------------------------------------------
    @Test
    void convertToDatabaseColumn_ShouldEncryptValue() {
        String original = "my-secret-data";

        String encrypted = converter.convertToDatabaseColumn(original);

        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);
    }

    // ---------------------------------------------------------
    // Case 2:  Decrypt should return original value.
    // ---------------------------------------------------------
    @Test
    void convertToEntityAttribute_ShouldDecryptValue() {
        String original = "another-secret";

        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(original, decrypted);
    }

    // ---------------------------------------------------------
    // Case 3: Encrypt and then Decrypt keeps integrity
    // ---------------------------------------------------------
    @Test
    void encryptThenDecrypt_ShouldReturnOriginal() {
        String original = "super-sensitive-info-123";

        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertEquals(original, decrypted);
    }

    // ---------------------------------------------------------
    // Case 4: Different Strings should create different encryption's
    // ---------------------------------------------------------
    @Test
    void encryptingDifferentValues_ShouldProduceDifferentOutputs() {
        String v1 = "value-one";
        String v2 = "value-two";

        String e1 = converter.convertToDatabaseColumn(v1);
        String e2 = converter.convertToDatabaseColumn(v2);

        assertNotEquals(e1, e2);
    }

    // ---------------------------------------------------------
    // Case 5: Encrypt empty String
    // ---------------------------------------------------------
    @Test
    void convertToDatabaseColumn_ShouldHandleEmptyString() {
        String encrypted = converter.convertToDatabaseColumn("");

        assertNotNull(encrypted);
        assertNotEquals("", encrypted);

        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertEquals("", decrypted);
    }

    // ---------------------------------------------------------
    // Case 6: Null behavior
    // ---------------------------------------------------------
    @Test
    void convertToDatabaseColumn_ShouldThrowOnNull() {
        assertThrows(RuntimeException.class, () -> converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_ShouldThrowOnNull() {
        assertThrows(RuntimeException.class, () -> converter.convertToEntityAttribute(null));
    }
}
