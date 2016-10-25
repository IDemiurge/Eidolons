package main.swing.listeners;

public interface ButtonHandler {
    void handleClick(String command);

    void handleClick(String command, boolean alt);
}
