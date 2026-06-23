package com.qx.Logger;

import java.util.ArrayList;
import java.util.List;

interface LoggerAPI {
    void log(String messages);
}

class ConsoleLogger implements LoggerAPI {

    ConsoleLogger() {}

    @Override
    public void log(String message) {
        System.out.println("Console: " + message);
    }
}

class FileLogger implements LoggerAPI {

    FileLogger() {}

    @Override
    public void log(String message) {
        System.out.println("File: " + message);
    }
}

class DatabaseLogger implements LoggerAPI {
    private String DatabasePre;

    DatabaseLogger(String DatabasePre) {
        this.DatabasePre = DatabasePre;
    }

    @Override
    public void log(String message) {
        System.out.println("Database: " + message);

        // 数据库写入逻辑
    }
}

class RemoteLogger implements LoggerAPI {
    private String url;

    RemoteLogger(String url) {
        this.url = url;
    }

    @Override
    public void log(String message) {
        System.out.println("Remote: " + message);
    }
}

class Logger {
    private final LoggerAPI loggerAPI;

    Logger(LoggerAPI loggerAPI) {
        this.loggerAPI = loggerAPI;
    }

    public void log(String message) {
        loggerAPI.log(message);
    }
}

class Loggers {
    private final List<LoggerAPI> loggers = new ArrayList<>();

    public void addLogger(LoggerAPI loggerAPI) {
        loggers.add(loggerAPI);
    }

    public void removeLogger(LoggerAPI loggerAPI) {
        loggers.remove(loggerAPI);
    }

    public void logAll(String message) {
        for (LoggerAPI logger : loggers) {
            logger.log(message);
        }
    }
}