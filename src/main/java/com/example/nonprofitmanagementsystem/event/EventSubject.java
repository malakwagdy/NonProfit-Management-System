package com.example.nonprofitmanagementsystem.event;

public interface EventSubject {
    public void addObserver(EventObserver observer);
    public void removeObserver(EventObserver observer);
    public void notifyObservers(String message);
}
