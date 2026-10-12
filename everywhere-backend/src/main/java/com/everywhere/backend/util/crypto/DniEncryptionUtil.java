package com.everywhere.backend.util.crypto;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Utilidad criptográfica determinista con AES-256 para protección de números de DNI.
 * Almacena los datos en formato hexadecimal con prefijo 'enc:'.
 * Tolera registros legados sin cifrar para máxima retrocompatibilidad.
 */
@Slf4j
@Component
public class DniEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String PREFIX = "enc:";
    private static final String DEFAULT_FALLBACK_SECRET = "EveryWhereTravelKey2026SecureDni";

    private static volatile SecretKeySpec secretKeySpec;
    private static volatile IvParameterSpec ivParameterSpec;

    @Value("${app.encryption.secret:EveryWhereTravelKey2026SecureDni}")
    private String injectedSecret;

    @PostConstruct
    public void init() {
        initKeys(injectedSecret != null && !injectedSecret.isBlank() ? injectedSecret : DEFAULT_FALLBACK_SECRET);
        log.info("DniEncryptionUtil inicializado correctamente con AES-256.");
    }

    /**
     * Inicializa las claves criptográficas asegurando 256 bits para AES
     * y un IV determinista de 16 bytes para permitir búsquedas exactas.
     */
    public static synchronized void initKeys(String secret) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);

            // IV determinista derivado del hash del secret + sal fija
            byte[] ivBytes = sha.digest((secret + "_iv_salt_everywhere").getBytes(StandardCharsets.UTF_8));
            ivParameterSpec = new IvParameterSpec(Arrays.copyOf(ivBytes, 16));
        } catch (Exception e) {
            log.error("Error al inicializar claves de cifrado DNI: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo inicializar DniEncryptionUtil", e);
        }
    }

    private static void ensureInitialized() {
        if (secretKeySpec == null || ivParameterSpec == null) {
            initKeys(DEFAULT_FALLBACK_SECRET);
        }
    }

    /**
     * Cifra un DNI en texto plano a formato 'enc:<hex>'.
     * Si el valor ya está cifrado o es nulo/vacío, lo retorna tal cual.
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return plainText;
        }

        String trimmed = plainText.trim();
        if (trimmed.startsWith(PREFIX)) {
            return trimmed; // Ya está cifrado
        }

        ensureInitialized();

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(trimmed.getBytes(StandardCharsets.UTF_8));
            return PREFIX + bytesToHex(encryptedBytes);
        } catch (Exception e) {
            log.error("Error al cifrar DNI: {}", e.getMessage());
            return plainText; // Fallback seguro
        }
    }

    /**
     * Descifra un DNI en formato 'enc:<hex>' a texto plano.
     * Si el valor no tiene el prefijo 'enc:' (registro legado sin cifrar) o es nulo/vacío, lo retorna tal cual.
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.trim().isEmpty()) {
            return cipherText;
        }

        String trimmed = cipherText.trim();
        if (!trimmed.startsWith(PREFIX)) {
            // Retrocompatibilidad con datos legados en texto plano
            return trimmed;
        }

        ensureInitialized();

        try {
            String hexContent = trimmed.substring(PREFIX.length());
            byte[] encryptedBytes = hexToBytes(hexContent);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("No se pudo descifrar el valor de DNI (posible texto legado con prefijo o clave errónea): {}", e.getMessage());
            return cipherText;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
