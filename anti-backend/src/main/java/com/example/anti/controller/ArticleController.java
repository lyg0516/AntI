package com.example.anti.controller;

import com.example.anti.dto.ArticleDto;
import com.example.anti.form.ArticleForm;
import com.example.anti.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/article/new")
    public String article(ArticleForm articleForm){
        return "/article-form";
    }

    @ResponseBody
    @PostMapping("/article/new")
    public String postArticle(ArticleForm articleForm) throws IOException {
        articleService.postArticle(articleForm);
        return "ok";
    }

    @ResponseBody
    @GetMapping("/articles")
    public List<ArticleDto> articles(){
        return articleService.getArticles();
    }

}
