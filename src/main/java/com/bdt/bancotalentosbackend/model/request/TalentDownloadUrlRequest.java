package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentDownloadUrlRequest {
    private Integer idFile;
    /**
     * Si es {@code true}, la URL pre-firmada se genera para visualización inline
     * (visor de PDF/imagen). Si es {@code false} (por defecto), se conserva el
     * comportamiento original (descarga).
     */
    private boolean inline;
}
