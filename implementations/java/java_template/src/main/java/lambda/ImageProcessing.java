package lambda;

import java.io.File;
import java.io.IOException;

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

    static String myBucket;
    static PixelImage myImage;
    static String imagePath;
    static File imageFile;
        


    public static void main(String[] args) throws IOException {
    downloadImage();
    processImage();
    uploadImage();
    }

    /**
     * lambda handler
     * @param event
     * @param context
     * @throws IOException
     */
    public static void handleRequest(Object event, Context context)  throws IOException{


        downloadImage();
        processImage();
        uploadImage();

    }

    /**
     * Download the specific image from S3 bucket
     * @throws IOException
     */
    public static void downloadImage() throws IOException {
        new GetObject();
        File image = new File(System.getProperty("user.dir")+"/husky.jpeg");
        myImage = PixelImage.load(image);
    }

    /**
     * Process the image by 3 filters
     * @throws IOException
     */
    public static void processImage()throws IOException {

        //filter the image
        GrayscaleFilter grayscale = new GrayscaleFilter();
        grayscale.filter(myImage);

        SoftenFilter soften = new SoftenFilter();
        soften.filter(myImage);

        FlipHorizontalFilter flip = new FlipHorizontalFilter();
        flip.filter(myImage);

        //create new file and save the image to be that file
        String path = System.getProperty("user.dir");
        File newFile = new File(path + "/edited.png");
        newFile.getParentFile().mkdirs(); 
        newFile.createNewFile();
        myImage.save(newFile);
    }

    
    /**
     * Upload the image to S3 bucket
     */
    public static void uploadImage(){
        new UploadObject();


    }







}
