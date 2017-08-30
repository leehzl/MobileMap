package com.lilynlee.mobilemap.object;

import java.io.Serializable;

/**
 * Created by admin on 2016/7/29.
 */
public class ReportItem implements Serializable{
    public static final String FILE_NAME = "file_name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DESCRIBE = "describe";
    public static final String IMAGE_URI = "image_uri";
    public static final String LOCATION_NAME = "location_name";

    private String file_name;       //2016-06-12 29:32
    private String image_uri;       //绝对路径
    private String describe;        //上报描述
    private String location;        //位置信息
    private float latitude;         //位置经度
    private float longitude;        //位置维度

    public ReportItem(String file_name, String image_uri, String describe
            , String location_name, float latitude, float longitude) {
        this.file_name = file_name;
        this.image_uri = image_uri;
        this.describe = describe;
        this.location = location_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setLocation(String location){ this.location = location;}

    public void setLongitude(float longitude) { this.longitude = longitude; }

    public void setLatitude(float latitude) { this.latitude = latitude; }

    public String getImage_uri() {
        return image_uri;
    }

    public String getDescribe() {
        return describe;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getLocation() { return location; }

    public float getLatitude() {return latitude; }

    public float getLongitude() { return longitude; }
}
