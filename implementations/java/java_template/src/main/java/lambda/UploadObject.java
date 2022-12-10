package lambda;

import java.io.File;
import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;



/**
 * The class to upload an image to S3 bucket
 * @author Codi Chun
 */
public class UploadObject {

    public static void main(String[] args) throws IOException {
        //UploadObjectToBucket();
    }

    String bucket;

    /**
     * Constructor
     */
    public UploadObject(String theBucket, String fileName){
        bucket = theBucket;
        UploadObjectToBucket(bucket, fileName);

    }

    /**
     * The method to upload the image to bucket
     */
    public static void UploadObjectToBucket(String bucketName, String fileName){

        String filePath = "/tmp" + "/" + fileName;
        //String filePath = System.getProperty("user.dir") + "/" + fileName;
        System.out.println(filePath);
        String key = fileName;
        AmazonS3 client = AmazonS3ClientBuilder.standard().build();
        client.putObject(bucketName, key, new File(filePath));

        //TODO: SEE IF WE COULD ADD A WAITER HERE??
         
        System.out.println("File " + fileName + " was uploaded.");     
    }
}

