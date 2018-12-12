package com.example.g315.rgb2gray.class_1116;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g315.rgb2gray.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.g315.rgb2gray.class_1116.HomeActivity.ADAPTIVE_THRESHOLD;
import static com.example.g315.rgb2gray.class_1116.HomeActivity.OTSU_THRESHOLD;
import static com.example.g315.rgb2gray.class_1116.HomeActivity.REGION_LABELING;

public class MeanBlur1116Activity extends AppCompatActivity {

    public static final String TAG = "OpenCV";
    private final int SELECT_PHOTO =1;
    private ImageView ivImage, ivImageProcessed;
    Mat src,src_gray,dilateMat,erosionMat;
    static int ACTION_MODE =0 ;
    static int REQUEST_READ_EXTERNAL_STORAGE=0;
    static boolean read_external_storage_granted=false;

    //Initialize OpenCV manager. 
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback (this){
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV SUCCESS!");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.d(TAG,"FAIL");
                    break;
            }
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mean_blur1116);

        //Add a left arrow on Action Bar to return to the previous Activity
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImageProcessed = (ImageView) findViewById(R.id.ivImageProcessed);

        checkPermission();


    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_load_image &&
                read_external_storage_granted){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            return true ;
        }else if(!read_external_storage_granted){
            Log.i("onOptionsItemSelected","read_external_storage_granted denied");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode){
            case  SELECT_PHOTO :
                if(resultCode == RESULT_OK &&
                        read_external_storage_granted){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);

                        Utils.bitmapToMat(selectedImage, src);
                        src_gray = new Mat(selectedImage.getHeight() , selectedImage.getWidth(), CvType.CV_8UC1);

                        switch (ACTION_MODE){
                            // class_1116 hw_3 模糊化
                            case HomeActivity.MEAN_BLUR :
                                Imgproc.blur(src,src,new Size(9,9));
                                break;

                            // class_1123
                            case HomeActivity.GAUSSIAN_BLUR :  //模糊
                                gaussian();
                                break;

                            case HomeActivity.MEDIAN_BLUR: //去雜質
                                median_blur();
                                break;

                            case HomeActivity.SHARPEN: //銳利
                                sharpen();
                                break;

                            case HomeActivity.THRESHOLD: //將前景跟背景切開
                                median_blur();
                                sharpen();
                                threshold();
                                break;

                            //class_1207
                            case ADAPTIVE_THRESHOLD:
                                setAptive();
                                break;

                            case OTSU_THRESHOLD:
                                setOtsu();
                                break;

                            case REGION_LABELING:
                                setReginLabling();
                                break;


                            default:
                                break;



                        }

                        Bitmap processedImage = Bitmap.createBitmap(
                                src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                        Log.i("imageType", CvType.typeToString(src.type())+"");

                        ivImage.setImageBitmap(selectedImage);
                        Utils.matToBitmap(src, processedImage);
                        ivImageProcessed.setImageBitmap(processedImage);
                        Log.i("process","process down");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }

    }

    private void gaussian() {
        Imgproc.GaussianBlur(src, src, new Size(29,29),0);
    }

    private void median_blur() {
        Imgproc.medianBlur(src, src ,9);
    }

    private void threshold() {
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
        Mat bin = new Mat(src_gray.rows() , src_gray.cols(), CvType.CV_8UC1);
        Imgproc.threshold(src_gray, bin, 80, 190, Imgproc.THRESH_BINARY);
        Imgproc.cvtColor(bin, src, Imgproc.COLOR_GRAY2RGBA, 4);
    }

    private void sharpen() {
        Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
        kernel.put(0,0,0, -1,0,-1 ,5,-1,0 ,-1,0);
        Imgproc.filter2D(src, src, src_gray.depth(), kernel);
    }

    private void setAptive(){
        Imgproc.cvtColor(src,src_gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(src_gray,src_gray,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,59,0); //blockSize 要單數
        Imgproc.cvtColor(src_gray,src,Imgproc.COLOR_GRAY2RGBA,4);
    }

    private void setOtsu(){
        Imgproc.cvtColor(src,src_gray,Imgproc.COLOR_BGR2GRAY);
        double threshold=Imgproc.threshold(src_gray,src_gray,0,255,Imgproc.THRESH_OTSU);
        Imgproc.cvtColor(src_gray,src,Imgproc.COLOR_GRAY2RGBA,4);

        TextView tv1=findViewById(R.id.textView1);
        tv1.setText("Thresold Value="+ threshold);
    }

    private void setReginLabling(){
        Imgproc.cvtColor(src,src_gray,Imgproc.COLOR_BGR2GRAY);
        Mat bin1 = new Mat(src_gray.rows(), src_gray.cols(),CvType.CV_8UC1);
        Imgproc.threshold(src_gray,bin1,111,233,Imgproc.THRESH_BINARY_INV);

        //finding contours
        List<MatOfPoint>contourListTemp=new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bin1,contourListTemp,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        //now iterate over all top level contours
        for (int idx=0;idx>=0;idx=(int)hierarchy.get(0,idx)[0]){
            MatOfPoint matOfPoint=contourListTemp.get(idx);
            Rect rect=Imgproc.boundingRect(matOfPoint);
            Imgproc.rectangle(src,new Point(rect.x,rect.y),new Point(rect.x+((Rect) rect).width,rect.y+rect.height),new Scalar(255,0,0,255),5);
        }

    }


    private void checkPermission() {
        String log = "checkPermission";
        Intent intent = getIntent();

        if (intent.hasExtra("ACTION_MODE")) {
            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);
        }

        if (ContextCompat.checkSelfPermission(MeanBlur1116Activity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PERMISSION_GRANTED) {
            Log.i(log, "requestREAD_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(MeanBlur1116Activity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }else{
            Log.i(log, "READ_ EXTERNAL_STORAGE already granted");
            read_external_storage_granted = true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == REQUEST_READ_EXTERNAL_STORAGE){
           if(grantResults.length >0 &&
                   grantResults[0] == PERMISSION_GRANTED){
               Log.i("permission", "REQUEST_READ_EXTERNAL_STORAGE granted");
               read_external_storage_granted = true ;
           }else {
               Log.i("permission", "REQUEST_READ_EXTERNAL_STORAGE refused");
               read_external_storage_granted = false ;
           }
       }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
