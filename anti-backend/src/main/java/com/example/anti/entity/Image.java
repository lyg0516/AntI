package com.example.anti.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Image {

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Article article;

    private String originalFileName;
    private String storeFileName;

}
