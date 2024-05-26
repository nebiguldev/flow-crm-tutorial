package com.example.application.views;

import com.example.application.data.QRCodeEntity;
import com.example.application.qr.QRCodeGenerator;
import com.example.application.services.ContactService;
import com.example.application.services.QRCodeService;
import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
// merhaba cihan commit
@Route(value = "qr-codes", layout = MainLayout.class)
@PageTitle("QR Codes | Vaadin CRM")
@RolesAllowed("ROLE_ADMIN")
public class QRCodeView extends VerticalLayout {

    private final QRCodeService qrCodeService;
    private final ContactService contactService;
    private Image qrCodeImage;

    public QRCodeView(QRCodeService qrCodeService, ContactService contactService) {
        this.qrCodeService = qrCodeService;
        this.contactService = contactService;

        qrCodeImage = new Image();
        qrCodeImage.setVisible(false); // Başlangıçta görünmez

        Button generateQRCodeButton = new Button("Generate QR Code", event -> generateQRCode());
        add(generateQRCodeButton, qrCodeImage);
    }
//TODO: rollere göre qr kod okuma yazma işlemi yapılacak

    private void generateQRCode() {
        try {
            var currentUser = contactService.getCurrentUser();
            if (currentUser == null) {
                Notification.show("Current user not found. Please log in.");
                return;
            }

            // Burada gerçek QR kod metni ile değiştirin
            String qrText = "Sample QR Code Text";
            String filePath = "src/main/resources/META-INF/resources/images/qr_code.png";
            QRCodeGenerator.generateQRCodeImage(qrText, 350, 350, filePath);

            // QR kod bilgisini veritabanına kaydetme
            QRCodeEntity qrCodeEntity = new QRCodeEntity();
            qrCodeEntity.setLocation("Sample Location"); // Gerçek konum bilgisi ile değiştirin
            qrCodeEntity.setCreatedBy(currentUser); // Şu anki admin kullanıcısını ayarlayın
            qrCodeEntity.setCompany(currentUser.getCompany());

            qrCodeService.save(qrCodeEntity);

            qrCodeImage.setSrc("images/qr_code.png");
            qrCodeImage.setVisible(true); // QR kod oluşturulduktan sonra görünür yapın
            Notification.show("QR Code generated and saved successfully.");
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            Notification.show("Error generating QR code.");
        }
    }
}
