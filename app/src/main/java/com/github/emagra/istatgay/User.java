package com.github.emagra.istatgay;

/**
 * Created by caccola on 08/08/2017.
 */

public class User {

    private String sex;
    private boolean status;

    public User() {
    }

    public User(String sex, boolean status) {
        this.sex = sex;
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
