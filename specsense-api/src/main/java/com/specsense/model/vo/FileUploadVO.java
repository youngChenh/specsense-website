package com.specsense.model.vo;

import lombok.Data;

@Data
public class FileUploadVO {
    private String url;
    private String originalFilename;

    public FileUploadVO(String url, String originalFilename) {
        this.url = url;
        this.originalFilename = originalFilename;
    }
}
