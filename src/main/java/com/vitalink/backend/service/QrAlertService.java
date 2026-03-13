package com.vitalink.backend.service;

import com.vitalink.backend.entity.Account;

public interface QrAlertService {
    void notifyQrScanned(Account account);
}
