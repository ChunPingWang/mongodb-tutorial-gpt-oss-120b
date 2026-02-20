package com.course.mongodb.m04.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
public class Address {
    @Id
    private String id;
    private String city;
    private String district;
    private String detail;

    public Address() {
    }

    public Address(String city, String district, String detail) {
        this.city = city;
        this.district = district;
        this.detail = detail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
