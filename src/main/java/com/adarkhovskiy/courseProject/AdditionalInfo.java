package com.adarkhovskiy.courseProject;

public class AdditionalInfo {

    private int additionalInfoId;
    private String phoneNumber;
    private String address;

    public AdditionalInfo() {}

    public AdditionalInfo(int additionalInfoId, String phoneNumber, String address) {
        this.additionalInfoId = additionalInfoId;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getAdditionalInfoId() {
        return additionalInfoId;
    }

    public void setAdditionalInfoId(int additionalInfoId) {
        this.additionalInfoId = additionalInfoId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
