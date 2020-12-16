package com.example.minglishmantra_beta.Modal;

public class CommentModal {

    String doubtText,sender,sender_image,time;


    public CommentModal() {
    }

    public CommentModal(String doubtText, String sender, String time ,String sender_image) {
        this.doubtText = doubtText;
        this.sender = sender;
        this.time = time;
        this.sender_image =sender_image;
    }

    public String getDoubtText() {
        return doubtText;
    }

    public void setDoubtText(String doubtText) {
        this.doubtText = doubtText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }
}
