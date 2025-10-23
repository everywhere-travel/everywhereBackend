package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CarpetaRequestDTO;
import com.everywhere.backend.model.dto.CarpetaResponseDTO;
import com.everywhere.backend.model.entity.Carpeta;

public class CarpetaMapper {

    public static Carpeta toEntity(CarpetaRequestDTO carpetaRequestDto) {
        if (carpetaRequestDto == null) {
            return null;
        }

        Carpeta carpeta = new Carpeta();
        carpeta.setNombre(carpetaRequestDto.getNombre());
        carpeta.setDescripcion(carpetaRequestDto.getDescripcion());
        return carpeta;
    }

    public static CarpetaResponseDTO toResponse(Carpeta carpeta) {
        if (carpeta == null) {
            return null;
        }
        CarpetaResponseDTO responseDto = new CarpetaResponseDTO();
        responseDto.setId(carpeta.getId());
        responseDto.setNombre(carpeta.getNombre());
        responseDto.setDescripcion(carpeta.getDescripcion());
        responseDto.setCreado(carpeta.getCreado());
        responseDto.setActualizado(carpeta.getActualizado());
        responseDto.setNivel(carpeta.getNivel());
        responseDto.setCarpetaPadreId(
                carpeta.getCarpetaPadre() != null ? carpeta.getCarpetaPadre().getId() : null
        );
        return responseDto;
    }
}
