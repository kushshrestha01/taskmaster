package com.ticketmasterkush.ticketmasterKush;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Service
public class S3Client {

    private AmazonS3 s3client;

    @Value("${amazon.S3.endpoint}")
    private String endpointUrl;

    @Value("${amazon.aws.S3.accesskey}")
    private String accessKey;

    @Value("${amazon.aws.S3.secretkey}")
    private String secretKey;

    @Value("${amazon.aws.bucket}")
    private String bucket;

    @Value("${amazon.S3.endpoint.resized}")
    private String resizedEndpointUrl;

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    public ArrayList<String> uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";
        String resizeUrl = "";
        ArrayList<String> result = new ArrayList<String>();
        long size = multipartFile.getSize();
        if(size > 350000) {
            try {
                String queueUrl = System.getenv("Queue");
                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody("hello world")
                        .withDelaySeconds(5);
                sqs.sendMessage(send_msg_request);

                File file = convertMultiPartToFile(multipartFile);
                String fileName = generateFileName(multipartFile);
                fileUrl = endpointUrl + "/" + fileName;
                result.add(fileUrl);
                uploadFileTos3bucket(fileName, file);
                resizeUrl = resizedEndpointUrl + "/resized-" + fileName;
                result.add(resizeUrl);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        return "Successfully deleted";
    }

}
