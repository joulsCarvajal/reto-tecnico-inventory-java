package com.reto.tecnico.infrastructure.email;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class SendGridEmailService {

    @Value("${app.sendgrid.api-key}")
    private String apiKey;

    @Value("${app.sendgrid.from-email}")
    private String fromEmail;

    @Value("${app.sendgrid.from-name}")
    private String fromName;

    public void enviarInventarioPdf(String destinatario, String empresaNombre,
                                     String empresaNit, byte[] pdfBytes) throws IOException {

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(destinatario);

        String subject = "Inventario - " + empresaNombre + " (NIT: " + empresaNit + ")";

        Content content = new Content("text/html",
                buildEmailBody(empresaNombre, empresaNit));

        Mail mail = new Mail(from, subject, to, content);

        // Adjuntar PDF
        Attachments attachment = new Attachments();
        attachment.setContent(Base64.getEncoder().encodeToString(pdfBytes));
        attachment.setType("application/pdf");
        attachment.setFilename("inventario_" + empresaNit + ".pdf");
        attachment.setDisposition("attachment");
        mail.addAttachments(attachment);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        log.info("SendGrid response: {} - {}", response.getStatusCode(), response.getBody());

        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("Error enviando correo: " + response.getBody());
        }
    }

    private String buildEmailBody(String empresaNombre, String empresaNit) {
        return """
            <html>
              <body style="font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px;">
                  <h2 style="color: #2962ff;">Inventario de Productos</h2>
                  <p>Estimado usuario,</p>
                  <p>Adjunto encontrará el reporte de inventario para:</p>
                  <ul>
                    <li><strong>Empresa:</strong> %s</li>
                    <li><strong>NIT:</strong> %s</li>
                  </ul>
                  <p>Este reporte fue generado automáticamente desde el sistema de inventario.</p>
                  <hr style="border: 1px solid #eee;">
                  <p style="font-size: 12px; color: #888;">
                    Este correo fue enviado desde el sistema de gestión de inventarios.
                  </p>
                </div>
              </body>
            </html>
            """.formatted(empresaNombre, empresaNit);
    }
}
