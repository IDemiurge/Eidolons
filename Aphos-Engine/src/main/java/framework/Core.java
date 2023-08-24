package framework;

import elements.content.enums.EnumFinder;
import elements.exec.preset.ExecPresets;

/**
 * Created by Alexander on 8/23/2023
 */
public class Core {
    public static void init(){
        ExecPresets.initAllPresets();
        EnumFinder.initEnumMap();
    }
}
