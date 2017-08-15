package main.music;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.enums.StatEnums.MUSIC_TAGS;
import main.enums.StatEnums.MUSIC_TYPE;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.music.ahk.AHK_Master;
import main.music.entity.MusicList;
import main.music.entity.Track;
import main.music.gui.MusicListPanel;
import main.music.gui.MusicMouseListener;
import main.music.m3u.TrackReader;
import main.swing.generic.components.G_Panel;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;

public class MusicCore {
    public static final String M3U_PREFIX = "#EXTM3U";
    public static final String M3U_TRACK_PREFIX = "#EXTINF:";
    public static final MUSIC_TYPE[] types_day = {MUSIC_TYPE.PREPARATION, MUSIC_TYPE.IMMERSION,
            MUSIC_TYPE.BATTLE, MUSIC_TYPE.WALK, MUSIC_TYPE.AFTERMATH};
    public static final MUSIC_TYPE[] types_gym = {MUSIC_TYPE.JOG, MUSIC_TYPE.GYM,
            MUSIC_TYPE.GYM_MORNING, MUSIC_TYPE.GYM_NIGHT, MUSIC_TYPE.AWAKENING};
    public static final MUSIC_TYPE[] types_night = {MUSIC_TYPE.RUMINATION, MUSIC_TYPE.PREPARATION,
            MUSIC_TYPE.IMMERSION, MUSIC_TYPE.BATTLE, MUSIC_TYPE.AFTERMATH,};
    public static final MUSIC_TYPE[][] std_groups = {types_day, types_gym, types_night,};
    public static final MUSIC_TAGS[] tags_midday = {MUSIC_TAGS.FANTASY, MUSIC_TAGS.RPG,
            MUSIC_TAGS.GOODLY, MUSIC_TAGS.CELTIC, MUSIC_TAGS.LIGHT, MUSIC_TAGS.JOLLY,};
    public static final MUSIC_TAGS[] tags_afternoon = {MUSIC_TAGS.SHARP, MUSIC_TAGS.ACTION,
            MUSIC_TAGS.NORTH, MUSIC_TAGS.FANTASY, MUSIC_TAGS.RPG, MUSIC_TAGS.CELTIC,};
    public static final MUSIC_TAGS[] tags_dusk = {MUSIC_TAGS.SHARP, MUSIC_TAGS.ARCANE,
            MUSIC_TAGS.HEAVY, MUSIC_TAGS.COOL, MUSIC_TAGS.LIGHT, MUSIC_TAGS.JOLLY,};
    public static final MUSIC_TAGS[] tags_night = {MUSIC_TAGS.DEEP, MUSIC_TAGS.DARK,
            MUSIC_TAGS.SHARP, MUSIC_TAGS.TRAURIG, MUSIC_TAGS.FIERY, MUSIC_TAGS.EVIL,};
    public static final MUSIC_TAGS[][] std_tags = {tags_midday, tags_afternoon, tags_dusk,
            tags_night,};
    private static final AT_OBJ_TYPE[] musicTYPEs = {AT_OBJ_TYPE.TRACK, AT_OBJ_TYPE.MUSIC_LIST,
            AT_OBJ_TYPE.SCRIPT};
    public static boolean initMusicListTypes = false;
    static Map<String, Track> trackTypeMap = new HashMap<>();
    static Map<String, MusicList> listTypeMap = new HashMap<>();
    static List<MusicList> lastPlayed = new LinkedList<>();
    private static Map<String, MusicMouseListener> listenerMap = new HashMap<>();
    private static VALUE[] FILTER_VALUES = {
            AT_PROPS.MUSIC_TYPE,
            AT_PROPS.MUSIC_TAGS,
    };
    private static boolean filterOut;
    private static Set<Pair<VALUE, String>> filterValues = new HashSet<>();

    public static void addFilterValue() {
        int i = DialogMaster.optionChoice(FILTER_VALUES, "Filter value name?");
        String val = DialogMaster.inputText("Filter's value?");
        filterValues.add(new ImmutablePair<>(FILTER_VALUES[i], val));
    }

