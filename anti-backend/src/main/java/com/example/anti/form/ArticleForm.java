package com.example.anti.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ArticleForm {
    private String username;
    private String text;
    private MultipartFile image;
}
