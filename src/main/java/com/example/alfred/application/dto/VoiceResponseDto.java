package com.example.alfred.application.dto;

import lombok.Builder;

@Builder
public class VoiceResponseDto {
    private String status;
    private String clipUrl;
    private String message;

    public VoiceResponseDto(String status, String clipUrl, String message) {
        this.status = status;
        this.clipUrl = clipUrl;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getClipUrl() {
        return clipUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setClipUrl(String clipUrl) {
        this.clipUrl = clipUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}