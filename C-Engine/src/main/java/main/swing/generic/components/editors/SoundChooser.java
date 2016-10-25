package main.swing.generic.components.editors;

import main.data.filesys.PathFinder;

public class SoundChooser extends FileChooser {
    @Override
    protected String getDefaultFileLocation() {
        return PathFinder.getSoundPath();
    }

}
