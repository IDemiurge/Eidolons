package main.level_editor.backend.handlers.ai;

import main.level_editor.backend.handlers.ControlButtonHandler;

public interface IAiHandler  extends ControlButtonHandler {

    void setLeader();

    void createGroup();

    void addToGroup();

    void editGroup(); //what if none is selected?

    void toggleEncounter(); //representation only?

    void toggleShowInfo();


}