    public static void removeFilterValue() {

        int i = DialogMaster.optionChoice(filterValues.toArray(), "Filter to remove?");
        if (i == -1) {
            if (DialogMaster.confirm("remove all?")) {
                filterValues.clear();
                return;
            }
        }
        for (Pair<VALUE, String> sub : filterValues) {
            if (i == 0) {
                filterValues.remove(sub);
                break;
            }
            i--;
        }
    }

    public static Set<Pair<VALUE, String>> getFilterValues() {
        return filterValues;
    }


    public static boolean isFilterOut() {
        return filterOut;
    }

    public static void setFilterOut(boolean filterOut) {
        MusicCore.filterOut = filterOut;
    }

    public static MusicList findList(String name) {
        // String list = new SearchMaster<String>().find(name,
        // listTypeMap.keySet());
        // if (list == null)
        // list = new SearchMaster<String>().findClosest(name,
        // listTypeMap.keySet());
        ObjType type = DataManager.findType(name, AT_OBJ_TYPE.MUSIC_LIST);
        MusicList list = getList(type.getName());
        if (list == null) {
            list = new MusicList(type);
        }
        return list;
    }

    public static MusicList getList(String name) {
        return listTypeMap.get(name);
    }

    public static MusicList getList(ObjType t) {
        return getList(t.getName(), "", "");
    }

    public static MusicList getList(String name, String keyPart, String funcPart) {
        MusicList list = listTypeMap.get(name);
        if (list != null) {
            return list;
        }
        ObjType type = DataManager.getOrAddType(name, AT_OBJ_TYPE.MUSIC_LIST);
        // DataManager.ge
        String tracks = getTrackStringFromList(funcPart);
        type.setProperty(AT_PROPS.TRACKS, tracks);
        type.setProperty(AT_PROPS.PATH, funcPart);
        type.setProperty(G_PROPS.HOTKEY, keyPart);
        // type, genre?
        list = new MusicList(type);
        listTypeMap.put(name, list);
        return list;
    }

    public static List<Track> getTracksFromLists(MusicList... selectedLists) {
        List<Track> tracks = new LinkedList<>();
        for (MusicList sub : selectedLists) {
            tracks.addAll(getTracks(sub.getProperty(AT_PROPS.PATH)));
        }
        return tracks;
    }

    public static String getTrackStringFromList(String funcPart) {
        List<String> tracks;
        if (initMusicListTypes) {
            tracks = new TrackReader<>(String.class).getTracksFromList(funcPart);
        } else {
            tracks = DataManager.toStringList(getTracks(funcPart));
        }
        return StringMaster.constructContainer(tracks);
    }

    public static List<Track> getTracks(String funcPart) {
        return new TrackReader<>(Track.class).getTracksFromList(funcPart);
    }

    public static void saveList(MusicList list) {
        String contents = convertTracksToM3U(list.getProperty(AT_PROPS.TRACKS));
        FileManager.write(contents, list.getProperty(AT_PROPS.PATH));
        list.setParam(AT_PARAMS.TIME_LAST_MODIFIED, TimeMaster.getTime() + "", true);
    }

    public static void saveAll() {
        if (!MusicCore.initMusicListTypes) {
            // preCheck types read
        }
        for (AT_OBJ_TYPE t : musicTYPEs) {
            XML_Writer.writeXML_ForTypeGroup(t);
        }
    }

    public static String convertTracksToM3U(String contents) {
        String newContents = M3U_PREFIX;
        for (String substring : StringMaster.openContainer(contents)) {
            // ObjType type = DataManager.getType(substring, AT_OBJ_TYPE.TRACK);
            Track track = getTrack(substring);

            newContents += M3U_TRACK_PREFIX + track.getDuration() + "," + track.getArtist() + " - "
                    + track.getName() + StringMaster.NEW_LINE;
            newContents += track.getPath() + StringMaster.NEW_LINE;

            // #EXTM3U
            // #EXTINF:116,Two Steps From Hell - Magika
            // X:\Music\Two Steps From Hell\2007 - Dynasty\CD 2\16 Magika.mp3

        }
        return newContents;
    }

