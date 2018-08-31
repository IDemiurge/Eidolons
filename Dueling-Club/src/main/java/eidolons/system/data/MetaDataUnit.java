package eidolons.system.data;

import eidolons.system.data.MetaDataUnit.META_DATA;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/30/2018.
 */
public class MetaDataUnit extends DataUnit<META_DATA> {

    private static MetaDataUnit instance;

    public MetaDataUnit(String text) {
        super(text);
    }

    public static MetaDataUnit getInstance() {
        if (instance == null) {
            instance = new MetaDataUnit(
             FileManager.readFile(PathFinder.getMetaDataUnitPath()) );
        }
        return instance;
    }

    public static void setInstance(MetaDataUnit instance) {
        MetaDataUnit.instance = instance;
    }

    public enum META_DATA{
        LAST_PREGEN_LVL_INDEX_MAP,

    }
}
