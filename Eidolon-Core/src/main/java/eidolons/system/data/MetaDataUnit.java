package eidolons.system.data;

import eidolons.system.data.MetaDataUnit.META_DATA;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 8/30/2018.
 */
public class MetaDataUnit extends DataUnit<META_DATA> {

    public static final String EXIT_OK = "ok";
    private static MetaDataUnit instance;
    private   static boolean initialized;

    private MetaDataUnit(String text) {
        super(text);
        initialized = true;
    }

    public static void write() {
        if (initialized)
            FileManager.write(getInstance().getData(), PathFinder.getMetaDataUnitPath());
    }

    public static MetaDataUnit getInstance() {
        if (instance == null) {
            String data = FileManager.readFile(PathFinder.getMetaDataUnitPath());
            if (data.isEmpty()) {
                FileManager.write("", PathFinder.getMetaDataUnitPath());
            }
            instance = new MetaDataUnit(data);
            instance.addToInt(META_DATA.TIMES_LAUNCHED, 1);
            if (!EXIT_OK.equalsIgnoreCase(instance.getValue(META_DATA.EXIT))) {
                instance.addToInt(META_DATA.CRASHED, 1);
            }
            instance.setValue(META_DATA.EXIT, "?");
        }
        return instance;
    }

    public static void setInstance(MetaDataUnit instance) {
        MetaDataUnit.instance = instance;
    }

    @Override
    public DataUnit<META_DATA> setValue(String name, String value) {
        super.setValue(name, value);
        write();
        return null;
    }

    @Override
    public Class<? extends META_DATA> getEnumClazz() {
        return META_DATA.class;
    }

    public enum META_DATA {
        LAST_PREGEN_LVL_INDEX,
        LAST_PREGEN_LVL_INDEX_MAP,

        TIMES_LAUNCHED,
        TIME_PLAYED,
        CRASHED, EXIT,


    }
}
