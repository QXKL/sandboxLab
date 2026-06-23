package com.qx.Logger;

// 使用示例
public class Main {
    public static void main(String[] args) {
        Loggers loggers = new Loggers();
        loggers.addLogger(new RemoteLogger("http://example.com"));
        loggers.addLogger(new FileLogger());
        loggers.logAll("Hello World!");

        Logger logger2 = new Logger(new RemoteLogger("http://example.com"));
        logger2.log("Hello World!");
    }
}