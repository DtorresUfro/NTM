package com.Ntm;
import java.util.Date;

public class Notification {
    private String message;
    private boolean read = false;
    private Date createdAd;

    public Notification(String message, boolean read, Date createdAd) {
        this.message = message;
        this.read = read;
        this.createdAd = new Date();
    }

    //marcada si esta leida

    public void markAsRead(){
        this.read = true;
    }
}
