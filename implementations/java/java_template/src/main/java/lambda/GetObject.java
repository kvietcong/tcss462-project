package lambda;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import com.amazonaws.services.s3.model.GetObjectRequest;



/**
 * Get the image from S3 bucket
 * @author Codi Chun
 */
public class GetObject {

    /**
     * For testing, can be delete later
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new GetObject();
      
    }

    /**
     * Constructor
     * @throws IOException
     */
    public GetObject() throws IOException{
        getObjectFromBucket();
    }

    /**
     * Get the image from S3 bucket and output the image to local
     * @throws IOException
     */
    private static void getObjectFromBucket() throws IOException{
        //****************** **********************************
        //TODO: SEE IF THE HARD CODE IMAGE NAME CAN BE FLEXIBLE?
        //****************************************************
        
        String bucket = "test.bucket.462-562.f22.cc";
        String key = "husky.jpeg";
        //S3Client client = S3Client.builder().build();
        AmazonS3 client = AmazonS3ClientBuilder.standard().build();
         
        // GetObjectRequest request = GetObjectRequest.builder()
        //                 .bucket(bucket)
        //                 .key(key)
        //                 .build();
        S3Object s3Object = client.getObject(new GetObjectRequest(bucket,key));

        //ResponseInputStream<GetObjectResponse> response = client.getObject(request);
        InputStream response = s3Object.getObjectContent();
        

                 
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(key));
         
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = response.read(buffer)) !=  -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
                             
        response.close();
        outputStream.close();
    }
}
