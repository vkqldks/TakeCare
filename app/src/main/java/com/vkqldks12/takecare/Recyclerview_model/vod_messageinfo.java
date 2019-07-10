package com.vkqldks12.takecare.Recyclerview_model;

/**
 * Vod에 사용되는 채팅 데이터 클래스
 */

public class vod_messageinfo {
    String userName,message,nick;
    int chat_time;

    public vod_messageinfo(String MSG, String userName, int ChatTime, String usernick){
        this.message = MSG;
        this.userName = userName;
        this.chat_time = ChatTime;
        this.nick = usernick;
    }

    public vod_messageinfo(){
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String UserName){
        this.userName = UserName;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String Message){
        this.message = Message;
    }

    public int getChat_time(){
        return chat_time;
    }

    public void setChat_time(int Chat_Time){
        this.chat_time = Chat_Time;
    }

    public String getNick(){
        return nick;
    }

    public void setNick(String userNick){
        this.nick = userNick;
    }
}
