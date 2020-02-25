package main.level_editor.gui.palette;

import main.content.DC_TYPE;
import main.data.DataManager;

public class ObjectPalette extends LE_Palette{

    public void init(){
        DataManager.getTypesSubGroupNames(DC_TYPE.BF_OBJ)
    }
}
