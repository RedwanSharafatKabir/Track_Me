package com.gpspayroll.track_me.ModelClasses;

public class StoreEmployeeData {
    private String username, userPhone, userEmail, userNid, userAddress;

    public StoreEmployeeData(String username, String userPhone, String userEmail, String userNid, String userAddress) {
        this.username = username;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userNid = userNid;
        this.userAddress = userAddress;
    }

    public StoreEmployeeData() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNid() {
        return userNid;
    }

    public void setUserNid(String userNid) {
        this.userNid = userNid;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
