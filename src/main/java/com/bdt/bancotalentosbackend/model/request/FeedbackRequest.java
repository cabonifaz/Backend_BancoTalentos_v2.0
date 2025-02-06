package com.bdt.bancotalentosbackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    @JsonProperty()
    private Integer idTalento;
    @JsonProperty()
    private Integer idFeedback;
    private Integer estrellas;
    private String feedback;
}
