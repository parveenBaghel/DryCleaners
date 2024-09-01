package com.superdrycleaners.drycleaners.beans;

public class RequestBeans {
    private String id;
    private String name;
    private String address;
    private String mobile;
    private String date;
    private String time;
    private String remarks;
    private String pickup_id;
    private String status;
    private String payment_option;
    private String d_req;

    public String getD_req() {
        return d_req;
    }

    public void setD_req(String d_req) {
        this.d_req = d_req;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayment_option() {
        return payment_option;
    }

    public void setPayment_option(String payment_option) {
        this.payment_option = payment_option;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickup_id() {
        return pickup_id;
    }

    public void setPickup_id(String pickup_id) {
        this.pickup_id = pickup_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
