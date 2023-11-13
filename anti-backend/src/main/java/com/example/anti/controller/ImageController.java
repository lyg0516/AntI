package com.example.anti.controller;

import com.example.anti.file.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final FileStore fileStore;

    @ResponseBody
    @GetMapping("/image/{filename}")
    public String downloadImage(@PathVariable String filename) throws MalformedURLException {
        String s3URI = "https://antiimages.s3.ap-northeast-2.amazonaws.com/" +filename;
        return s3URI;
    }
}
