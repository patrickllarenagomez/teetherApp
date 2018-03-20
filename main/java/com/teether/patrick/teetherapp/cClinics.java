package com.teether.patrick.teetherapp;

/**
 * Created by Patrick on 2/4/2018.
 */

public class cClinics
{
    private String titleHeader;
    private String addressDescription;
    private int itemNo;


    public cClinics(String titleHeader, String addressDescription, int itemNo)
    {
        this.titleHeader = titleHeader;
        this.addressDescription = addressDescription;
        this.itemNo = itemNo;
    }

    public String getTitleHeader() {
        return titleHeader;
    }

    public String getAddressDescription() {
        return addressDescription;
    }

    public int getItemNo() {
        return itemNo;
    }

}
