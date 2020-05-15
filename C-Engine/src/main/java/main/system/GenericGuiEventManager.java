package main.system;

public interface GenericGuiEventManager {
    void removeBind(EventType type);

    void bind(EventType type, EventCallback event);

    void cleanUp();

    void trigger(EventType type, Object obj);

    void processEvents();
}
