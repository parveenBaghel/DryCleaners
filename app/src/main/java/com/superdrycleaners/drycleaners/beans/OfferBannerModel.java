package com.superdrycleaners.drycleaners.beans;

public class OfferBannerModel {
    private String offerId;
    private String offerName;
    private String offerAmount;
    private String offerPercentage;
    private String offerTotal;
    private String offerImage;
    private String description;




    public OfferBannerModel() {
        this.offerName = offerName;
        this.offerAmount = offerAmount;
        this.offerPercentage = offerPercentage;
        this.offerTotal = offerTotal;
        this.offerImage = offerImage;
        this.offerId=offerId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(String offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(String offerPercentage) {
        this.offerPercentage = offerPercentage;
    }

    public String getOfferTotal() {
        return offerTotal;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferTotal(String offerTotal) {
        this.offerTotal = offerTotal;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}
