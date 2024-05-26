package com.example.application.qr;


import com.example.application.data.QRCodeEntity;
import com.example.application.services.QRCodeService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "qr-admin", layout = MainLayout.class)
@PageTitle("QR Codes Admin | Vaadin CRM")
@RolesAllowed("ROLE_ADMIN")
public class QRCodeAdminView extends VerticalLayout {

    private final QRCodeService qrCodeService;

    public QRCodeAdminView(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;

        Grid<QRCodeEntity> qrCodeGrid = new Grid<>(QRCodeEntity.class);
        qrCodeGrid.setItems(qrCodeService.findAll());
        qrCodeGrid.setColumns("id", "location", "createdBy", "scannedBy", "scanDateTime");

        add(qrCodeGrid);
    }
}