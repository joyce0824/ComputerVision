# ComputerVision
ComputerVision_HW

0928 

1.rGB2Gray() : 照片轉為灰階 
   Gray = 0.299*red + 0.587*green + 0.114*blue
  
2.scaleBright() : 照片變亮
   float SCALE = 1.2f;
   colors = (int) (colors * SCALE) ; // colors include red, green, blue 
   255 >= colors_Range >= 0
   
3.changeGamma() : 照片變暗
    // set a gamma table
    gammaR[i] = (int) Math.min(255, (255 * Math.pow (i / 255, 1 / gamma) + 0.5))
