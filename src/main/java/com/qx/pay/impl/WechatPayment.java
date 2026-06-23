package com.qx.pay.impl;

import com.qx.pay.PaymentAPI;

public class WechatPayment implements PaymentAPI {

    @Override
    public void pay(String orderNo, double amount) {
        System.out.println("微信支付");
    }
}
