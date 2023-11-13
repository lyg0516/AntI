package com.example.anti.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ImageProcessor {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @ResponseBody
    public String blurImage(String imageUrl, String imageName) throws IOException {
        BufferedImage image = null;
        try {
            URL imageURL = new URL(imageUrl);
            image = ImageIO.read(imageURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int pos = imageName.lastIndexOf(".");
        String imageNotExt = imageName.substring(0,pos + 1);
        image = adjustImageOpacity(image, 0.5f);
        uploadImageToS3(image, imageNotExt + "_blur."+extractExt(imageName),extractExt(imageName) );
        return "ok";
    }

    public String cropImage(String imageUrl, String imageName) throws IOException {
        BufferedImage image = null;
        try {
            URL imageURL = new URL(imageUrl);
            image = ImageIO.read(imageURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage subImage = null;
        int pos = imageName.lastIndexOf(".");
        String imageExt = extractExt(imageName);
        String imageNotExt = imageName.substring(0,pos);
        for (int i = 0; i < width; i += width / 2) {
            for (int j = 0; j < height; j += height / 2) {
                subImage = image.getSubimage(i, j, width / 2, height / 2);

                uploadImageToS3(subImage, imageNotExt + "_crop"+i+j+"."+imageExt, imageExt);
            }
        }
        return "ok";
    }


    public void uploadImageToS3(BufferedImage image, String imageName, String imageType) throws IOException {
        // BufferedImage를 InputStream으로 변환
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, os);
        byte[] buffer = os.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/"+imageType);
        meta.setContentLength(buffer.length);
        amazonS3Client.putObject(new PutObjectRequest(bucket, imageName, is, meta).withCannedAcl(CannedAccessControlList.PublicRead));
    }


    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private static BufferedImage adjustImageOpacity(BufferedImage image, float opacity) {
        // BufferedImage를 복제하여 새로운 이미지 생성
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);

        // Graphics2D를 사용하여 새 이미지에 원본 이미지를 그리고 투명도 조절
        Graphics2D g2d = newImage.createGraphics();
        g2d.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return newImage;
    }
}
