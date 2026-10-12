package com.everywhere.backend.util.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA para cifrar y descifrar de manera automática y transparente
 * los números de documento (DNI) entre la entidad y la base de datos PostgreSQL.
 */
@Converter
public class DniEncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return DniEncryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return DniEncryptionUtil.decrypt(dbData);
    }
}
