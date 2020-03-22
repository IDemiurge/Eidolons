package main.level_editor.backend.handlers.ai;

public interface IAiHandler {

    void setLeader();

    void createGroup();

    void addToGroup();

    void editGroup(); //what if none is selected?

    void toggleEncounter(); //representation only?

    void toggleShowInfo();


}
