package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeedbackRequest {
    private Integer idFeedback;
    private Integer estrellas;
    private String feedback;
}
