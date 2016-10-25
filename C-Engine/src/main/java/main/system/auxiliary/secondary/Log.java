package main.system.auxiliary.secondary;

public interface Log {

    void sysLog(String text);

    void chatLog(String text);

    void combatLog(String string);

}
