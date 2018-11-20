# ComputerVision
ComputerVision_HW

<< 0928 >> 

1.rGB2Gray() : 照片轉為灰階 

Gray = 0.299*red + 0.587*green + 0.114*blue
  
2.scaleBright() : 照片變亮

float SCALE = 1.2f;
colors = (int) (colors * SCALE) ; // colors include red, green, blue 
255 >= colors_Range >= 0
   
3.changeGamma() : 照片變暗

set a gamma table
gammaR[i] = (int) Math.min(255, (255 * Math.pow (i / 255, 1 / gamma) + 0.5))

# 11/09
直方圖： 
bay image : 對比影像很差的時候

HSV :
1:準備投票箱，整張影像都跑過一遍 
2:x,y
3:
4:求機率密度函數，

直方等化 （HE）： 非線性轉換

出現的機率 ＝ 
出現的次數除以總面積 n0 / n

缺點：wash out 

constrast-limited : 取百分比，例如：0.1以下保留，0.1以上做直方圖
太暗的黑色，不處理

-----------------
區部區域做直方等化



# 1116

#### 1. 
#### 2. 
#### 3.CV3 Mean blur 
 影像有很多資訊，有時候會先將彩色轉成灰階，灰階在做一些前處裡
 前處理包含 
 blurring(影像模糊)，
 de-noising(去除雜訊)，
 sharpening(銳化)，
 erosion(斷開) and dilation(長胖接在一起)，
 thresholding(二值化) and adaptive thresholding(局部二值化) 
 
 openCV : 輸入bmp影像，轉成openCV影像格式，做好處裡後，再將openCV影像格式，轉成bmp影像
 
 影像是一個2D，用for loop 掃每個點
 
 openCV 用 Mat object stored images (use rows and cols)
 
 openCV :
 convolution : 平均模糊，使用線性濾波器，sliding window, kernel （Mask）是一個3*3的單位，每個像素都＊1相加後 / 9  ,計算後放到ancerl的地方，往右移動一單位繼續運算，
 
 
 
 