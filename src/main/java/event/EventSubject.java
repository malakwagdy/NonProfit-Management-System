package event;

import java.util.Observer;

public interface EventSubject {
    public void addObserver(EventObserver observer);
    public void removeObserver(EventObserver observer);
    public void notifyObservers(String message);
}
