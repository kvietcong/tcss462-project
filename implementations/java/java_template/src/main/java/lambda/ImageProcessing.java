package lambda;

import filters.FlipHorizontalFilter;
import filters.GrayscaleFilter;
import filters.SoftenFilter;
import image.PixelImage;
 
/**
 * 
 * @author Codi Chun
 */
public class ImageProcessing {
        


    public static void handleRequest(){
        PixelImage image = downloadImage();
        processImage(image);

    }

    public static PixelImage downloadImage(){
        


    }

    public static void processImage(PixelImage theImage){

        //open the image

        //filter the image
        GrayscaleFilter grayscale = new GrayscaleFilter();
        grayscale.filter(theImage);

        SoftenFilter soften = new SoftenFilter();
        soften.filter(theImage);

        FlipHorizontalFilter flip = new FlipHorizontalFilter();
        flip.filter(theImage);

        //save the image

    }

    

    public static void uploadImage(String fileName, String bucket, String ObjName){



    }







}
