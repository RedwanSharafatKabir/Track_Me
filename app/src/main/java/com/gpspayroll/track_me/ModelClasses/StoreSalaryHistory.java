package com.gpspayroll.track_me.ModelClasses;

public class StoreSalaryHistory {
    private String userPhone;
    private String username;
    private String workhour;
    private String remuneration;
    private String checkin;
    private String checkout;
    private String status;

    public StoreSalaryHistory(String userPhone, String username, String workhour, String remuneration, String checkin, String checkout, String status) {
        this.userPhone = userPhone;
        this.username = username;
        this.workhour = workhour;
        this.remuneration = remuneration;
        this.checkin = checkin;
        this.checkout = checkout;
        this.status = status;
    }

    public StoreSalaryHistory() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
