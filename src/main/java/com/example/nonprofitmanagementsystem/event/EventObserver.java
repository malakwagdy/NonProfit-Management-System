package com.example.nonprofitmanagementsystem.event;

public interface EventObserver {
    public void update (Event event, String message);
    public default void unsubscribe(EventSubject s) {
        s.removeObserver(this);
    }
}
