package com.superdrycleaners.drycleaners.beans;


public class PincodeModel {
    private String id;
    private String  pincodeId;
    private String location;

//    public PincodeModel(String id, String pincodeId, String location) {
//        this.id = id;
//        this.pincodeId = pincodeId;
//        this.location = location;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPincodeId() {
        return pincodeId;
    }

    public void setPincodeId(String pincodeId) {
        this.pincodeId = pincodeId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
