package com.example.application.views;

import com.example.application.data.QRCodeEntity;
import com.example.application.qr.QRCodeGenerator;
import com.example.application.services.ContactService;
import com.example.application.services.QRCodeService;
import com.google.zxing.WriterException;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.time.LocalDateTime;

@Route(value = "qr-codes", layout = MainLayout.class)
@PageTitle("QR Codes | Vaadin CRM")
@RolesAllowed({"ROLE_FOREMAN", "ROLE_ADMIN"})
// @AnonymousAllowed
public class QRCodeView extends VerticalLayout {

    private final QRCodeService qrCodeService;
    private final ContactService contactService;
    private final AuthenticationContext authenticationContext;
    private Image qrCodeImage;


    public QRCodeView(QRCodeService qrCodeService, ContactService contactService, AuthenticationContext authenticationContext) {
        this.qrCodeService = qrCodeService;
        this.contactService = contactService;
        this.authenticationContext = authenticationContext;

        qrCodeImage = new Image();
        qrCodeImage.setVisible(true); // Başlangıçta görünür
        add(qrCodeImage); // QR kodu görüntüleyiciyi ekle

        generateQRCode(); // QR kodu oluştur ve göster
    }

    private void generateQRCode() {

        try {
            var currentUser = contactService.getCurrentUser();
            Notification.show(String.valueOf(currentUser));
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
            qrCodeEntity.setLocation("text"); // Gerçek konum bilgisi ile değiştirin
            qrCodeEntity.setScanDateTime(LocalDateTime.now());

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
