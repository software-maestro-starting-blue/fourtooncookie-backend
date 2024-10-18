package com.startingblue.fourtooncookie.notification.exeption;

public final class NotificationSendFailedException extends RuntimeException {

    public NotificationSendFailedException(String message) {
        super(message);
    }
}
