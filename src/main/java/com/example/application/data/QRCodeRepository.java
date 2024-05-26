package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;


public interface QRCodeRepository extends JpaRepository<QRCodeEntity, Long> {
    QRCodeEntity findByLocation(String location);
}