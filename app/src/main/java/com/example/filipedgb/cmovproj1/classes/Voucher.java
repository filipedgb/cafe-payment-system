package com.example.filipedgb.cmovproj1.classes;

import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

/**
 * Created by Filipe Batista on 24/10/2016.
 */

public class Voucher {

    private int type; // 0 - coffe / 1 - popcorn / 2 - 5% discount
    private String user_id;
    private boolean used;

    private String criptographic_signature;
    private int serial;
    private boolean signed;


    public Voucher() {

    }

    public Voucher(String user_id_in,int type_in) {
        this.user_id = user_id_in;
        this.type = type_in;
        this.used = false;
        this.serial = RandomUtils.nextInt(1,1000)+ RandomUtils.nextInt(100,500);
        this.signed = false;
        this.criptographic_signature = "";
    }

    public String getCriptographic_signature() {
        return criptographic_signature;
    }

    public void setCriptographic_signature(String criptographic_signature) {
        this.criptographic_signature = criptographic_signature;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }


}
