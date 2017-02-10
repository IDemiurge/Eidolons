package main.libgdx.anims.particles;

import javafx.util.Pair;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.StringMaster;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 1/26/2017.
 */
public class EmitterPresetMaster {
    private static EmitterPresetMaster instance;
    String separator = " - ";
    String value_separator = ": ";
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

    public static void save(EmitterActor last) {
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
        XML_Writer.write(c, PathFinder.getSfxPath() + "custom\\" + last.path);
    }

    public String getImagePath(String path) {

        return
         getValueFromGroup(path, EMITTER_VALUE_GROUP.Image_Path, null);
    }

    public String getValueFromGroup(String path, EMITTER_VALUE_GROUP group, String value) {
        String text = getGroupTextFromPreset(path, group);
        if (text == null) {
            return "";
        }
        if (value == null) {
            return text;
        }
        for (String substring : StringMaster.openContainer(text, "\n")) {
            if (substring.split(value_separator)[0].equalsIgnoreCase(value)) {
                return substring.split(value_separator)[1];
            }
        }
        return null;
    }

    public EmitterActor getModifiedEmitter(String path, int offset, EMITTER_VALUE_GROUP... groups) {
        String[] array = new String[groups.length];
        for (int i = 0; i < groups.length; i++) {
            array[i] = groups[i].name + value_separator + offset;
        }
        return getModifiedEmitter(path, false, array);
    }

    public EmitterActor getModifiedEmitter(String path,
                                           boolean write, String... modvals) {

        String newName =StringMaster.getLastPathSegment(path) +" modified";
//crop format!

            clone(path, newName);

        String data = getData(path);
        for (String sub : modvals) {

            String name = sub.split(value_separator)[0];
            String val = sub.split(value_separator)[1];

            EMITTER_VALUE_GROUP group = new EnumMaster<EMITTER_VALUE_GROUP>().retrieveEnumConst(EMITTER_VALUE_GROUP.class, name);
            if (group != null) {
                if (group.container) {
                    data = offsetValue(group, StringMaster.getDouble(val), data);
                } else {
                    data = setValue(group, val, data);
                }
            }
        }
        path = StringMaster.cropLastPathSegment(path);

            XML_Writer.write(data, path, newName);

        String newPath = path + newName;
        EmitterActor actor = EmitterPools.getEmitterActor(newPath);
//        new EmitterActor(newPath);
        if (!write){
            //delete
        }
        return actor;

    }

    private String setValue(EMITTER_VALUE_GROUP group, String val, String data) {
        String text = getGroupText(data, group);
        for (String substring : StringMaster.openContainer(text, "\n")) {
//            if (predicate())
//                data = producer()
            if (substring.split(value_separator)[0].equalsIgnoreCase(group.name)) {
                String newString = substring.replace(substring.split(value_separator)[1], val);
                data = data.replace(substring, newString);
            }
        }
        data =  data.replace(getGroupText(data, group), text);
        return data;
    }

    String offsetValue(EMITTER_VALUE_GROUP group, double offset, String data) {
        String text = getGroupText(data, group);
        List<Pair<String, String>> entryList = getGroupEntries(text);
        for (Pair<String, String> p : entryList) {
            if (lowHighMinMax.contains(p.getKey().toString())) {
                double newValue = StringMaster.getDouble(p.getValue()) + offset;
                text = text.replace(p.getKey() + value_separator + p.getValue(),
                 p.getKey() + value_separator + String.valueOf(
                  newValue));
            }
        }
        data =  data.replace(getGroupText(data, group), text);
        return data;
    }

    private List<Pair<String, String>> getGroupEntries(String text) {
        List<Pair<String, String>> list = new LinkedList<>();
        for (String substring : StringMaster.openContainer(text, "\n")) {
            String[] parts = substring.split(value_separator);
            list.add(new Pair<>(parts[0], parts[1]));
        }
        return list;
    }

    public void clone(String path, String newName) {
        String content = getData(path);
        path = StringMaster.cropLastPathSegment(path);
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
        String[] parts = data.split(group.name + " - \n");
        String valuePart=null ;

        if (parts.length == 1) {
            parts = data.split(group.name + " -\n");
        }
        if (parts.length > 1) {
            valuePart = parts[1];
        }
        if (parts.length>2){
            valuePart = valuePart.split("\n\n")[0];
        }

        String text = valuePart.split("- ")[0];
        return text;
    }

    private String getData(String path) {
        path = path.toLowerCase();
        String data = map.get(path);
        if (data == null) {
            data = FileManager.readFile(path);
            if (!StringMaster.isEmpty(data)) {
                map.put(path, data);
            }
        }
        return data;
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

    public enum EMITTER_VALUE_SHORTCUTS {
        EMISSION_AMOUNT,
        PARTICLE_VELOCITY,
        PARTICLE_DURATION,

    }

    public enum EMITTER_VALUE_GROUP {
        Image_Path(),
        Angle(true),
        Rotation(true),
        Scale,;
        private Boolean container;
        private String name;

        EMITTER_VALUE_GROUP() {
            name = StringMaster.getWellFormattedString(name());
        }

        EMITTER_VALUE_GROUP(Boolean container) {
            this();
            this.container = container;
        }
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
