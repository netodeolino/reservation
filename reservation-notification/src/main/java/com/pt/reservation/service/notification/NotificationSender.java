package com.pt.reservation.service.notification;

public interface NotificationSender<T> {
    void send(T message);
}
