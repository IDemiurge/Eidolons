package eidolons.libgdx.particles.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.particles.EmitterPools;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/26/2017.
 */
public class EmitterPresetMaster {
    public static String separator = " - ";
    public static String value_separator = ": ";
    private static EmitterPresetMaster instance;
    private static boolean spriteEmitterTest;
    private static Map<EmitterActor, String> mods = new HashMap<>();
    Map<String, String> imagePathMap;
    private Map<String, String> map = new HashMap<>();
    private String lowHighMinMax = "lowMin lowMax highMin highMax";

    public EmitterPresetMaster() {
        instance = this;
    }

    public static EmitterPresetMaster getInstance() {
        if (instance == null) {
            instance = new EmitterPresetMaster();
        }
        return instance;
    }

    public static String save(EmitterActor last, String prefix) {
        return save(last, prefix, null);
    }

    public static String save(EmitterActor last, String prefix, String name) {
        String c;
        Writer output = new StringWriter();
        last.getEffect().getEmitters().forEach(e -> {
            try {
                e.save(output);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        c = output.toString();
        String suffix = mods.get(last);
        if (suffix == null) {
            suffix = "";
        } else {
            String buffer = suffix;
            suffix = " ";
            for (String substring : ContainerUtils.open(buffer, value_separator)) {
                suffix += StringMaster.cropFormat(
                        PathUtils.getLastPathSegment(substring)) + " ";
            }
        }
        String newName = (name != null) ? name :
                FileManager.getUniqueVersion(FileManager.getFile(PathFinder.getVfxPath() + prefix +
                        "/" + last.path + suffix));

        String path = StringMaster.replace(true, last.path,
                PathFinder.getVfxPath(), "").replace(prefix, "");
        String pathAndName = PathFinder.getVfxPath() + prefix +
                "/" +
                PathUtils.cropLastPathSegment(path) +
                newName;
        XML_Writer.write(c, pathAndName);

        return pathAndName;
    }

    public String searchImage(File directory, String imageName) {
        //search recursively
        String imagePath = null;
        for (File d : FileManager.getFilesFromDirectory(directory.getPath(), true)) {
            if (d.isDirectory()) {
                imagePath = searchImage(d, imageName);
            }
            if (d.isFile()) {
                if (d.getName().equalsIgnoreCase(imageName)) {
                    imagePath = d.getPath();
                }
            }
            if (imagePath != null) {
                //updatePreset();
                return imagePath;
            }
        }
        return imagePath;
    }

    public String findImagePath(String path) {
        String imagePath = getImagePath(path);
        if (imagePath.split("\n").length > 1) {
            //TODO how to handle this? multiple images...
            return imagePath;
        }

        FileHandle file = Gdx.files.internal(imagePath);
//        if (!imagePath.isEmpty())
            if (file.exists()) {
                return imagePath;
            }
        try {
            imagePath = FileManager.formatPath(imagePath, true);
            String afterClassPath = imagePath.split("img/")[1];
            file = Gdx.files.internal(PathFinder.getImagePath() + afterClassPath);
            if (file.exists()) {
                return PathFinder.getImagePath() + afterClassPath;
            }
        } catch (Exception e) {

        }
        String name = PathUtils.getLastPathSegment(imagePath);
        //generic
        imagePath = PathFinder.getParticleImagePath();
        file = Gdx.files.internal(imagePath + "/" + name);
        if (file.exists()) {
            return imagePath + "/" + name;
        }

        //raw
        //        String suffix = StringMaster.replaceFirst(path, PathFinder.getParticlePresetPath(), "");
        //        suffix = StringMaster.cropLastPathSegment(suffix);
        //        imagePath  = suffix;
        //        file = Gdx.files.internal(imagePath);
        //        if (file.exists())
        //            return imagePath;

        imagePath += "particles/";
        file = Gdx.files.internal(imagePath + "/" + name);
        if (file.exists()) {
            return imagePath + "/" + name;
        }

        imagePath =
                PathFinder.removeSpecificPcPrefix(
                        EmitterPresetMaster.getInstance().getImagePath(path));
        file = Gdx.files.internal(imagePath);
        if (file.exists()) {
            return imagePath;
        }

        imagePath = PathUtils.cropLastPathSegment(imagePath);
        file = Gdx.files.internal(imagePath);
        if (file.exists()) {
            return imagePath;
        }


        if (spriteEmitterTest) {
            //            effect.getEmitters().forEach(e -> {
            String randomPath = FileManager.getRandomFile(PathFinder.getSpritesPathFull() +
                    "impact/").getPath();
            return randomPath;
            //        ((Emitter) e).offset(20, "scale");
            //        e.setImagePath(randomPath);
            //        e.setPremultipliedAlpha(false);
            //            });
        }

        imagePath = searchImage(FileManager.getFile(PathFinder.getVfxPath()), name);
        if (StringMaster.isEmpty(imagePath)) {
            imagePath = searchImage(FileManager.getFile(PathFinder.getSpritesPathFull()), name);
        }
        if (StringMaster.isEmpty(imagePath)) {
            LogMaster.log(1, imagePath + " - NO IMAGE FOUND FOR VFX: " + path);
        }

        return imagePath;
    }

    public String getImagePath(String path) {
        String imgPath = null;
        //        if (imagePathMap != null) {
        //            imgPath = imagePathMap.get(path.toLowerCase());
        //            if (imgPath != null)
        //                return imgPath;
        //        }
        imgPath = getValueFromGroup(path, EMITTER_VALUE_GROUP.Image_Path, null);
        if (StringMaster.isEmpty(imgPath)) {
            imgPath = getValueFromGroup(path, EMITTER_VALUE_GROUP.Image_Paths, null);
        }
        if (imgPath.contains(StringMaster.NEW_LINE)) {
            imgPath = imgPath.split(StringMaster.NEW_LINE)[0];
        }
        //        if (imgPath.contains("\n")) {
        //            imgPath = imgPath.split("\n")[0];
        //        }
        return imgPath;

    }

    public String getValueFromGroup(String path, EMITTER_VALUE_GROUP group, String value) {
        String text = getGroupTextFromPreset(path, group);
        if (text == null) {
            return "";
        }
        if (value == null) {
            return text.trim();
        }
        for (String substring : ContainerUtils.open(text, "\n")) {
            if (substring.split(value_separator)[0].equalsIgnoreCase(value)) {
                return substring.split(value_separator)[1].trim();
            }
        }
        return null;
    }

    public EmitterActor getModifiedEmitter(String path, int offset, EMITTER_VALUE_GROUP... groups) {
        String mods = "";
        for (int i = 0; i < groups.length; i++) {
            mods += groups[i].name + value_separator + offset + ";";
        }
        return getModifiedEmitter(path, false, mods);
    }

    public EmitterActor getModifiedEmitter(String path,
                                           boolean write, String modvals) {
        EmitterActor actor = EmitterPools.getEmitterActor(path);

        for (String sub : ContainerUtils.open(modvals)) {
            String name = sub.split(value_separator)[0];
            String val = sub.split(value_separator)[1];
            EMITTER_VALUE_GROUP group = new EnumMaster<EMITTER_VALUE_GROUP>().retrieveEnumConst(EMITTER_VALUE_GROUP.class, name);
            name = group.getFieldName();
            if (group != null) {
                if (group.container) {
                    if (!val.contains("set")) {
                        actor.getEffect().offset(name, val);
                        continue;
                    } else {
                        val.replace("set", "");
                    }
                }
                actor.getEffect().set(name, val);

            }
        }

        String newPath = save(actor, "modified");
        actor = EmitterPools.getEmitterActor(newPath);
        //        if (!write) delete();
        mods.put(actor, modvals);
        return actor;
    }

    private String setValue(EMITTER_VALUE_GROUP group, String val, String data) {
        String text = getGroupText(data, group);
        for (String substring : ContainerUtils.open(text, "\n")) {
            //            if (predicate())
            //                data = producer()
            if (substring.split(value_separator)[0].equalsIgnoreCase(group.name)) {
                String newString = substring.replace(substring.split(value_separator)[1], val);
                data = data.replace(substring, newString);
            }
        }
        if (group == EMITTER_VALUE_GROUP.Image_Path
                && text.startsWith("\n")) {
            data = data.replace(text, "\n" + val);
        } else
            data = data.replace(text, val);
        return data;
    }

    String offsetValue(EMITTER_VALUE_GROUP group, double offset, String data) {
        String text = getGroupText(data, group);
        List<Pair<String, String>> entryList = getGroupEntries(text);
        for (Pair<String, String> p : entryList) {
            if (lowHighMinMax.contains(p.getKey().toString())) {
                double newValue = NumberUtils.getDouble(p.getValue()) + offset;
                text = text.replace(p.getKey() + value_separator + p.getValue(),
                        p.getKey() + value_separator + String.valueOf(
                                newValue));
            }
        }
        data = data.replace(getGroupText(data, group), text);
        return data;
    }

    private List<Pair<String, String>> getGroupEntries(String text) {
        List<Pair<String, String>> list = new ArrayList<>();
        for (String substring : ContainerUtils.open(text, "\n")) {
            String[] parts = substring.split(value_separator);
            list.add(new ImmutablePair<>(parts[0], parts[1]));
        }
        return list;
    }

    public void clone(String path, String newName) {
        String content = getData(path);
        path = PathUtils.cropLastPathSegment(path);
        XML_Writer.write(content, path, newName);
        //    map.put()

    }

    public String getGroupTextFromPreset(String path, EMITTER_VALUE_GROUP group) {
        String data = getData(path);
        if (data == null) {
            return null;
        }
        return getGroupText(data, group);
    }

    public String getGroupText(String data, EMITTER_VALUE_GROUP group) {
        //TODO for multi emitters?!
        String[] parts = data.split(group.name + "s - \n");
        String valuePart = null;

        if (parts.length == 1) {
            parts = data.split(group.name + "s -");
        }
        if (parts.length == 1) {
            parts = data.split(group.name + " -\n");
        }
        if (parts.length == 1) {
            parts = data.split(group.name + " -");
        }
        if (parts.length > 1) {
            valuePart = parts[1];
        }
        if (parts.length > 2) {
            valuePart = valuePart.split("\n\n")[0];
        }
        if (valuePart == null)
            return null;
        String text = valuePart.split("- ")[0];
        return text;
    }

    public String getModifiedData(String data, EMITTER_VALUE_GROUP image_path, String newVal) {
        return setValue(image_path, newVal, data);
    }

    public String readAndModifyData(String path, EMITTER_VALUE_GROUP image_path, String newVal) {
        String data = getData(path);
        return setValue(image_path, newVal, data);

    }

    public String getData(String path) {
        path = path.toLowerCase();
        path = PathUtils.addMissingPathSegments(path, PathFinder.getVfxPath());
        String data = map.get(path);
        if (data == null) {
            data = FileManager.readFile(path);
            if (!StringMaster.isEmpty(data)) {
                map.put(path, data);
            }
        }
        return data;
    }

    public void init() {
        imagePathMap = new HashMap<>();
        for (GenericEnums.VFX sub : GenericEnums.VFX.values()) {
            String path = null;
            try {
                path = findImagePath(sub.getPath());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                continue;
            }
            imagePathMap.put(sub.getPath().toLowerCase(), path);
        }
    }

    private void saveLastAs() {

    }

    private void getRandom(String path) {

    }

    private void getGenerate(String path) {
    /*
    std variants

    rotation
    speed
    emission
    duration
    angle
    alpha
    image
    size
    color(s)


     */
    }


    public enum EMITTER_VALUE_GROUP {
        Image_Path(),
        Angle(true),
        Rotation(true),
        Scale,
        PremultipliedAlpha,
        Percentage_Of_Lagging_Particles, Image_Paths, Options;
        private boolean container;
        private String name;

        EMITTER_VALUE_GROUP() {
            name = StringMaster.getWellFormattedString(name());
        }

        EMITTER_VALUE_GROUP(Boolean container) {
            this();
            this.container = container;
        }

        public String getFieldName() {
            return StringMaster.getCamelCase(name());
        }
    }

    public enum EMITTER_VALUE_SHORTCUTS {
        EMISSION_AMOUNT,
        PARTICLE_VELOCITY,
        PARTICLE_DURATION,

    }
}

/*
Untitled
- Delay -
active: false
- Duration -
lowMin: 3000.0
lowMax: 3000.0
- Count -
min: 0
max: 200
- Emission -
lowMin: 0.0
lowMax: 0.0
highMin: 250.0
highMax: 250.0
relative: false
scalingCount: 1
scaling0: 0.5882353
timelineCount: 1
timeline0: 0.0
- Life -
lowMin: 0.0
lowMax: 0.0
highMin: 500.0
highMax: 1000.0
relative: false
scalingCount: 3
scaling0: 1.0
scaling1: 1.0
scaling2: 0.3
timelineCount: 3
timeline0: 0.0
timeline1: 0.66
timeline2: 1.0
- Life Offset -
active: false
- X Offset -
active: false
- Y Offset -
active: false
- Spawn Shape -
shape: point
- Spawn Width -
lowMin: 0.0
lowMax: 0.0
highMin: 0.0
highMax: 0.0
relative: false
scalingCount: 1
scaling0: 1.0
timelineCount: 1
timeline0: 0.0
- Spawn Height -
lowMin: 0.0
lowMax: 0.0
highMin: 0.0
highMax: 0.0
relative: false
scalingCount: 1
scaling0: 1.0
timelineCount: 1
timeline0: 0.0
- Scale -
lowMin: 0.0
lowMax: 0.0
highMin: 62.0
highMax: 62.0
relative: false
scalingCount: 1
scaling0: 1.0
timelineCount: 1
timeline0: 0.0
- Velocity -
active: true
lowMin: 0.0
lowMax: 0.0
highMin: 30.0
highMax: 300.0
relative: false
scalingCount: 1
scaling0: 0.5686275
timelineCount: 1
timeline0: 0.0
- Angle -
active: true
lowMin: 312.0
lowMax: 312.0
highMin: -115.0
highMax: -360.0
relative: false
scalingCount: 3
scaling0: 0.0
scaling1: 0.6862745
scaling2: 1.0
timelineCount: 3
timeline0: 0.0
timeline1: 0.29452056
timeline2: 0.84931505
- Rotation -
active: true
lowMin: 0.0
lowMax: 0.0
highMin: 110.0
highMax: 110.0
relative: false
scalingCount: 4
scaling0: 0.019607844
scaling1: 1.0
scaling2: 0.039215688
scaling3: 0.98039216
timelineCount: 4
timeline0: 0.0
timeline1: 0.28767124
timeline2: 0.5890411
timeline3: 0.9931507
- Wind -
active: true
lowMin: 0.0
lowMax: 0.0
highMin: 0.0
highMax: 0.0
relative: false
scalingCount: 1
scaling0: 0.6862745
timelineCount: 1
timeline0: 0.0
- Gravity -
active: true
lowMin: 0.0
lowMax: 0.0
highMin: 0.0
highMax: 0.0
relative: false
scalingCount: 1
scaling0: 1.0
timelineCount: 1
timeline0: 0.0
- Tint -
colorsCount: 9
colors0: 1.0
colors1: 0.41960785
colors2: 0.0
colors3: 0.8156863
colors4: 0.039215688
colors5: 0.039215688
colors6: 1.0
colors7: 0.64705884
colors8: 0.047058824
timelineCount: 3
timeline0: 0.0
timeline1: 0.3316062
timeline2: 1.0
- Transparency -
lowMin: 0.0
lowMax: 0.0
highMin: 1.0
highMax: 1.0
relative: false
scalingCount: 4
scaling0: 0.0
scaling1: 1.0
scaling2: 0.75
scaling3: 0.0
timelineCount: 4
timeline0: 0.0
timeline1: 0.2
timeline2: 0.8
timeline3: 1.0
- Options -
attached: true
continuous: true
aligned: false
additive: true
behind: true
premultipliedAlpha: false
- Image Path -
/Y:/Google Drive/shareRes/resources/img/mini/sfx/images/64/daemon x.jpg

 */
