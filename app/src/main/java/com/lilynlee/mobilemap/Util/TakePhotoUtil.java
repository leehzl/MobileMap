package com.lilynlee.mobilemap.Util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/7/26.
 */
public class TakePhotoUtil {

    private Context mContext;

    public TakePhotoUtil(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 创建一个拍照的file，并返回图片文件的uri对象
     * @param photo_dir 图片的路径
     * @param photo_name 图片名称
     */
    public Uri createPhotoFile(String photo_dir, String photo_name){
        File image = new File(photo_dir,photo_name);
        try {
            if (image.exists()){
                image.delete();
            }
            image.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        Uri image_uri = Uri.fromFile(image);

        return image_uri;
    }

    /**
     * 获取当前的时间作为图片的名称
     * yyyyMMdd-HHmmss
     * @return 20160503-122314
     */
    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");//设置日期格式
        String currentTime = df.format(new Date(System.currentTimeMillis()))+".jpg";// new Date()为获取当前系统时间
        return currentTime;
    }

    /**
     * 获取绝对图片文件夹路径
     * 即ArcGISPhoto文件夹的路径
     * @return
     */
    public String getAbsoluteDir(){
        String absolute_dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGISPhoto";
        return absolute_dir;
    }
}
