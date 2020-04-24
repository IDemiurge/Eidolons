package main.level_editor.backend.handlers.structure.layer;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface ILayerHandler  extends ControlButtonHandler {

    void cloneLayer();

    void toggleOn();

    void toggleVisible();

    void setTrigger();

    void edit();


    void add();
    void remove();

    void mergeWithDefault();

    void mergeWith();

    void setDefault();

    void removeTrigger();


}
