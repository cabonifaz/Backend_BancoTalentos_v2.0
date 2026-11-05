package com.bdt.bancotalentosbackend.model.request;

import java.util.List;

import com.bdt.bancotalentosbackend.model.dto.InterviewQuestionDTO;

import lombok.Data;

/**
 * Alta de las respuestas de una entrevista: llegan en bloque, como los
 * entrevistadores, porque el formulario las arma como filas.
 */
@Data
public class InterviewQuestionsRequest {

    private Integer idEntrevista;
    private List<InterviewQuestionDTO> preguntas;
}
