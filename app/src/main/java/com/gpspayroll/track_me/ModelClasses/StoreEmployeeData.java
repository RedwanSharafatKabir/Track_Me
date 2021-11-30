package com.gpspayroll.track_me.ModelClasses;

public class StoreEmployeeData {
    String username, userPhone, userEmail;

    public StoreEmployeeData(String username, String userPhone, String userEmail) {
        this.username = username;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
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
}
