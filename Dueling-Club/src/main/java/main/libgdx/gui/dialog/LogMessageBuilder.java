package main.libgdx.gui.dialog;

public class LogMessageBuilder {
    private LogMessageBuilder() {
        //to prevent direct instance creation
    }

    public static LogMessageBuilder createNew() {
        return new LogMessageBuilder();
    }
}
