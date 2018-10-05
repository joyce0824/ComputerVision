package com.example.g315.rgb2gray;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgIn,imgOut;
    private Bitmap bmpIn, bmpOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_RGB2gGray).setOnClickListener(this);
        findViewById(R.id.btn_ScaleBright).setOnClickListener(this);
        findViewById(R.id.btn_ChangeGamma).setOnClickListener(this);

        imgIn = findViewById(R.id.img_input);
        imgOut = findViewById(R.id.img_output);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_RGB2gGray :
                rGB2Gray();
                break;
            case R.id.btn_ScaleBright :
                scaleBright();
                break;
            case R.id.btn_ChangeGamma :
                changeGamma();
                break;
            default:
                break;
        }
    }

    private void rGB2Gray(){
        BitmapDrawable  src = (BitmapDrawable)imgIn.getDrawable();
        bmpIn = src.getBitmap();
        int width = bmpIn.getWidth();
        int height = bmpIn.getHeight();
        Bitmap.Config config = bmpIn.getConfig();
        bmpOut = Bitmap.createBitmap(width,height,config);

        double GS_RED = 0.299;
        double GS_GREEN = 0.587;
        double GS_BLUE = 0.114;

        int alpha, red, green, blue, pixel ;

        for(int x=0; x<width; x++){
            for(int y =0; y<height; y++){
                pixel = bmpIn.getPixel(x,y);
                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                //gray = 0.299*red + 0.587*green + 0.114*blue
                red = green = blue = (int)(GS_RED * red + GS_GREEN * green + GS_BLUE * blue);
                bmpOut.setPixel(x,y,Color.argb(alpha,red,green,blue));
            }
        }
        imgOut.setImageBitmap(bmpOut);
    }

    private void scaleBright(){
        BitmapDrawable  src = (BitmapDrawable)imgIn.getDrawable();
        bmpIn = src.getBitmap();
        int width = bmpIn.getWidth();
        int height = bmpIn.getHeight();
        Bitmap.Config config = bmpIn.getConfig();
        bmpOut = Bitmap.createBitmap(width,height,config);
        int red, green, blue, pixel ;

        float SCALE = 1.2f;
        int MIN = 0 ;
        int MAX = 255 ;


        for(int x=0; x<width; x++){
            for(int y =0; y<height; y++){
                pixel = bmpIn.getPixel(x,y);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                //
                red = checkRange(red,SCALE);
                green = checkRange(green,SCALE);
                blue = checkRange(blue,SCALE);

                bmpOut.setPixel(x,y,Color.argb(Color.alpha(pixel),red,green,blue));
            }
        }
        imgOut.setImageBitmap(bmpOut);
    }
     private int checkRange(int data, float scale){
        data = (int) (data * scale) ;
        if(data > 255){
            data = 255 ;
        }else if(data <0){
            data = 0 ;
        }
        return data;
     }


    private void changeGamma(){
        BitmapDrawable  src = (BitmapDrawable)imgIn.getDrawable();
        bmpIn = src.getBitmap();
        int width = bmpIn.getWidth();
        int height = bmpIn.getHeight();
        Bitmap.Config config = bmpIn.getConfig();
        bmpOut = Bitmap.createBitmap(width,height,config);

        double gamma = 0.6 ;
        int MAX_SIZE = 256 ;
        double MAX_VALUE_DBL = 255.0 ;
        int MAX_VALUE_INT = 255;
        double REVERSE = 1.0 ;

        int alpha, red, green, blue, pixel ;
        int[] gammaR = new int[MAX_SIZE];

        //gamma table
        for(int i = 0 ; i < MAX_SIZE ; i ++){
            gammaR[i] = (int) Math.min(MAX_VALUE_INT,
                    (int) (MAX_VALUE_DBL * Math.pow (i / MAX_VALUE_DBL, REVERSE / gamma) + 0.5));
        }

        for(int x=0; x<width; x++){
            for(int y =0; y<height; y++){
                pixel = bmpIn.getPixel(x,y);
                alpha = Color.alpha(pixel);

                red = gammaR[Color.red(pixel)];
                green = gammaR[Color.green(pixel)];
                blue = gammaR[Color.blue(pixel)];
                bmpOut.setPixel(x,y,Color.argb(alpha,red,green,blue));
            }
        }
        imgOut.setImageBitmap(bmpOut);
    }
}