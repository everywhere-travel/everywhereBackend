package com.everywhere.backend.util.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DniEncryptionUtilTest {

    @BeforeEach
    void setUp() {
        DniEncryptionUtil.initKeys("TestSecretKeyForEveryWhere2026AES");
    }

    @Test
    void testEncryptionAndDecryption() {
        String originalDni = "72345678";
        String encrypted = DniEncryptionUtil.encrypt(originalDni);

        assertNotNull(encrypted);
        assertTrue(encrypted.startsWith("enc:"), "El texto cifrado debe comenzar con el prefijo 'enc:'");
        assertNotEquals(originalDni, encrypted);

        String decrypted = DniEncryptionUtil.decrypt(encrypted);
        assertEquals(originalDni, decrypted, "El texto descifrado debe coincidir exactamente con el original");
    }

    @Test
    void testDeterministicEncryption() {
        String dni = "87654321";
        String enc1 = DniEncryptionUtil.encrypt(dni);
        String enc2 = DniEncryptionUtil.encrypt(dni);

        assertEquals(enc1, enc2, "El cifrado debe ser determinista para permitir búsquedas exactas en base de datos");
    }

    @Test
    void testLegacyUnencryptedRetrocompatibility() {
        String legacyPlainDni = "45678901";
        // Si la base de datos tiene un DNI legado sin cifrar (sin 'enc:')
        String result = DniEncryptionUtil.decrypt(legacyPlainDni);
        assertEquals(legacyPlainDni, result, "Los registros legados sin prefijo 'enc:' deben devolverse intactos sin error");
    }

    @Test
    void testIdempotency() {
        String dni = "12345678";
        String enc1 = DniEncryptionUtil.encrypt(dni);
        String enc2 = DniEncryptionUtil.encrypt(enc1);

        assertEquals(enc1, enc2, "Cifrar un valor ya cifrado no debe volver a cifrarlo");
    }

    @Test
    void testNullAndEmptyHandling() {
        assertNull(DniEncryptionUtil.encrypt(null));
        assertEquals("", DniEncryptionUtil.encrypt(""));
        assertEquals("   ", DniEncryptionUtil.encrypt("   "));

        assertNull(DniEncryptionUtil.decrypt(null));
        assertEquals("", DniEncryptionUtil.decrypt(""));
    }

    @Test
    void testConverterIntegration() {
        DniEncryptionConverter converter = new DniEncryptionConverter();
        String dni = "71234567";

        String dbColumn = converter.convertToDatabaseColumn(dni);
        assertTrue(dbColumn.startsWith("enc:"));

        String entityAttribute = converter.convertToEntityAttribute(dbColumn);
        assertEquals(dni, entityAttribute);
    }
}
