package com.gpspayroll.track_me.ModelClasses;

public class StoreEmployees {
    private String username;
    private String checkin;
    private String checkout;
    private String workhour;
    private String remuneration;
    private String userPhone;
    private String employeeLocation;

    public StoreEmployees(String username, String checkin, String checkout, String workhour,
                          String remuneration, String userPhone, String employeeLocation) {
        this.username = username;
        this.checkin = checkin;
        this.checkout = checkout;
        this.workhour = workhour;
        this.remuneration = remuneration;
        this.userPhone = userPhone;
        this.employeeLocation = employeeLocation;
    }

    public StoreEmployees() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public String getWorkhour() {
        return workhour;
    }

    public void setWorkhour(String workhour) {
        this.workhour = workhour;
    }

    public String getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(String remuneration) {
        this.remuneration = remuneration;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getEmployeeLocation() {
        return employeeLocation;
    }

    public void setEmployeeLocation(String employeeLocation) {
        this.employeeLocation = employeeLocation;
    }
}
