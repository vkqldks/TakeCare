package com.vkqldks12.takecare.Recyclerview_model;

/**
 * Created by vkqld on 2019-02-14.
 */

public class presenter_chat_model {
    String nick_name, messege;

    public presenter_chat_model(String nick, String messege ){
        this.nick_name = nick;
        this.messege = messege;

    }

    public presenter_chat_model(){

    }

    public String getNick_name(){
        return nick_name;
    }

    public void setNick_name(String Nick_Name){
        this.nick_name = Nick_Name;
    }

    public String getMessege(){
        return messege;
    }

    public void setMessege(String Message){
        this.messege = Message;
    }
}
