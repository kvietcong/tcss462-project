package lambda;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
 
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class GetObject {

    public static void main(String[] args) throws IOException {
        //getObjectFromBucket();
        String bucket = "cc-image-converter";
        String key = "original/husky.jpeg";
        S3Client client = S3Client.builder().build();
        //AmazonS3 client = AmazonS3ClientBuilder
         
        GetObjectRequest request = GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
         
        ResponseInputStream<GetObjectResponse> response = client.getObject(request);
                 
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(key));
         
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = response.read(buffer)) !=  -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
                             
        response.close();
        outputStream.close();
      
    }

    // public GetObject(){

    // }

    private static void getObjectFromBucket() throws IOException{
 
    }
}
