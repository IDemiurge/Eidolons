package main.swing.generic.services.listener;

public interface ClickListenerEnum<T> {
    void handleClick(T command, boolean alt);
}
