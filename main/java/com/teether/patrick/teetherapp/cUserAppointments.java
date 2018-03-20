package com.teether.patrick.teetherapp;

/**
 * Created by Patrick on 2/12/2018.
 */

public class cUserAppointments {

    private int appointment_id;
    private String clinic_name;
    private String clinic_address;
    private String clinic_date;
    private String clinic_status;

    public cUserAppointments(int appointment_id, String clinic_name, String clinic_address, String clinic_date, String clinic_status) {
        this.appointment_id = appointment_id;
        this.clinic_name = clinic_name;
        this.clinic_address = clinic_address;
        this.clinic_date = clinic_date;
        this.clinic_status = clinic_status;
    }


    public int getAppointment_id() {
        return appointment_id;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public String getClinic_address() {
        return clinic_address;
    }

    public String getClinic_date() {
        return clinic_date;
    }

    public String getClinic_status() {
        return clinic_status;
    }





}
