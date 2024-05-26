package com.example.application.qr;

import com.example.application.data.QRCodeEntity;
import com.example.application.qr.QRCodeReader;
import com.example.application.services.ContactService;
import com.example.application.services.QRCodeService;
import com.example.application.views.MainLayout;
import com.google.zxing.NotFoundException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Route(value = "qr-scan", layout = MainLayout.class)
@PageTitle("Scan QR Code | Vaadin CRM")
@PermitAll
public class QRCodeScanView extends VerticalLayout {

    private final QRCodeService qrCodeService;
    private final ContactService contactService;

    public QRCodeScanView(QRCodeService qrCodeService, ContactService contactService) {
        this.qrCodeService = qrCodeService;
        this.contactService = contactService;

        Paragraph instruction = new Paragraph("Scan a QR code by clicking the button below.");
        Button scanQRCodeButton = new Button("Scan QR Code", event -> scanQRCode());

        add(instruction, scanQRCodeButton);
    }

    private void scanQRCode() {
        try {
            String filePath = "src/main/resources/META-INF/resources/images/qr_code.png"; // Gerçek QR kod dosya yolu
            String qrContent = QRCodeReader.readQRCode(filePath);

            // QR kodu veritabanında bulma ve güncelleme
            QRCodeEntity qrCodeEntity = qrCodeService.findByLocation(qrContent);
            if (qrCodeEntity != null) {
                qrCodeEntity.setScannedBy(contactService.getCurrentUser());
                qrCodeEntity.setScanDateTime(LocalDateTime.now());
                qrCodeService.save(qrCodeEntity);

                Notification.show("QR Code scanned and data saved successfully.");
            } else {
                Notification.show("QR Code not found in the system.");
            }
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            Notification.show("Error scanning QR code.");
        }
    }
}
