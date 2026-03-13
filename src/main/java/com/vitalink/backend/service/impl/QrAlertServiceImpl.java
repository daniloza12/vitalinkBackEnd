package com.vitalink.backend.service.impl;

import com.vitalink.backend.entity.Account;
import com.vitalink.backend.entity.Profile;
import com.vitalink.backend.repository.ProfileRepository;
import com.vitalink.backend.service.EmailService;
import com.vitalink.backend.service.QrAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class QrAlertServiceImpl implements QrAlertService {

    private static final Logger log = LoggerFactory.getLogger(QrAlertServiceImpl.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final EmailService emailService;
    private final ProfileRepository profileRepository;

    public QrAlertServiceImpl(EmailService emailService, ProfileRepository profileRepository) {
        this.emailService = emailService;
        this.profileRepository = profileRepository;
    }

    @Override
    @Async
    public void notifyQrScanned(Account account) {
        String scannedAt = LocalDateTime.now().format(FORMATTER);

        Optional<Profile> optProfile = profileRepository.findByAccountId(account.getId());

        String ownerName = account.getEmail();
        if (optProfile.isPresent() && optProfile.get().getPersonal() != null
                && optProfile.get().getPersonal().getFullName() != null) {
            ownerName = optProfile.get().getPersonal().getFullName();
        }

        // Notificar al titular de la cuenta
        emailService.sendQrScannedAlertToOwner(account.getEmail(), ownerName, scannedAt);
        log.info("QR scan alert sent to owner {} at {}", account.getEmail(), scannedAt);

        // Recolectar todos los correos de contactos y enviar un único correo
        if (optProfile.isPresent() && optProfile.get().getContacts() != null) {
            List<String> contactEmails = optProfile.get().getContacts().stream()
                    .flatMap(c -> java.util.stream.Stream.of(c.getPersonalEmail(), c.getWorkEmail()))
                    .filter(email -> email != null && !email.isBlank())
                    .distinct()
                    .toList();

            if (!contactEmails.isEmpty()) {
                emailService.sendQrScannedAlertToContacts(contactEmails, ownerName, scannedAt);
                log.info("QR scan alert sent to {} contact addresses for owner {} at {}",
                        contactEmails.size(), ownerName, scannedAt);
            }
        }
    }
}
