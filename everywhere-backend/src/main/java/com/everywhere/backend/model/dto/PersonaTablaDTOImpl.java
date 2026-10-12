package com.everywhere.backend.model.dto;

import com.everywhere.backend.util.crypto.DniEncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementación de PersonaTablaDTO que garantiza el descifrado transparente
 * del campo documento (DNI) antes de enviarlo a las vistas del frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaTablaDTOImpl implements PersonaTablaDTO {
    private Integer id;
    private Integer tipoId;
    private String tipo;
    private String nombre;
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String razonSocial;
    private String documento;
    private String ruc;
    private String email;
    private String telefono;
    private String direccion;

    public static PersonaTablaDTO fromProjection(PersonaTablaDTO proj) {
        if (proj == null) {
            return null;
        }
        return PersonaTablaDTOImpl.builder()
                .id(proj.getId())
                .tipoId(proj.getTipoId())
                .tipo(proj.getTipo())
                .nombre(proj.getNombre())
                .nombres(proj.getNombres())
                .apellidosPaterno(proj.getApellidosPaterno())
                .apellidosMaterno(proj.getApellidosMaterno())
                .razonSocial(proj.getRazonSocial())
                .documento(DniEncryptionUtil.decrypt(proj.getDocumento()))
                .ruc(proj.getRuc())
                .email(proj.getEmail())
                .telefono(proj.getTelefono())
                .direccion(proj.getDireccion())
                .build();
    }
}
