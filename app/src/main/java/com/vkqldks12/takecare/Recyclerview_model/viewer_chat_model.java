package com.vkqldks12.takecare.Recyclerview_model;

/**
 * Created by vkqld on 2019-02-14.
 */

public class viewer_chat_model {
    String Nick, messeage;

    public viewer_chat_model(String Nick_name, String Messeage ){
        this.messeage = Messeage;
        this.Nick = Nick_name;
    }

    public viewer_chat_model(){

    }

    public String getNick(){
        return Nick;
    }

    public void setNick(String Nick_Name){
        this.Nick = Nick_Name;
    }

    public String getMesseage(){
        return messeage;
    }

    public void setMesseage(String Messege){
        this.messeage = Messege;
    }
}
