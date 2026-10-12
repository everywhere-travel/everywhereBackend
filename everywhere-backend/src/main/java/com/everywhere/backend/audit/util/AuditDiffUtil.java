package com.everywhere.backend.audit.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Utilidad para serializar entidades a JSON y calcular el Diff campo por campo
 * entre el estado previo y el estado posterior a una operación.
 */
@Slf4j
public class AuditDiffUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "usr_pass_vc", "token", "jwt", "secret", "cvv"
    );

    /**
     * Serializa un objeto a JSON seguro (enmascarando contraseñas).
     */
    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            JsonNode node = OBJECT_MAPPER.valueToTree(obj);
            sanitize(node);
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            log.warn("No se pudo serializar objeto para auditoría: {}", e.getMessage());
            return String.valueOf(obj);
        }
    }

    /**
     * Compara dos objetos y genera un JSON con únicamente los campos que han cambiado:
     * {
     *   "nombre": { "antes": "Carlos", "ahora": "Carlos Alberto" },
     *   "monto": { "antes": 100.0, "ahora": 150.0 }
     * }
     */
    public static String calculateDiff(Object previo, Object nuevo) {
        if (previo == null && nuevo == null) return null;
        if (previo == null) return toJson(nuevo);
        if (nuevo == null) return toJson(previo);

        try {
            JsonNode nodePrevio = OBJECT_MAPPER.valueToTree(previo);
            JsonNode nodeNuevo = OBJECT_MAPPER.valueToTree(nuevo);

            sanitize(nodePrevio);
            sanitize(nodeNuevo);

            ObjectNode diffNode = OBJECT_MAPPER.createObjectNode();

            Set<String> allKeys = new HashSet<>();
            if (nodePrevio.isObject()) nodePrevio.fieldNames().forEachRemaining(allKeys::add);
            if (nodeNuevo.isObject()) nodeNuevo.fieldNames().forEachRemaining(allKeys::add);

            for (String key : allKeys) {
                if (SENSITIVE_FIELDS.contains(key.toLowerCase())) continue;

                JsonNode valPrevio = nodePrevio.get(key);
                JsonNode valNuevo = nodeNuevo.get(key);

                if (!Objects.equals(valPrevio, valNuevo)) {
                    ObjectNode fieldDiff = OBJECT_MAPPER.createObjectNode();
                    fieldDiff.set("antes", valPrevio);
                    fieldDiff.set("ahora", valNuevo);
                    diffNode.set(key, fieldDiff);
                }
            }

            return diffNode.isEmpty() ? null : OBJECT_MAPPER.writeValueAsString(diffNode);
        } catch (Exception e) {
            log.warn("Error al calcular diff de auditoría: {}", e.getMessage());
            return null;
        }
    }

    private static void sanitize(JsonNode node) {
        if (node == null || !node.isObject()) return;
        ObjectNode objectNode = (ObjectNode) node;
        for (String sensitive : SENSITIVE_FIELDS) {
            if (objectNode.has(sensitive)) {
                objectNode.put(sensitive, "********");
            }
        }
    }
}
