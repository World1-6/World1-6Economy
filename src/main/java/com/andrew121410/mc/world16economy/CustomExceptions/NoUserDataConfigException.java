package com.andrew121410.mc.world16economy.CustomExceptions;

public class NoUserDataConfigException extends Exception {

    public NoUserDataConfigException() {
    }

    public NoUserDataConfigException(String message) {
        super(message);
    }

    public NoUserDataConfigException(Throwable cause) {
        super(cause);
    }

    public NoUserDataConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
