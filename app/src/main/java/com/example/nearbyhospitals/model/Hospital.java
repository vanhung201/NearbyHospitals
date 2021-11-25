package com.example.nearbyhospitals.model;

public class Hospital {
    private String nameHospital;
    private String hospitalLong;
    private String hospitalLat;

    public Hospital(String nameHospital, String hospitalLong, String hospitalLat) {
        this.nameHospital = nameHospital;
        this.hospitalLong = hospitalLong;
        this.hospitalLat = hospitalLat;
    }

    public String getNameHospital() {
        return nameHospital;
    }

    public void setNameHospital(String nameHospital) {
        this.nameHospital = nameHospital;
    }

    public String getHospitalLong() {
        return hospitalLong;
    }

    public void setHospitalLong(String hospitalLong) {
        this.hospitalLong = hospitalLong;
    }

    public String getHospitalLat() {
        return hospitalLat;
    }

    public void setHospitalLat(String hospitalLat) {
        this.hospitalLat = hospitalLat;
    }
}
