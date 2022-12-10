package lambda;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.amazonaws.services.lambda.runtime.Context;
import filters.FlipHorizontalFilter;
import filters.GrayscaleFilter;
import filters.SoftenFilter;
import image.PixelImage;


 
/**
 * The class to process the image.
 * @author Codi Chun
 */
public class ImageProcessing {

    //static String myBucket;
    static PixelImage myImage;
    static String imagePath;
    static File imageFile;
        


    public static void main(String[] args) throws IOException {
    bucket = "test.bucket.462-562.f22.cc";
    key = "husky.jpeg"; 
    filter = "ST";
    downloadImage();
    processImage();
    uploadImage();
    }


    static String bucket;
    static String key;
    static String filter;


    /**
     * lambda handler
     * @param event
     * @param context
     * @throws IOException
     */
    public static void handleRequest(HashMap<String, String> request, Context context)  throws IOException{
        bucket = request.get("bucket");
        key = request.get("key");
        filter = request.get("filter");

        downloadImage();
        processImage();
        uploadImage();

    }

    /**
     * Download the specific image from S3 bucket
     * @throws IOException
     */
    public static void downloadImage() throws IOException {
        GetObject object = new GetObject(bucket, key);
        //File image = new File(System.getProperty("user.dir")+"/husky.jpeg");
        //File image = new File("/tmp/"+key);
        //myImage = PixelImage.load(image);
        myImage = object.pixelImage;
    }

    /**
     * Process the image by 3 filters
     * @throws IOException
     */
    public static void processImage() throws IOException {

        //filter the image
        if(filter.equals("greyscale")){
            GrayscaleFilter grayscale = new GrayscaleFilter();
            grayscale.filter(myImage);
        }

        if(filter.equals("soften")){
            SoftenFilter soften = new SoftenFilter();
            soften.filter(myImage);
        }

        if(filter.equals("flipHorizontal")){
            FlipHorizontalFilter flip = new FlipHorizontalFilter();
            flip.filter(myImage);
        }

        //create new file and save the image to be that file

        //String path = System.getProperty("user.dir");
        String path = "/tmp";
        File newFile = new File(path + "/edited-" + key);
        //newFile.getParentFile().mkdirs(); 
        //newFile.createNewFile();
        myImage.save(newFile);
    }

    
    /**
     * Upload the image to S3 bucket
     */
    public static void uploadImage(){
        String fileName = "edited-" + key;
        System.out.println(fileName);
        new UploadObject(bucket, fileName);
    }
}