package com.qx.pay;


public class PaymentService {
    private PaymentAPI paymentAPI;

    public PaymentService(PaymentAPI paymentAPI) {
        this.paymentAPI = paymentAPI;
    }

    public void setPaymentAPI(PaymentAPI paymentAPI) {
        this.paymentAPI = paymentAPI;
    }

    public void pay(String orderNo, double amount) {
        if (paymentAPI != null) {
            paymentAPI.pay(orderNo, amount);
        }
    }

}
