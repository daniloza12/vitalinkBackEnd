package com.vitalink.backend.service.impl;

import com.vitalink.backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Agente 1: Email — responsable exclusivo de construir y enviar correos
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@vitalink.com}")
    private String fromEmail;

    @Value("${vitalink.app.url:http://localhost:4201}")
    private String appUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Activa tu cuenta VitaLink");

            String activationLink = appUrl + "/activate?token=" + token;
            helper.setText(buildHtml(activationLink), true);

            mailSender.send(message);
            log.info("Activation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Error sending activation email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Recupera tu contraseña — VitaLink");

            String resetLink = appUrl + "/reset-password?token=" + token;
            helper.setText(buildPasswordResetHtml(resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Error sending password reset email", e);
        }
    }

    private String buildPasswordResetHtml(String resetLink) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding:40px 0;">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:8px;overflow:hidden;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:#2c3e50;padding:24px 32px;">
                              <h1 style="margin:0;color:#ffffff;font-size:24px;">VitaLink</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px;">
                              <h2 style="color:#2c3e50;margin-top:0;">Recuperación de contraseña</h2>
                              <p style="color:#555;line-height:1.6;">
                                Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                                Haz clic en el botón de abajo para crear una nueva contraseña.
                              </p>
                              <div style="text-align:center;margin:32px 0;">
                                <a href="%s"
                                   style="display:inline-block;padding:14px 32px;
                                          background-color:#e74c3c;color:#ffffff;
                                          text-decoration:none;border-radius:6px;
                                          font-weight:bold;font-size:16px;">
                                  Restablecer contraseña
                                </a>
                              </div>
                              <p style="color:#888;font-size:13px;line-height:1.5;">
                                Este enlace expira en <strong>24 horas</strong>.<br>
                                Si no solicitaste este cambio, ignora este mensaje.
                                Tu contraseña actual seguirá siendo la misma.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f4f6f8;padding:16px 32px;text-align:center;">
                              <p style="margin:0;color:#aaa;font-size:12px;">
                                © 2026 VitaLink. Todos los derechos reservados.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(resetLink);
    }

    @Override
    public void sendQrScannedAlertToOwner(String toEmail, String ownerName, String scannedAt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("❗[INFO] Tu QR de VitaLink fue escaneado — " + scannedAt);

            setUrgencyHeaders(message);

            String intro = "Tu código QR de VitaLink acaba de ser escaneado.";
            String recommendation = "Si fuiste tú quien mostró el QR, no es necesario que hagas nada. "
                    + "De lo contrario, revisa tu información de seguridad.";
            helper.setText(buildQrAlertHtml(ownerName, scannedAt, intro, recommendation), true);

            mailSender.send(message);
            log.info("QR alert email sent to owner {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send QR owner alert to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendQrScannedAlertToContacts(List<String> toEmails, String ownerName, String scannedAt) {
        if (toEmails == null || toEmails.isEmpty()) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmails.toArray(new String[0]));
            helper.setSubject("❗[INFO] QR de " + ownerName + " fue escaneado — " + scannedAt);

            setUrgencyHeaders(message);

            String intro = "El código QR de <strong>" + ownerName + "</strong> acaba de ser escaneado.";
            String recommendation = "Esto puede ser una señal de alerta. Se recomienda ponerse en contacto "
                    + "con <strong>" + ownerName + "</strong> o estar atento a cualquier comunicado de su parte.";
            helper.setText(buildQrAlertHtml(ownerName, scannedAt, intro, recommendation), true);

            mailSender.send(message);
            log.info("QR alert email sent to {} contacts for owner {}", toEmails.size(), ownerName);
        } catch (Exception e) {
            log.error("Failed to send QR contacts alert for {}: {}", ownerName, e.getMessage());
        }
    }

    private void setUrgencyHeaders(MimeMessage message) throws jakarta.mail.MessagingException {
        message.setHeader("X-Priority", "1");           // 1 = Highest (Outlook, Thunderbird)
        message.setHeader("X-MSMail-Priority", "High"); // Outlook legacy
        message.setHeader("Importance", "High");        // RFC 2156 — Gmail, Apple Mail
    }

    private String buildQrAlertHtml(String ownerName, String scannedAt, String intro, String recommendation) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding:40px 0;">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:8px;overflow:hidden;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:#e67e22;padding:24px 32px;">
                              <h1 style="margin:0;color:#ffffff;font-size:24px;">VitaLink — Alerta QR</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px;">
                              <h2 style="color:#c0392b;margin-top:0;">QR escaneado</h2>
                              <p style="color:#555;line-height:1.6;">%s</p>
                              <table style="background:#fff3cd;border-left:4px solid #e67e22;
                                            border-radius:4px;padding:16px;width:100%%;margin:16px 0;">
                                <tr>
                                  <td>
                                    <p style="margin:0;color:#856404;font-size:14px;">
                                      <strong>Fecha y hora del escaneo:</strong><br>%s
                                    </p>
                                  </td>
                                </tr>
                              </table>
                              <p style="color:#555;line-height:1.6;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f4f6f8;padding:16px 32px;text-align:center;">
                              <p style="margin:0;color:#aaa;font-size:12px;">
                                &copy; 2026 VitaLink. Todos los derechos reservados.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(intro, scannedAt, recommendation);
    }

    private String buildHtml(String activationLink) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding:40px 0;">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:8px;overflow:hidden;
                                      box-shadow:0 2px 8px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background:#27ae60;padding:24px 32px;">
                              <h1 style="margin:0;color:#ffffff;font-size:24px;">VitaLink</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px;">
                              <h2 style="color:#2c3e50;margin-top:0;">Bienvenido a VitaLink</h2>
                              <p style="color:#555;line-height:1.6;">
                                Gracias por registrarte. Para activar tu cuenta y comenzar a usar
                                la plataforma, haz clic en el botón de abajo.
                              </p>
                              <div style="text-align:center;margin:32px 0;">
                                <a href="%s"
                                   style="display:inline-block;padding:14px 32px;
                                          background-color:#27ae60;color:#ffffff;
                                          text-decoration:none;border-radius:6px;
                                          font-weight:bold;font-size:16px;">
                                  Activar mi cuenta
                                </a>
                              </div>
                              <p style="color:#888;font-size:13px;line-height:1.5;">
                                Este enlace expira en <strong>24 horas</strong>.<br>
                                Si no creaste una cuenta en VitaLink, puedes ignorar este mensaje.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f4f6f8;padding:16px 32px;text-align:center;">
                              <p style="margin:0;color:#aaa;font-size:12px;">
                                © 2026 VitaLink. Todos los derechos reservados.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(activationLink);
    }
}
