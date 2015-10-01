package ua.kiev.vignatyev.vhome1.models;

import android.graphics.Bitmap;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class Vcam {
    public String error;
    public String URL;
    public int HLS;
    public int RTMP;
    public int I_CUSTOMER;
    public int I_CUSTOMER_VCAM;
    public int ROD;
    public int ROS;
    public int ON_AIR;
    public int RESTRICTION;
    public String EMAIL;
    public String VENDOR_NAME;
    public String VCAM_NAME;
    public String SCHEDULE;
    public String CUSTOMER_VCAM_NAME;
    public String CUSTOMER_VCAM_LOGIN;
    public String CUSTOMER_VCAM_PASSWORD;
    public String TOKEN;
    public String TYPE;

    /*** OPTIONS ***/
    public int VCAM_PORT;
    public String VCAM_IP;
    public String VCAM_LOCATION;
    public String VCAM_VIDEO;
    public String UTILITY_NAME;
    public String UTIL_IN_ARGS;
    public String VCAM_PROTOCOL;
    public String VCAM_AUDIO;
    public int ROD_START_TIME;

    public Bitmap THUMBNAIL;

    /*** GETTERS ***/
    public String getError() {  return error; }
    public int getHLS() { return HLS;  }
    public int getRTMP() { return RTMP; }
    public int getI_CUSTOMER() { return this.I_CUSTOMER; }
    public int getI_CUSTOMER_VCAM() {return this.I_CUSTOMER_VCAM; }
    public int getROD() { return ROD;}
    public int getROS() { return ROS; }
    public int getON_AIR() { return ON_AIR; }
    public int getRESTRICTION() { return RESTRICTION; }
    public String getEMAIL() { return EMAIL; }
    public String getVENDOR_NAME() { return VENDOR_NAME; }
    public String getVCAM_NAME() { return VCAM_NAME; }
    public String getSCHEDULE() { return SCHEDULE; }
    public String getCUSTOMER_VCAM_NAME() { return CUSTOMER_VCAM_NAME; }
    public String getCUSTOMER_VCAM_LOGIN() { return CUSTOMER_VCAM_LOGIN; }
    public String getCUSTOMER_VCAM_PASSWORD() { return CUSTOMER_VCAM_PASSWORD; }
    public String getTOKEN() { return TOKEN; }
    public String getTYPE() { return TYPE; }
    public String getVcamURL(){
        return "http://" + URL + ":" + HLS + "/myapp/"+ TOKEN + "/index.m3u8";
    }
}
