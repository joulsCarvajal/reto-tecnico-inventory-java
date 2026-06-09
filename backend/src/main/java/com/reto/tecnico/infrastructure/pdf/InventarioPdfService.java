package com.reto.tecnico.infrastructure.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.reto.tecnico.application.dto.response.InventarioResponse;
import com.reto.tecnico.application.dto.response.ProductoPrecioResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class InventarioPdfService {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 98, 255);
    private static final DeviceRgb HEADER_TEXT   = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb ROW_ALT_COLOR = new DeviceRgb(240, 245, 255);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPdfInventario(String empresaNombre, String empresaNit,
                                        List<InventarioResponse> inventario) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4.rotate());

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Encabezado
            Paragraph titulo = new Paragraph("INVENTARIO DE PRODUCTOS")
                    .setFont(boldFont).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(HEADER_COLOR);
            document.add(titulo);

            document.add(new Paragraph("Empresa: " + empresaNombre + " | NIT: " + empresaNit)
                    .setFont(regularFont).setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Generado: " + LocalDateTime.now().format(FORMATTER))
                    .setFont(regularFont).setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph("\n"));

            // Tabla
            float[] colWidths = {2f, 3f, 1.5f, 4f, 2f, 2f, 2f};
            Table table = new Table(UnitValue.createPercentArray(colWidths))
                    .useAllAvailableWidth();

            // Headers
            String[] headers = {"Código", "Nombre", "Cantidad", "Características", "COP", "USD", "EUR"};
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setFont(boldFont).setFontSize(9)
                                .setFontColor(HEADER_TEXT))
                        .setBackgroundColor(HEADER_COLOR)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }

            // Filas
            boolean altRow = false;
            for (InventarioResponse item : inventario) {
                DeviceRgb bg = altRow ? ROW_ALT_COLOR : new DeviceRgb(255, 255, 255);

                table.addCell(styledCell(item.getProductoCodigo(), regularFont, bg, TextAlignment.CENTER));
                table.addCell(styledCell(item.getProductoNombre(), boldFont, bg, TextAlignment.LEFT));
                table.addCell(styledCell(String.valueOf(item.getCantidad()), regularFont, bg, TextAlignment.CENTER));
                table.addCell(styledCell(
                        item.getProductoCaracteristicas() != null ? item.getProductoCaracteristicas() : "-",
                        regularFont, bg, TextAlignment.LEFT));

                table.addCell(styledCell(getPrecio(item, "COP"), regularFont, bg, TextAlignment.RIGHT));
                table.addCell(styledCell(getPrecio(item, "USD"), regularFont, bg, TextAlignment.RIGHT));
                table.addCell(styledCell(getPrecio(item, "EUR"), regularFont, bg, TextAlignment.RIGHT));

                altRow = !altRow;
            }

            document.add(table);

            // Total de registros
            document.add(new Paragraph("\nTotal de productos: " + inventario.size())
                    .setFont(boldFont).setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando PDF: ", e);
            throw new RuntimeException("Error generando PDF del inventario", e);
        }
    }

    private Cell styledCell(String text, PdfFont font, DeviceRgb bg, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text != null ? text : "-").setFont(font).setFontSize(8))
                .setBackgroundColor(bg)
                .setTextAlignment(align)
                .setPadding(4);
    }

    private String getPrecio(InventarioResponse item, String moneda) {
        if (item.getPrecios() == null) return "-";
        return item.getPrecios().stream()
                .filter(p -> moneda.equals(p.getMoneda()))
                .findFirst()
                .map(p -> String.format("%,.2f", p.getPrecio()))
                .orElse("-");
    }
}
