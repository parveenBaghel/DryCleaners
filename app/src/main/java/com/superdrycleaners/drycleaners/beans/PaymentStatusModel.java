package com.superdrycleaners.drycleaners.beans;

public class PaymentStatusModel {
    private String id;
    private String user_id;
    private String amount;
    private String remark;
    private String pay_status;
    private String date;
    private String bill_no;

    public PaymentStatusModel(String id, String user_id, String amount, String remark, String pay_status, String bill_no, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.amount = amount;
        this.remark = remark;
        this.pay_status = pay_status;
        this.date = created_at;
        this.bill_no = bill_no;
    }


    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public String getPay_status() {
        return pay_status;
    }

    public String getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

