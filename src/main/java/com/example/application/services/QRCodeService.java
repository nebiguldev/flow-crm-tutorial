package com.example.application.services;

import com.example.application.data.QRCodeEntity;
import com.example.application.data.QRCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;

    public QRCodeService(QRCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    public List<QRCodeEntity> findAll() {
        return qrCodeRepository.findAll();
    }

    public void save(QRCodeEntity qrCode) {
        qrCodeRepository.save(qrCode);
    }

    public QRCodeEntity findById(Long id) {
        return qrCodeRepository.findById(id).orElse(null);
    }

    public QRCodeEntity findByLocation(String location) {
        return qrCodeRepository.findByLocation(location);
    }

    public void delete(QRCodeEntity qrCode) {
        qrCodeRepository.delete(qrCode);
    }
}
