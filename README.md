# [대구를 빛내는 해커톤] AntI
### S타입: 창작자의 저작권을 보호하고 AI의 무단 데이터 수집을 방지하기 위한 온라인 플랫폼

# 프로젝트 설명
무단 크롤링을 방지하게 위해
--- 기능 사용

# 프로젝트 활용된 기술

## [기술 스택]
백엔드 - 스프링부트, 스프링 시큐리티, 스프링 데이터 JPA, Amazon S3




## [ERDigram]
![image](https://github.com/NinkatS/anti/assets/91305949/091aac0a-4d6f-4c06-b2b9-65e57d34ef08)

<br/>

## [Bearer 방식 JWT 로그인] 
![image](https://github.com/NinkatS/anti/assets/91305949/a1bbd5e3-e28f-4cf6-a0dc-6b304ccc034e)
Bearer 방식의 JWT 토큰을 이용해 로그인을 구현했습니다.

1. 회원가입
클라이언트가 최초에 회원가입을 요청하면 서버는 database에 회원의 id와 BCryptPasswordEncoder로 인코딩된 password를 데이터베이스에 저장합니다.

2. 로그인
클라이언트가 로그인시 id와 password를 서버에 전달하면 서버에서 password를 BCryptPasswordEncoder로
인코딩후 데이터베이스의 유저 id, password와 비교해 검증합니다.
검증 완료 후, 유저정보를 jwt토큰으로 변환한 후 클라이언트에게 반환합니다.

3. 인증
클라이언트가 로그인 후 클라이언트가 request를 하면 header에 jwt토큰을 담아서 요청을 진행하게 되며 서버는
클라이언트의 jwt토큰을 검증 후 response를 반환합니다.
<br/>


## [AWS S3 이미지서버]

![image](https://github.com/NinkatS/anti/assets/91305949/9807a9fd-2e27-407a-b474-11f7b08d1501)


1. canvas태그를 이용해 이미지를 자바스크립트로 동적으로 렌더링하기위해서는 크롭된 이미지가 필요하다. 그에 따라 많은 이미지를 저장할 있는 대용량의 서버가 필요합니다.
2. 이미지를 렌더링하기 위해 이미지의 url을 관리해주는 서버가 필요합니다.

위와 같은 필요로 이미지 서버의 필요성을 느꼈습니다. 그래서 저희는 Amazon Web Service S3 기능을 활용해 이미지 서버를 구현했습니다.
저희는 canvas에서 크롭된 이미지를 사용하므로 이미지를 크롭하기 위해 다음과 같은 로직을 이용했습니다.
1. 최초의 기본 이미지를 AWS S3에 업로드합니다.
2. S3에 업로드된 이미지를 uri를 이용해 BufferedImage로 로드합니다.
3. 로드된 BufferedImage를 자바 awt를 이용해 크롭하고 크롭된이미지를 AWS S3에 저장합니다.

build.gradle dependency추가
```
    implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'
```

application.yml 추가
```
    cloud:
  aws:
    s3:
      bucket: antiimages
    stack:
      auto: false
    credentials:
      access-key: {access-key}
      secret-key: {secret-key}
    region:
      static: ap-northeast-2

```
AwsS3Config
```
   @Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}

```
AWS S3 이미지 저장
```
    ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(imageFile.getContentType());;
        objectMetadata.setContentLength(imageFile.getSize());
        amazonS3Client.putObject(bucket,storeFileName,imageFile.getInputStream(),objectMetadata);
        imageProcessor.cropImage(getS3URI(image.getStoreFileName()), image.getStoreFileName());
        imageProcessor.blurImage(getS3URI(image.getStoreFileName()), image.getStoreFileName());
```
