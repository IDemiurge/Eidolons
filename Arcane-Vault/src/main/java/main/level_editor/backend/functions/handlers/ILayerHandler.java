package main.level_editor.backend.functions.handlers;

public interface ILayerHandler {

    void cloneLayer();

    void toggleOn();

    void toggleVisible();

    void setTrigger();

    void remove();

    void add();

    void setDefault();

    void removeTrigger();


}
