package com.project.bookingya.models;

import java.util.UUID;

public class Guest {
    private UUID id;
    private String identification;
    private String fullName;
    private String email;
    private String phone;

    public Guest() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
