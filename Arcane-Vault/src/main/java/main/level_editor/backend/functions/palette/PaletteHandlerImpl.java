package main.level_editor.backend.functions.palette;

import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

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
}
