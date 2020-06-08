package main.data;

import eidolons.game.core.game.DC_Game;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.launch.ArcaneVault;

import java.util.List;

public class AvHandler {

    protected ObjType getType(String name, DC_TYPE type) {
        return DataManager.getType(name, type);
    }
    protected boolean confirm(String s) {
        return DialogMaster.confirm(s);
    }

    protected String input(String s) {
        return DialogMaster.inputText(s );
    }
    protected int inputInt(String s) {
        return DialogMaster.inputInt(s, 0);
    }

    public ObjType getSelected(){
        return ArcaneVault.getSelectedType();
    }
    public ObjType getPrevious(){
        return ArcaneVault.getPreviousSelectedType();
    }
    public DC_Game getGame(){
        return ArcaneVault.getGame();
    }
    public List<ObjType> getSelectedTypes(){
        return ArcaneVault.getSelectedTypes();
    }
}
