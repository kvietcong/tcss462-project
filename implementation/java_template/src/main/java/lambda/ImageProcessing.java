package lambda;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.amazonaws.services.lambda.runtime.Context;
import filters.FlipHorizontalFilter;
import filters.FlipVerticalFilter;
import filters.GrayscaleFilter;
import filters.SoftenFilter;
import image.PixelImage;
import saaf.Inspector;

final class FILTERS {
    public static final GrayscaleFilter GREYSCALE = new GrayscaleFilter();
    public static final SoftenFilter SOFTEN = new SoftenFilter();
    public static final FlipHorizontalFilter FLIP_H = new FlipHorizontalFilter();
    public static final FlipVerticalFilter FLIP_V = new FlipVerticalFilter();
}

/**
 * The class to process the image.
 * @author Codi Chun
 */
public class ImageProcessing {
    //static String myBucket;
    static PixelImage myImage;
    static String imagePath;
    static File imageFile;
    static String bucket;
    static String key;
    static String filter;
    static String newKey;
    static int repeats = 1;

    /*
     * ONLY FOR LOCAL TESTING
     */
    public static void main(String[] args) throws IOException {
        // bucket = "test.bucket.462-562.f22.cc";
        // key = "husky.jpeg";
        // filter = "ST";
        // downloadImage();
        // processImage();
        // uploadImage();
    }

    /**
     * lambda handler
     * @param event
     * @param context
     * @throws IOException
     */
    public static HashMap<String, Object> handleRequest(HashMap<String, String> request, Context context)  throws IOException{

        //Collect initial data.
        Inspector inspector = new Inspector();
        inspector.inspectAll();

        bucket = request.get("bucket");
        key = request.get("key");
        filter = request.get("filter");
        newKey = request.getOrDefault("newKey", filter + "-" + key);
        newKey = newKey.replace("{}", key);
        repeats = Integer.valueOf(request.getOrDefault("repeats", "1"));

        downloadImage();

        for(int i = 0; i < repeats; i++){
            processImage(newKey);
        }
        uploadImage(newKey);

        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();

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
    public static void processImage(String newKey) throws IOException {

        //filter the image
        if(filter.equals("greyscale")){
            FILTERS.GREYSCALE.filter(myImage);
        } else if(filter.equals("soften")){
            FILTERS.SOFTEN.filter(myImage);
        } else if(filter.equals("flipHorizontal")){
            FILTERS.FLIP_H.filter(myImage);
        } else if(filter.equals("flipVertical")){
            FILTERS.FLIP_V.filter(myImage);
        } else {
            throw new IllegalArgumentException("Invalid filter: " + filter);
        }

        //create new file and save the image to be that file

        //String path = System.getProperty("user.dir");
        String path = "/tmp";
        File newFile = new File(path + "/" + newKey);
        myImage.save(newFile);
    }



    // /**
    //  * Process the image by 1 chosen filter
    //  * @throws IOException
    //  */
    // public static void processImageTwo( String option , int n )throws IOException {

    //     //  by getting input from the keyboard
    //     // c
    //     //String option = "";
    //     //Scanner s = new Scanner(system.in);
    //     //option = s.nextLine();
    //     File newFile = null;
    //     //filter the image
    //     for ( int i = 0 ; i< n; i++) {

    //         switch (option) {
    //             case "greyscale":
    //                 GrayscaleFilter grayscale = new GrayscaleFilter();
    //                 grayscale.filter(myImage);
    //                 break;

    //             case "soften":
    //                 SoftenFilter soften = new SoftenFilter();
    //                 soften.filter(myImage);
    //                 break;

    //             case "flipHorizontal":
    //                 FlipHorizontalFilter flip = new FlipHorizontalFilter();
    //                 flip.filter(myImage);
    //                 break;

    //             case "flipVertical":
    //                 FlipVerticalFilter flipv = new FlipVerticalFilter();
    //                 flipv.filter(myImage);
    //                 break;
    //         }

    //         //   using a different name

    //         //  What would be the best option ?


    //     /*switch( option ){
    //         case "greyscale": case "Greyscale": GrayscaleFilter grayscale = new GrayscaleFilter();
    //             grayscale.filter(myImage);  break;

    //         case "soften": case "Soften":  SoftenFilter soften = new SoftenFilter();
    //             soften.filter(myImage);  break;

    //         case "flipHorizontal": case "Flip":  FlipHorizontalFilter flip = new FlipHorizontalFilter();
    //             flip.filter(myImage);  break;
    //     }*/
    //         //create new file and save the image to be that file
    //         String path = System.getProperty("user.dir");
    //         newFile = new File(path + "/edited.png");
    //         newFile.getParentFile().mkdirs();
    //         newFile.createNewFile();

    //     }

    //     myImage.save(newFile);
    //}


    /**
     * Upload the image to S3 bucket
     */
    public static void uploadImage(String newKey){
        System.out.println(newKey);
        new UploadObject(bucket, newKey);
    }
}
