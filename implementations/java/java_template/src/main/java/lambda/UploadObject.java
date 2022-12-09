package lambda;



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

        //String bucketName = "test.bucket.462-562.f22.cc";
        //String folderName = "photos";
         
        //String fileName = "edited.png";
        //String filePath = System.getProperty("user.dir") + "/" + fileName;
        String filePath = "/tmp" + "/" + fileName;
        System.out.println(filePath);
        String key = fileName;
         
        //S3Client client = S3Client.builder().build();
        AmazonS3 client = AmazonS3ClientBuilder.standard().build();
         
        // PutObjectRequest request = PutObjectRequest.builder()
        //                 .bucket(bucketName)
        //                 .key(key)
        //                 //.acl("public-read")
        //                 .build();
        
         
        // client.putObject(request, RequestBody.fromFile(new File(filePath)));
        client.putObject(bucketName, key, filePath);

        //TODO: SEE IF WE COULD ADD A WAITER HERE??
         
        // S3Waiter waiter = client.waiter();
        // HeadObjectRequest requestWait = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
         
        // WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectExists(requestWait);
         
        // waiterResponse.matched().response().ifPresent(System.out::println);
         
        System.out.println("File " + fileName + " was uploaded.");     
    }
}


