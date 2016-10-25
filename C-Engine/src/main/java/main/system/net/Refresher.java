package main.system.net;

public interface Refresher extends Runnable {
    void setEnabled(boolean b);
}
