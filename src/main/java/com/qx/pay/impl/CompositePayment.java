package com.qx.pay.impl;

import com.qx.pay.PaymentAPI;

public class CompositePayment implements PaymentAPI {
    private final PaymentAPI primaryPaymentAPI;
    private final PaymentAPI secondPaymentAPI;
    private final double primaryLimit;


    CompositePayment(PaymentAPI primaryPaymentAPI, PaymentAPI secondPaymentAPI, double primaryLimit) {
        this.primaryPaymentAPI = primaryPaymentAPI;
        this.secondPaymentAPI = secondPaymentAPI;
        this.primaryLimit = primaryLimit;
    }

    public void pay(String orderNo, double amount) {
        if (primaryLimit >= amount) {
            primaryPaymentAPI.pay(orderNo, amount);
        } else {
            primaryPaymentAPI.pay(orderNo, primaryLimit);
            secondPaymentAPI.pay(orderNo, amount - primaryLimit);
        }
    }
}
