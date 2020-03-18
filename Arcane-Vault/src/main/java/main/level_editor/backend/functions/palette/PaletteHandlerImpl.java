package main.level_editor.backend.functions.palette;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

import java.util.ArrayList;
import java.util.List;

public class PaletteHandlerImpl extends LE_Handler implements IPaletteHandler {

    public PaletteHandlerImpl(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void createPalette() {
        //displayed as tree?
        //what is the data, txt files? yeah, but maybe with folder structure!

        //path via layered grouping...

//        selectedPalettePath =  getModel().getPaletteSelection();
//        if ( == null ){
//            path = getDefaultPath() + name;
//        }
//        //paletteCreationDialog
//
//        FileManager.write(types, path);
    }

    @Override
    public void removePalette() {

    }

    @Override
    public void mergePalettes() {

    }

    @Override
    public void clonePalette() {

    }

    @Override
    public void addToPalette() {

    }

    @Override
    public void removeFromPalette() {

    }

    public List<ObjType> getTypesForTreeNode(DC_TYPE TYPE, Object object) {
        List<ObjType> list = new ArrayList<>();
        if (object instanceof DC_TYPE) {
            DataManager.getTypes(((DC_TYPE) object));
        } else {
            if (object instanceof String) {
                DataManager.getTypesSubGroup(TYPE, object.toString());
                DataManager.getTypesGroup(TYPE, object.toString());
            } else {

            }
        }
        return list;
    }
}
