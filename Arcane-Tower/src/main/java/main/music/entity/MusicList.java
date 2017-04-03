package main.music.entity;

import main.data.DataManager;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.music.MusicCore;
import main.music.ahk.AHK_Master;
import main.music.gui.MusicMouseListener;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListObjChooser;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class MusicList extends MusicEntity {
    List<Track> tracks;
    private MusicMouseListener mouseListener;

    public MusicList(ObjType type) {
        super(type);
    }

    @Override
    public void setName(String name) {

        String path = getProperty(AT_PROPS.PATH);
        AHK_Master.listRenamed(path, name);
        setProperty(AT_PROPS.PATH, path.replace(StringMaster.getLastPathSegment(path), "\\" + name));

        super.setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void init() {
        toBase();
    }

    @Override
    public void toBase() {
        super.toBase();
        tracks = new LinkedList<>();
        String property = getProperty(AT_PROPS.TRACKS);
        for (ObjType t : DataManager.toTypeList(property, AT_OBJ_TYPE.TRACK)) {
            // duplicate preCheck
            tracks.add(MusicCore.getTrack(t));
        }
    }

    public void addTracks(List<Track> tracksFromLists) {
        // AT_PROPS.TRACKS
        tracks.addAll(tracksFromLists);
        resetPropertyFromList(AT_PROPS.TRACKS, tracks);
        toBase();
    }

    public void editTracks() {
        String result = ListChooser.chooseTypesNoPool(AT_OBJ_TYPE.TRACK,
                getProperty(AT_PROPS.TRACKS));
        // String result = ListChooser.chooseStrings(StringMaster
        // .openContainer(getProperty(AT_PROPS.TRACKS)));

        if (result != null) {
            setProperty(AT_PROPS.TRACKS, result);
        }
        MusicCore.saveList(this);
    }

    public List<Track> selectTracks() {
        return new ListObjChooser<Track>().selectMulti(getTracks());
        // List<String> tracks = MusicCore.getTracksFromList(function);
        // String chooseStrings = ListChooser.chooseStrings(tracks);
        // return chooseStrings;
    }

    public String getPath() {
        return getProperty(AT_PROPS.PATH);
    }

    public List<Track> getTracks() {
        if (!ListMaster.isNotEmpty(tracks)) {
            setTracks(MusicCore.getTracks(getProperty(AT_PROPS.PATH)));
        }
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        resetPropertyFromList(AT_PROPS.TRACKS, tracks);
    }

    public MusicMouseListener getMouseListener() {
        if (mouseListener == null) {
            mouseListener = MusicCore.getListenerMap().get(getName());
        }
        return mouseListener;
    }

    public void setMouseListener(MusicMouseListener musicMouseListener) {
        mouseListener = musicMouseListener;
    }

    public void setMusicType(String arg) {
        setProperty(AT_PROPS.MUSIC_TYPE, arg, true);
    }

    public void setGenre(String arg) {
        setProperty(AT_PROPS.MUSIC_GENRE, arg, true);

    }

    public void addTag(String arg) {
        addProperty(AT_PROPS.MUSIC_TAGS, arg, true);
        type.addProperty(AT_PROPS.MUSIC_TAGS, arg, true);
    }

    public void play() {
        MusicMouseListener.playM3uList(getPath(), null);

    }

    // public static void viewTracks(String function) {
    // List<Track> tracks = MusicCore.getTracksFromList(function);
    // ListChooser.chooseString(DataManager.convertToStringList(tracks));
    // }

}
