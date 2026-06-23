package com.qx.pay.impl;

import com.qx.pay.PaymentAPI;

public class CreditCardPayment implements PaymentAPI {

    @Override
    public void pay(String orderNo, double amount) {
        System.out.println("信用卡支付");
    }

}
