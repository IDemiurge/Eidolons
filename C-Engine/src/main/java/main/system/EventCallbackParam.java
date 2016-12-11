package main.system;

public class EventCallbackParam<T> {
    private T object;

    public EventCallbackParam(T param) {
        object = param;
    }

    public T get() {
        return object;
    }
}
