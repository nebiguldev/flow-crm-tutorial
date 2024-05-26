package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class QRCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
    private Long id;

    @NotBlank
    private String location;

    @ManyToOne
    private Contact createdBy;

    @OneToOne
    private Contact scannedBy;

    @ManyToOne
    private Company company;

    private LocalDateTime scanDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Contact getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Contact createdBy) {
        this.createdBy = createdBy;
    }

    public Contact getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(Contact scannedBy) {
        this.scannedBy = scannedBy;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getScanDateTime() {
        return scanDateTime;
    }

    public void setScanDateTime(LocalDateTime scanDateTime) {
        this.scanDateTime = scanDateTime;
    }
}
