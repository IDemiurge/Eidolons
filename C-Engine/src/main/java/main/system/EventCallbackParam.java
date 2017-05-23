package main.system;

public class EventCallbackParam {
    private Object object;

    public EventCallbackParam(Object param) {
        object = param;
    }

    public Object get() {
        return object;
    }
}
