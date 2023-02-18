package com.example.clinicmanagement;

import java.time.LocalDate;

public class User {
    private String name;
    private String nik;
    private LocalDate birthDate;
    private String address;

    public User(String name, String nik, LocalDate birthDate, String address) {
        this.name = name;
        this.nik = nik;
        this.birthDate = birthDate;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}