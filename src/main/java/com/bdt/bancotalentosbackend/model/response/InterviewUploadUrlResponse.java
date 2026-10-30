package com.bdt.bancotalentosbackend.model.response;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class InterviewUploadUrlResponse {

    private String url;
    private String path;
    private String fileName;
    
}