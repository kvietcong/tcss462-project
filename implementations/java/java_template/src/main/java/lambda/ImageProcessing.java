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
    public static HashMap<String, Object> handleRequest(HashMap<String, String> request, Context context)  throws IOException{
                //Collect initial data.
                Inspector inspector = new Inspector();
                inspector.inspectAll();




        bucket = request.get("bucket");
        key = request.get("key");
        filter = request.get("filter");

        downloadImage();
        processImage();
        uploadImage();

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
     * Process the image by 1 chosen filter
     * @throws IOException
     */
    public static void processImageTwo( String option , int n )throws IOException {

        //  by getting input from the keyboard
        // c
        //String option = "";
        //Scanner s = new Scanner(system.in);
        //option = s.nextLine();
        File newFile = null;
        //filter the image
        for ( int i = 0 ; i< n; i++) {

            switch (option) {
                case "greyscale":
                    GrayscaleFilter grayscale = new GrayscaleFilter();
                    grayscale.filter(myImage);
                    break;

                case "soften":
                    SoftenFilter soften = new SoftenFilter();
                    soften.filter(myImage);
                    break;

                case "flipHorizontal":
                    FlipHorizontalFilter flip = new FlipHorizontalFilter();
                    flip.filter(myImage);
                    break;

                case "flipVertical":
                    FlipVerticalFilter flipv = new FlipVerticalFilter();
                    flipv.filter(myImage);
                    break;
            }

            //   using a different name

            //  What would be the best option ?


        /*switch( option ){
            case "greyscale": case "Greyscale": GrayscaleFilter grayscale = new GrayscaleFilter();
                grayscale.filter(myImage);  break;

            case "soften": case "Soften":  SoftenFilter soften = new SoftenFilter();
                soften.filter(myImage);  break;

            case "flipHorizontal": case "Flip":  FlipHorizontalFilter flip = new FlipHorizontalFilter();
                flip.filter(myImage);  break;
        }*/
            //create new file and save the image to be that file
            String path = System.getProperty("user.dir");
            newFile = new File(path + "/edited.png");
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();

        }
        
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