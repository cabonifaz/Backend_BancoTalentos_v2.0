package com.bdt.bancotalentosbackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lo mínimo para dar de alta un talento desde su CV: identidad y contacto.
 *
 * No es un recorte de {@link IACVResponse}: es un contrato propio, porque la
 * carga rápida se define por lo que NO pide (experiencias, educación,
 * habilidades, idiomas, ubicación). Si algún día pide más, se amplía aquí sin
 * arrastrar el análisis completo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IACVQuickResponse {

    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    /** Tal como aparece en el CV, sólo dígitos y el prefijo si lo trae. */
    private String celular;

    private String email;
}
