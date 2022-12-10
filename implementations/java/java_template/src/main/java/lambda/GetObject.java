package lambda;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import image.PixelImage;

import java.io.InputStream;

import javax.imageio.ImageIO;

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
        new GetObject("test.bucket.462-562.f22.cc", "husky.jpeg");
      
    }

    String bucket;
    String key;
    static PixelImage pixelImage;

    /**
     * Constructor
     * @throws IOException
     */
    public GetObject(String theBuket, String theKey) throws IOException{
        this.bucket = theBuket;
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
        //****************** **********************************
        //TODO: SEE IF THE HARD CODE IMAGE NAME CAN BE FLEXIBLE?
        //****************************************************
        
        //String bucket = "test.bucket.462-562.f22.cc";
        //String key = "husky.jpeg";




        //S3Client client = S3Client.builder().build();
        AmazonS3 client = AmazonS3ClientBuilder.standard().build();
         
        // GetObjectRequest request = GetObjectRequest.builder()
        //                 .bucket(bucket)
        //                 .key(key)
        //                 .build();
        S3Object s3Object = client.getObject(new GetObjectRequest(bucket,key));
        

        //ResponseInputStream<GetObjectResponse> response = client.getObject(request);
        InputStream response = s3Object.getObjectContent();
        
        

                 
        // BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(key));
         
        // byte[] buffer = new byte[4096];
        // int bytesRead = -1;
         
        // while ((bytesRead = response.read(buffer)) !=  -1) {
        //     outputStream.write(buffer, 0, bytesRead);
        // }



        //String path = System.getProperty("user.dir");
        //String path = "/tmp";
        //File newFile = new File(path + "/" + key);
        // newFile.getParentFile().mkdirs(); 
        // newFile.createNewFile();
        // File file = new File(key);
        // PixelImage test = PixelImage.load(file);
        // test.save(newFile);
        
        //BufferedImage testImage = ImageIO.read(response);
        pixelImage = PixelImage.load(response);


       // ImageIO.write(testImage, "png", newFile);

                             
        response.close();
        //outputStream.close();
    }


}