package com.qx.sandboxLab.LSP;

// 问题在于: 父类的Account不允许balance<0, 而子类的OverdraftAccount则让balance可以<0

/* 修改方案: 使用组合
 */

import java.awt.event.WindowEvent;

class Account {
    protected double balance;

    public Account(double initialBalance) {
        this.balance = initialBalance;
    }

    public void 充钱(double amount) {
        this.balance += amount;
    }

    public void 扣钱(double amount) {
        this.balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

// 提款策略接口
interface WithdrawalStrategy {
    boolean canWithdraw(double balance, double amount);
}

// 标准策略，不允许借贷
class StandardStrategy implements WithdrawalStrategy {
    @Override
    public boolean canWithdraw(double balance, double amount) {
        return balance >= amount;
    }
}

class OverdraftWithdrawal implements WithdrawalStrategy {
    protected double overdraftLimit;

    public OverdraftWithdrawal(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public boolean canWithdraw(double balance, double amount) {
        return balance + overdraftLimit >= amount;
    }
}

class AccountService {
    private final Account account;
    private final WithdrawalStrategy strategy;

    public AccountService(Account account, WithdrawalStrategy strategy) {
        this.account = account;
        this.strategy = strategy;
    }

    public void pay(double amount) {
        if (strategy.canWithdraw(account.getBalance(), amount)) {
            account.扣钱(amount);
        } else {
            System.out.println("没钱了呜呜呜");
        }
    }

    public void 充值(double amount) {
        account.充钱(amount);
    }
}
