package com.kushal.stylista.api.request;

public class ConfirmPaymentRequest {
    public String paymentIntentId;

    public ConfirmPaymentRequest(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
}
