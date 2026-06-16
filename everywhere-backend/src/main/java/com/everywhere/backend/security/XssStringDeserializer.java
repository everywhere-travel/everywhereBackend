package com.everywhere.backend.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.regex.Pattern;

@JsonComponent
public class XssStringDeserializer extends JsonDeserializer<String> {

    // Expresión regular para detectar etiquetas <script>
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script.*?>.*?</script.*?>|<script.*?>");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value != null && SCRIPT_PATTERN.matcher(value).find()) {
            throw new IllegalArgumentException("Payload rechazado: Se han detectado etiquetas <script> (Posible ataque XSS)");
        }
        return value;
    }
}
