package com.example.filipedgb.cmovproj1.classes;

/**
 * Created by Filipe Batista on 24/10/2016.
 */

public class Voucher {


    private long id;
    private int type; // 0 - coffe / 1 - popcorn / 2 - 5% discount
    private String user_id;
    private boolean used;


    Voucher() {
        used = false;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
