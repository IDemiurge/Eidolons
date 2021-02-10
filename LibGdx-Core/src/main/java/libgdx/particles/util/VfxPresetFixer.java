package libgdx.particles.util;

import main.system.PathUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.List;

public class VfxPresetFixer {

    public static void main(String[] args) {
        List<File> presets = EmitterMaster.getVfxPresets(false);
        for (File preset : presets) {
            fixPreset(preset.getPath());
        }

    }

    public static void fixPreset(String path) {
        String data = EmitterPresetMaster.getInstance().getData(path);
//        String imgPaths = EmitterPresetMaster.getInstance().getImagePath(path);
//        String cropped = cropCrap(imgPaths);
//        data = EmitterPresetMaster.getInstance().getModifiedData(data,
//                EmitterPresetMaster.EMITTER_VALUE_GROUP.Image_Paths, cropped);
//        data = formatPath(data);
//        data = data.replace(PathFinder.getImagePath(), "");
        data=  data.replace("c:/eidolons/battlecraft/resources/res/img/", "c:/eidolons/eidolons/resources/res/img");
        data=  data.replace("C:/Eidolons/battlecraft/resources/res/img/", "c:/eidolons/eidolons/resources/res/img");
        FileManager.write(data, path);
    }

    private static String cropCrap(String imgPaths) {
        String cropped = "";
        for (String line : StringMaster.splitLines(imgPaths, false)) {
            cropped += PathUtils.cropAllBefore(
                    "resources"
                    , line);
        }
        return cropped;
    }


}
