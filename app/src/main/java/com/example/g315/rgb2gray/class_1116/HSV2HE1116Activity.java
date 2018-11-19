package com.example.g315.rgb2gray.class_1116;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.g315.rgb2gray.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class HSV2HE1116Activity extends AppCompatActivity {

    private Bitmap bmp1, bmp2;
    private ImageView  iv1, iv2,iv3, iv4;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram1109);
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b4_Click();
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b5_Click();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                this, mLoaderCallback);
    }

    public void b4_Click() {
        iv2 = (ImageView) findViewById(R.id.inputImg1);
        BitmapDrawable abmp = (BitmapDrawable) iv2.getDrawable();
        bmp2 = abmp.getBitmap();
        iv4 = (ImageView) findViewById(R.id.outputImg1);
        Mat colorImage = new Mat();

        Utils.bitmapToMat(bmp2, colorImage);
        Mat V = new Mat(colorImage.rows(), colorImage.cols(), CvType.CV_8UC1);
        Mat S = new Mat(colorImage.rows(), colorImage.cols(), CvType.CV_8UC1);
        Mat HSV = new Mat();
        Imgproc.cvtColor(colorImage, HSV, Imgproc.COLOR_RGB2HSV);
        //Now,we convert the RGB image to the HSV color space: 
        byte[] Vs = new byte[3];
        byte[] vsout = new byte[1];
        byte[] ssout = new byte[1];
        for (int i = 0; i < HSV.cols(); i++) {
            for (int j = 0; j < HSV.cols(); j++) {
                HSV.get(i, j, Vs);
                V.put(i, j, new byte[]{Vs[2]});
                S.put(i, j, new byte[]{Vs[1]});
            }
        }
        //Then,we access the image pixel by pixel to copy the saturation and value channels: 
        // Call Imgproc.equalizeHist() to enhance the value and saturation channels: 
        Imgproc.equalizeHist(V, V);
        Imgproc.equalizeHist(S, S);
        //Now,wecopy the enhanced saturation and value back to the original image: 
        for (int i = 0; i < HSV.rows(); i++) {
            for (int j = 0; j < HSV.cols(); j++) {
                V.get(i, j, vsout);
                S.get(i, j, ssout);
                HSV.get(i, j, Vs);
                Vs[2] = vsout[0];
                Vs[1] = ssout[0];
                HSV.put(i, j, Vs);
            }
        }
        //Finally,we convert the HSV color space to RGB and display the enchanced image:
        Mat enhancedImage = new Mat();
        Imgproc.cvtColor(HSV, enhancedImage, Imgproc.COLOR_HSV2BGR);
        displayMatImage(enhancedImage, iv4);
    }

    private void displayMatImage(Mat image, ImageView iv) {
        Bitmap bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888); //RGB_565

        Utils.matToBitmap(image, bmp);

        iv.setImageBitmap(bmp);


    }

    public void b5_Click() {
        iv1 = (ImageView) findViewById(R.id.inputImg1);
        BitmapDrawable abmp = (BitmapDrawable)
                iv1.getDrawable();
        bmp1 = abmp.getBitmap();
        iv3 = (ImageView) findViewById(R.id.outputImg1);
        Bitmap rgbHE = YUVHE(bmp1);
        iv3.setImageBitmap(rgbHE);
    }

    private Bitmap YUVHE(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap processedImage = Bitmap.createBitmap(width, height, src.getConfig());
        int A = 0, R, G, B;
        int pixel;
        float[][] Y = new float[width][height];
        float[][] U = new float[width][height];
        float[][] V = new float[width][height];
        int[] histogram = new int[256];
        Arrays.fill(histogram, 0);
        int[] cdf = new int[256];
        Arrays.fill(cdf, 0);
        float min = 257;
        float max = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                //convert to YUV 
                Y[x][y] = 0.299f * R + 0.587f * G + 0.114f * B;
                U[x][y] = 0.565f * (B - Y[x][y]);
                V[x][y] = 0.713f * (R - Y[x][y]);

                //create a histogram 
                histogram[(int) Y[x][y]] += 1;
                //get min and max values 
                if (Y[x][y] < min) {
                    min = Y[x][y];
                }
                if (Y[x][y] > max) {
                    max = Y[x][y];
                }
            }
        }
        cdf[0] = histogram[0];
        for (int i = 1; i <= 255; i++){
            cdf[i] = cdf[i - 1] + histogram[i];
        }
        float minCDF = cdf[(int) min];
        float denominator = width * height - minCDF;
        float value;

        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                Y[x][y] = ((cdf[(int) Y[x][y]] - minCDF) / (denominator)) * 255;
                value = Y[x][y] + 1.403f * V[x][y];
                if (value < 1.1) R = 0;
                else if (value > 255.0) R = 255;
                else R = (int) value;
                value = Y[x][y] - 0.344f * U[x][y] - 0.714f * V[x][y];
                if (value < 0.0) G = 0;
                else if (value > 255.0) G = 255;
                else G = (int) value;
                value = Y[x][y] + 1.77f * U[x][y];
                if (value < 0.0) B = 0;
                else if (value > 255.0) B = 255;
                else B = (int) value;
                processedImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return processedImage;
    }
}
