package org.naukma.dev_ice.controller;

import org.naukma.dev_ice.dto.ExportRequest;
import org.naukma.dev_ice.service.ExportOrderService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/orders")
public class OrderExportController {

    private final ExportOrderService exportOrderService;

    public OrderExportController(ExportOrderService exportOrderService) {
        this.exportOrderService = exportOrderService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportOrders(@RequestBody ExportRequest request) throws IOException {
        File tempFile = File.createTempFile("orders_", ".xlsx");

        exportOrderService.exportOrdersToExcel(request.getOrderIds(), request.getSortOrder(), tempFile.getAbsolutePath());

        byte[] fileContent = Files.readAllBytes(tempFile.toPath());
        tempFile.delete();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("orders.xlsx").build());
        headers.setContentLength(fileContent.length);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
