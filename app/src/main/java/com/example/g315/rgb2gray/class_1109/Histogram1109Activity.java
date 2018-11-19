package com.example.g315.rgb2gray.class_1109;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.g315.rgb2gray.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.findContours;

public class Histogram1109Activity extends AppCompatActivity {

    private ImageView inputImg, inputImg1, outputImg, outputImg1, iv1, iv2, iv3, iv4;
    private Bitmap inputBmp, bmp1,bmp2;
    private Bitmap operation;
    //opencv
    private Size mSize0;
    private Mat mIntermediateMat;
    private Mat mMat0;
    private MatOfInt mChannels[];
    private MatOfInt mHistSize;
    private int                  mHistSizeNum = 25;
    private MatOfFloat mRanges;
    private Scalar mColorsRGB[];
    private Scalar               mColorsHue[];
    private Scalar mWhilte;
    private Point mP1;
    private Point mP2;
    private float                mBuff[];
    private static final String TAG = "IHistogram OpenCV342";

    private Button btn1,btn2,btn3 ;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:

                    Log.i(TAG,"LoaderCallbackInterface.SUCCESS");
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
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1_Click();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2_Click();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn3_Click();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                this, mLoaderCallback);
    }

    public void btn1_Click(){
        iv1 = findViewById(R.id.inputImg1);
        BitmapDrawable abmp = (BitmapDrawable)iv1.getDrawable();
        bmp1 = abmp.getBitmap();
        iv3 = findViewById(R.id.outputImg1);

        HistogramVariableInitialization();
        Histogram(bmp1,iv3);

    }

    public void btn2_Click(){
        iv2 = findViewById(R.id.inputImg2);
        BitmapDrawable abmp = (BitmapDrawable)iv2.getDrawable();
        bmp2 = abmp.getBitmap();
        iv4 = findViewById(R.id.outputImg2);

        HistogramVariableInitialization();
        Histogram(bmp2,iv4);

    }

    public void btn3_Click(){
        iv2 = findViewById(R.id.inputImg2);
        BitmapDrawable abmp = (BitmapDrawable)iv2.getDrawable();
        bmp2 = abmp.getBitmap();

        iv3 = findViewById(R.id.outputImg1);
        iv4 = findViewById(R.id.outputImg2);

        Mat sImage = new Mat();
        Utils.bitmapToMat(bmp2, sImage);
        Mat grayImage = new Mat();
        Imgproc.cvtColor(sImage, grayImage, COLOR_RGB2GRAY);
        displayMatImage(grayImage, iv3);

        Mat eqGS = new Mat();
        Imgproc.equalizeHist(grayImage, eqGS);
        displayMatImage(eqGS, iv4);
        eqGS.release();
        grayImage.release();
        sImage.release();

    }

    private void Histogram(Bitmap bmp, ImageView iv)
    {
        Bitmap bmp3 = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        int imgH = bmp3.getHeight();
        int imgW = bmp3.getWidth();
        Mat rgba = new Mat(imgH, imgW, CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, rgba);
        //do histogram
        Size sizeRgba = rgba.size();
        Mat rgbaInnerWindow;
        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;
        int left = cols / 8;
        int top = rows / 8;
        int width = cols * 3 / 4;
        int height = rows * 3 / 4;
        Mat hist = new Mat();
        int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
        if(thikness > 5) thikness = 5;
        int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
        // RGB
        for(int c=0; c<3; c++) {
            Imgproc.calcHist(Arrays.asList(rgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for(int h=0; h<mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height-1;
                mP2.y = mP1.y - 2 - (int)mBuff[h];
                Imgproc.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
            }
        }
        // Value and Hue
        Imgproc.cvtColor(rgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);
        // Value
        Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[2], mMat0, hist, mHistSize, mRanges);
        Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
        hist.get(0, 0, mBuff);
        for(int h=0; h<mHistSizeNum; h++) {
            mP1.x = mP2.x = offset + (3 * (mHistSizeNum + 10) + h) * thikness;
            mP1.y = sizeRgba.height-1;
            mP2.y = mP1.y - 2 - (int)mBuff[h];
            Imgproc.line(rgba, mP1, mP2, mWhilte, thikness);
        }
        // Hue
        Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0], mMat0, hist, mHistSize, mRanges);
        mIntermediateMat.release();
        Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
        hist.get(0, 0, mBuff);
        for(int h=0; h<mHistSizeNum; h++) {
            mP1.x = mP2.x = offset + (4 * (mHistSizeNum + 10) + h) * thikness;
            mP1.y = sizeRgba.height-1;
            mP2.y = mP1.y - 2 - (int)mBuff[h];
            Imgproc.line(rgba, mP1, mP2, mColorsHue[h], thikness);
        }
        try {
            bmp3 = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgba, bmp3);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        rgba.release();
        hist.release();
        iv.setImageBitmap(bmp3);
        iv.invalidate();
    }

    private void HistogramVariableInitialization()
    {
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };
        mWhilte = Scalar.all(255);
        mP1 = new Point();
        mP2 = new Point();
    }

    private void displayMatImage(Mat image, ImageView iv){
        Bitmap bmp = Bitmap.createBitmap(image.cols(),image.rows(), Bitmap.Config.ARGB_8888); //RGB_565

        Utils.matToBitmap(image, bmp);

        iv.setImageBitmap(bmp);


    }


}
