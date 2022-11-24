// package lambda;

// import java.io.FileOutputStream;

// import com.amazonaws.auth.PropertiesCredentials;
// import com.amazonaws.services.lambda.runtime.Context;
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.AmazonS3Client;
// import com.amazonaws.services.s3.model.GetObjectRequest;
// import com.amazonaws.services.s3.model.S3Object;
// import com.amazonaws.services.s3.model.S3ObjectInputStream;
// import com.amazonaws.util.IOUtils;

// import filters.FlipHorizontalFilter;
// import filters.GrayscaleFilter;
// import filters.SoftenFilter;
// import image.PixelImage;
 
// /**
//  * 
//  * @author Codi Chun
//  */
// public class ImageProcessing {

//     static String myBucket;
//     static String myImage;
        


//     public static void handleRequest(Object event, Context context){
//         myBucket = "cc-image-converter";
//         PixelImage image = downloadImage(event);
//         processImage(image);

//     }

//     public static PixelImage downloadImage(Object event){
//         // String existingBucketName = "<your Bucket>";
//         // String keyName = "/"+"";
        
//         // PropertiesCredentials p = new PropertiesCredentials(ImageProcessing.class.getResourceAsStream("AwsCredentials.properties"));
//         // AmazonS3 s3Client = new AmazonS3Client(new PropertiesCredentials(
//         //   ImageProcessing.class
//         //     .getResourceAsStream("AwsCredentials.properties")));
        
//         // GetObjectRequest request = new GetObjectRequest(existingBucketName,
//         //   keyName);
//         // S3Object object = s3Client.getObject(request);
//         // S3ObjectInputStream objectContent = object.getObjectContent();
//         // IOUtils.copy(objectContent, new FileOutputStream("D://upload//test.jpg"));
      
//         S3Object s3object = s3Client.getObject(myBucket, "picture/pic.png");
//         S3ObjectInputStream inputStream = s3object.getObjectContent();
//         FileUtils.copyInputStreamToFile(inputStream, new File("/Users/user/Desktop/hello.txt"));

//         return null;

//     }

//     public static void processImage(PixelImage theImage){

//         //filter the image
//         GrayscaleFilter grayscale = new GrayscaleFilter();
//         grayscale.filter(theImage);

//         SoftenFilter soften = new SoftenFilter();
//         soften.filter(theImage);

//         FlipHorizontalFilter flip = new FlipHorizontalFilter();
//         flip.filter(theImage);

//         //save the image

//     }

    

//     public static void uploadImage(String fileName, String bucket, String ObjName){


//     }







// }
