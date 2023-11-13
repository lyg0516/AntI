package com.example.anti.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.anti.dto.ArticleDto;
import com.example.anti.file.FileStore;
import com.example.anti.file.ImageProcessor;
import com.example.anti.form.ArticleForm;
import com.example.anti.entity.Article;
import com.example.anti.entity.Image;
import com.example.anti.entity.User;
import com.example.anti.repository.ArticleRepository;
import com.example.anti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final FileStore fileStore;
    private final AmazonS3Client amazonS3Client;
    private final ImageProcessor imageProcessor;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void postArticle(ArticleForm articleForm) throws IOException {
        String username = articleForm.getUsername();
        User user = userRepository.findByUsername(username);
        String text = articleForm.getText();
        MultipartFile imageFile = articleForm.getImage();
        Article article = new Article();
        article.setUser(user);
        article.setText(text);
        //Image image = fileStore.storeFile(imageFile);
        Image image = new Image();
        image.setOriginalFileName(imageFile.getOriginalFilename());
        String storeFileName = FileStore.createStoreFileName(imageFile.getOriginalFilename());
        image.setStoreFileName(storeFileName);


        article.setImage(image);
        articleRepository.save(article);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(imageFile.getContentType());;
        objectMetadata.setContentLength(imageFile.getSize());
        amazonS3Client.putObject(bucket,storeFileName,imageFile.getInputStream(),objectMetadata);
        imageProcessor.cropImage(getS3URI(image.getStoreFileName()), image.getStoreFileName());
        imageProcessor.blurImage(getS3URI(image.getStoreFileName()), image.getStoreFileName());
    }

    public List<ArticleDto> getArticles(){
        ArrayList<ArticleDto> dtos = new ArrayList<>();
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            dtos.add(new ArticleDto(article.getUser().getUsername(),
                    article.getText(), article.getImage().getStoreFileName()));
        }
        return dtos;
    }

    private String getS3URI(String filename){
        return "https://antiimages.s3.ap-northeast-2.amazonaws.com/" +filename;
    }

}
