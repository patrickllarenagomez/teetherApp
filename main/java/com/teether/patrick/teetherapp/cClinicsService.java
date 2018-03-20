package com.teether.patrick.teetherapp;

/**
 * Created by Patrick on 2/12/2018.
 */

public class cClinicsService {
    private String serviceName;
    private String servicePrice;


    public cClinicsService(String serviceName, String servicePrice)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServicePrice() {
        return servicePrice;
    }

}
