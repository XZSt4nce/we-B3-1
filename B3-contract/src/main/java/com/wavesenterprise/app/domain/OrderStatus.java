package com.wavesenterprise.app.domain;

public enum OrderStatus {
    WAITING_FOR_CLIENT,
    WAITING_FOR_EMPLOYEE,
    WAITING_FOR_PAYMENT,
    EXECUTING,
    CANCELLED,
    DONE
}
