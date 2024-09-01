package com.superdrycleaners.drycleaners.beans;

public class CouponBannerModel {
    private String id;
    private String couponImage;
    private String couponCode;
    private String amount;
    private String exp_days;
    private String coupon_type;
    private String user_code;
    private String c_type;
    private String status;
    private String installation_date;

    public CouponBannerModel() {
        this.id = id;
        this.couponImage = couponImage;
        this.couponCode = couponCode;
        this.amount = amount;
        this.exp_days = exp_days;
        this.coupon_type = coupon_type;
        this.coupon_type = coupon_type;
        this.c_type = c_type;
        this.status = status;
        this.installation_date = installation_date;


    }

    public String getId() {
        return id;
    }

    public String getCouponImage() {
        return couponImage;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public String getAmount() {
        return amount;
    }

    public String getExp_days() {
        return exp_days;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public String getC_type() {
        return c_type;
    }

    public String getUser_code() {
        return user_code;
    }

    public String getStatus() {
        return status;
    }

    public String getInstallation_date() {
        return installation_date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCouponImage(String couponImage) {
        this.couponImage = couponImage;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setExp_days(String exp_days) {
        this.exp_days = exp_days;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInstallation_date(String installation_date) {
        this.installation_date = installation_date;
    }
}