    public static Track getTrack(String name) {
        ObjType type = DataManager.getType(name, AT_OBJ_TYPE.TRACK);
        if (type == null) {
            type = DataManager.addType(name, AT_OBJ_TYPE.TRACK);
        }
        return getTrack(type);
    }

    public static Track getTrack(ObjType type) {
        if (type == null) {
            return null;
        }
        Track track = trackTypeMap.get(type.getName());
        if (track == null) {
            track = new Track(type);
            trackTypeMap.put(type.getName(), track);
        }
        return track;
    }

    public static void initDates() {
        for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
            File file = new File(type.getProperty(AT_PROPS.PATH));
            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                        BasicFileAttributes.class);
                type.setParam(AT_PARAMS.TIME_CREATED, attr.creationTime().toMillis() + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            long time = file.lastModified();
            type.setParam(AT_PARAMS.TIME_LAST_MODIFIED, time + "");
        }
        //
    }

    public static void processData() {
        for (ObjType sub : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
            if (!sub.checkProperty(AT_PROPS.MUSIC_TYPE)) {
                sub.setProperty(AT_PROPS.MUSIC_TYPE, MUSIC_TYPE.GYM.toString());
            }
        }

    }

    public static MusicListPanel getFilterView(String filterVal, PROPERTY filterProp) {
        Map<String, List<String>> map = new XLinkedMap<>();
        String name = filterProp.getName() + " by " + filterVal;
        MusicListPanel panel = new MusicListPanel(name, map);
        int maxSize = 0;
        int i = 0;
        for (String substring : StringMaster.openContainer(filterVal)) {
            List<String> list = new LinkedList<>();
            for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
                if (type.checkProperty(filterProp, substring)) {
                    list.add(type.getProperty(G_PROPS.HOTKEY) + "::"
                            + type.getProperty(AT_PROPS.PATH));
                }
            }
            if (maxSize < list.size()) {
                maxSize = list.size();
            }
            map.put(i + "", list);
            i++;

        }
        List<String> musicConsts = StringMaster.openContainer(filterVal);
        int customWrap = 2 + maxSize / 14;
        if (customWrap < 0) {
            customWrap = 0;
        }
        G_Panel v = panel.initView(map, false, customWrap, musicConsts);
        panel.setView(v);
        panel.setName(name);
        v.setName(name);
        return panel;
    }

    public static void newFilteredView(int option, Class<?> c) {
        MusicListPanel view = getGroupedView(option, c);
        if (view == null) {
            return;
        }
        AHK_Master.getPanel().getViewsPanel().addView(view.getView().getName(), view);
        AHK_Master.getPanel().getViewsPanel().viewClicked(view.getView().getName());

    }

    public static void newGroupView(int option) {
        newFilteredView(option, MUSIC_TYPE.class);
    }

    public static MusicListPanel getGroupedView(int option) {
        return getGroupedView(option, MUSIC_TYPE.class);
    }

    public static MusicListPanel getGroupedView(String viewName, Class<?> constClass) {
        return getGroupedView(getViewOption(constClass, viewName), constClass);
    }

    public static MusicListPanel getGroupedView(int option, Class<?> constClass) {
        if (option == -1) {
            return null;
        }
        PROPERTY filterProp = ContentManager.getPROP(constClass.getSimpleName());
        // String[] array = (AHK_Master.qwerty + " " +
        // AHK_Master.qwerty.substring(1).toUpperCase() + " 123")
        // .split(" ");
        String viewName = "All " + StringMaster.getWellFormattedString(constClass.getSimpleName());
        Map<String, List<String>> map = new XLinkedMap<>();
        List<String> musicConsts = EnumMaster.getEnumConstantNames(constClass);
        if (option != 0) {
            musicConsts = getMusicConsts(constClass, option);
            viewName = getViewName(constClass, option);

        }
        Map<ObjType, String> multiPropMap = new HashMap<>();
        loop:
        for (String g : musicConsts) {
            // map.
            List<String> list = new LinkedList<>();
            // for (MusicList musList : listTypeMap.values()) {
            typeLoop:
            for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {
                if (type.checkProperty(filterProp, g)) {
                    if (filterProp.isContainer()) {
                        containerLoop:
                        for (String sub : StringMaster.openContainer(type
                                .getProperty(filterProp))) {
                            for (String c : musicConsts) {
                                if (StringMaster.compare(c, sub)) {
                                    if (c.equalsIgnoreCase(g)) {
                                        break containerLoop;
                                    } else {
                                        multiPropMap.put(type, c);
                                        continue typeLoop;
                                    }
                                }
                            }
                        }
                    }
                    list.add(AHK_Master.getScriptLineForList(type));
                }
            }
            map.put(g, list);
        }
        for (ObjType sub : multiPropMap.keySet()) {
            for (String c : map.keySet()) {
                if (multiPropMap.get(sub).equals(c)) {
                    map.get(c).add(AHK_Master.getScriptLineForList(sub)

                    );
                }
            }
        }
        for (String sub : map.keySet()) {
            map.put(sub, new LinkedList<>(new LinkedHashSet<>(map.get(sub))));
        }
        // TODO adapt to max list size also
        int wrap = 5 - map.size() / 2;
        MusicListPanel musicListPanel = new MusicListPanel("", null);
        G_Panel view =
                // AHK_Master.getPanel()
                musicListPanel.initView(map, false, wrap, musicConsts);
        musicListPanel.setView(view);
        view.setName(viewName);
        return musicListPanel;
    }

    public static int getViewOption(Class<?> constClass, String name) {
        for (int i = 0; i < 10; i++) {
            if (name.equals(getViewName(constClass, i))) {
                return i;
            }
        }
        return 0;

    }

    public static String getViewName(Class<?> constClass, int option) {

        switch (constClass.getSimpleName()) {
            case "MUSIC_TAGS":
                if (option == 1) {
                    return "Tags Day";
                }
                if (option == 2) {
                    return "Tags Afternoon";
                }
                if (option == 3) {
                    return "Tags Dusk";
                }
                if (option == 4) {
                    return "Tags Night";
                }
                break;
            case "MUSIC_TYPE":
                if (option == 1) {
                    return "Day";
                }
                if (option == 2) {
                    return "Gym";
                }
                if (option == 3) {
                    return "Night";
                }
                break;
        }
        return null;
    }

    public static List<String> getMusicConsts(Class<?> constClass, int option) {
        switch (constClass.getSimpleName()) {
            case "MUSIC_TAGS":
                if (option == 1) {
                    return ListMaster.toStringList(tags_midday);
                }
                if (option == 2) {
                    return ListMaster.toStringList(tags_afternoon);
                }
                if (option == 3) {
                    return ListMaster.toStringList(tags_dusk);
                }
                if (option == 4) {
                    return ListMaster.toStringList(tags_night);
                }
                break;
            case "MUSIC_TYPE":
                if (option == 1) {
                    return ListMaster.toStringList(types_day);
                }
                if (option == 2) {
                    return ListMaster.toStringList(types_gym);
                }
                if (option == 3) {
                    return ListMaster.toStringList(types_night);
                }
                break;
        }
        return null;
    }

    public static List<MusicList> getMusicLists() {
        return getMusicLists(DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST));
    }

    public static List<MusicList> getMusicLists(List<ObjType> list) {
        List<MusicList> filtered = new LinkedList<>();
        for (ObjType sub : list) {
            filtered.add(getList(sub));
        }
        return filtered;
    }

    public static List<String> getMusicConsts(boolean group) {
        List<String> defaults = EnumMaster.getEnumConstantNames(group ? MUSIC_TYPE.class
                : MUSIC_TAGS.class);
        return defaults;
    }

    public static Color getTextColor() {
        return Color.black;
    }

    public static List<MusicList> getLastPlayed() {
        return lastPlayed;
    }

    public static Map<String, MusicMouseListener> getListenerMap() {
        return listenerMap;
    }

    public static Map<String, MusicList> getListTypeMap() {
        return listTypeMap;
    }

    public void init() {
        for (ObjType sub : DataManager.getTypes(AT_OBJ_TYPE.MUSIC_LIST)) {

        }
    }

}
