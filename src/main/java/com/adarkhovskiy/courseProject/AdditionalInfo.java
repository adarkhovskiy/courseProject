package com.adarkhovskiy.courseProject;

import java.io.Serializable;

public class AdditionalInfo implements Serializable {

    private int additionalInfoId;
    private String phoneNumber;
    private String address;

    public AdditionalInfo() {}

    public AdditionalInfo(int additionalInfoId, String phoneNumber, String address) {
        this.additionalInfoId = additionalInfoId;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public AdditionalInfo(String additionalData) {
        additionalData.replaceAll("\\s+","");
        String[] data = additionalData.split(",");
        this.additionalInfoId = Integer.valueOf(data[0]);
        this.phoneNumber = data[1];
        this.address = data[2];
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(getClass() == obj.getClass()))
            return false;
        else {
            AdditionalInfo tmp = (AdditionalInfo) obj;
            if (tmp.getAdditionalInfoId() == this.getAdditionalInfoId() && tmp.getPhoneNumber().equals(this.getPhoneNumber())
                    && tmp.getAddress().equals(this.getAddress()))
                return true;
            else
                return false;
        }
    }
}
