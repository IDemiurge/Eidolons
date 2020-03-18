package main.level_editor.backend.handlers.structure.layer;

public interface ILayerHandler {

    void cloneLayer();

    void toggleOn();

    void toggleVisible();

    void setTrigger();

    void edit();


    void add();
    void remove();

    void setDefault();

    void removeTrigger();


}
