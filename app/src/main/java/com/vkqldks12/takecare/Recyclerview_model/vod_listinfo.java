package com.vkqldks12.takecare.Recyclerview_model;

/**
 * Vod 리스트에 사용되는 데이터 클래스
 */

public class vod_listinfo {

    String vod_thumnail;
    String vod_name;
    String vod_id;

    public vod_listinfo( String vod_name, String vod_ID){
        this.vod_thumnail = vod_thumnail;
        this.vod_name = vod_name;
        this.vod_id = vod_ID;
    }

    public vod_listinfo() {

    }

    public String getVod_thumnail(){
        return vod_thumnail;
    }

    public void setVod_thumnail(String vodThumnail){
        this.vod_thumnail = vodThumnail;
    }

    public String getVod_name(){
        return vod_name;
    }

    public void setVod_name(String vodName){
        this.vod_name = vodName;
    }

    public String getVod_id(){
        return vod_id;
    }

    public void setVod_id(String vodID){
        this.vod_id = vodID;
    }

}
