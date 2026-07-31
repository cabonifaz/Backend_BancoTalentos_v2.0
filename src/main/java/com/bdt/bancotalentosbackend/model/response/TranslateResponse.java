package com.bdt.bancotalentosbackend.model.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateResponse extends BaseResponse {
    /** Textos traducidos, en el MISMO orden que los de entrada. */
    private List<String> translations;

    public TranslateResponse(int code, String message, List<String> translations) {
        super(code, message);
        this.translations = translations;
    }
}
