package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentConfirmUploadRequest {
    private Integer idTalento;
    // Optional: when present (> 0) an existing file is replaced (UPD),
    // otherwise a new file is inserted (INS).
    private Integer idArchivo;
    private Integer idTipoDocumento;
    private Integer idTipoArchivo;
    private String nombreArchivo;
    private String path;
}
