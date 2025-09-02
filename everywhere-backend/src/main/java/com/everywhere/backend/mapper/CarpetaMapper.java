package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.model.entity.Carpeta;

public class CarpetaMapper {

    public static Carpeta toEntity(CarpetaRequestDto carpetaRequestDto) {
        if (carpetaRequestDto == null) {
            return null;
        }

        Carpeta carpeta = new Carpeta();
        carpeta.setNombre(carpetaRequestDto.getNombre());
        carpeta.setDescripcion(carpetaRequestDto.getDescripcion());
        return carpeta;
    }

    public static CarpetaResponseDto toResponse(Carpeta carpeta) {
        if (carpeta == null) {
            return null;
        }
        CarpetaResponseDto responseDto = new CarpetaResponseDto();
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
