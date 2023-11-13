package com.example.anti.dto;

import lombok.Data;

@Data
public class ArticleDto {
    public ArticleDto(String username, String text, String imageUrl) {
        this.username = username;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    private String username;
    private String text;
    private String imageUrl;
}
