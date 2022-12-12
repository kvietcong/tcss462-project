package lambda;

import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import image.PixelImage;
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
        new GetObject("test.bucket.462-562.f22.cmu", "husky.jpeg");
    }

    String bucket;
    String key;
    static PixelImage pixelImage;

    /**
     * Constructor
     * @throws IOException
     */
    public GetObject(String theBucket, String theKey) throws IOException{
        this.bucket = theBucket;
        //"test.bucket.462-562.f22.cc";
        this.key = theKey;
        //"husky.jpeg";
        getObjectFromBucket(bucket, key);
    }

    /**
     * Get the image from S3 bucket and output the image to local
     * @throws IOException
     */
    private static void getObjectFromBucket(String bucket, String key) throws IOException{

        AmazonS3 client = AmazonS3ClientBuilder.standard().build();

        S3Object s3Object = client.getObject(new GetObjectRequest(bucket,key));

        InputStream response = s3Object.getObjectContent();
        pixelImage = PixelImage.load(response);
        response.close();

    }


}
