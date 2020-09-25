package com.example.mayday22;

public class Medic {
    private String Id, name, password, organization, idUrl;
    private double latitude=0.0, longitude=0.0, userLatitude, userLongitude;
    String tripleshake1 = "false", tripleshake3 = "false";

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getTripleshake1() {
        return tripleshake1;
    }

    public void setTripleshake1(String tripleshake1) {
        this.tripleshake1 = tripleshake1;
    }

    public String getTripleshake3() {
        return tripleshake3;
    }

    public void setTripleshake3(String tripleshake3) {
        this.tripleshake3 = tripleshake3;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getIdUrl() {
        return idUrl;
    }

    public void setIdUrl(String idUrl) {
        this.idUrl = idUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
