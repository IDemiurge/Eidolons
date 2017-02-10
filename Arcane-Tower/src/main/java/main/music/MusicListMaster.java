package main.music;

import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.music.ahk.AHK_Master;
import main.music.entity.MusicList;
import main.music.gui.MusicMouseListener;
import main.swing.generic.components.editors.lists.GenericListChooser;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.text.NameMaster;

public class MusicListMaster {

    public static void newList(MusicList list, boolean alt) {
        boolean fromSelected = !alt && list != null;
        /*
         * 1) select type -> music list
		 * 2) select via click
		 * 3) select genre -> track add dialog 
		 *  
		 */
        // create m3u in Custom ? hotkey?
        // addType -> save
        // what View will it belong to?
        // M3uGenerator.generateCustomM3Us()
        ObjType type = null;
        String tracks = "";
        String name = "";
        if (fromSelected) {
            if (!MusicMouseListener.getSelectedLists().isEmpty()) {
                name = "Merged ";
                for (MusicList sub : MusicMouseListener.getSelectedLists()) {
                    tracks += sub.getProperty(AT_PROPS.TRACKS, true) + ";";
                    name += sub.getName() + " ";
                }
            } else if (!MusicMouseListener.getSelectedTracks().isEmpty()) {
                name = (NameMaster.getUniqueVersionedName("Track Group", AT_OBJ_TYPE.MUSIC_LIST));// TODO
                tracks = StringMaster.constructEntityNameContainer(MusicMouseListener
                        .getSelectedTracks());
            } else {
                type = new ObjType(list.getType());
            }
        } else {
            // type = new ObjType(typeName, AT_OBJ_TYPE.MUSIC_LIST);
        }
        if (DialogMaster.confirm("Manual Edit?")) {
            GenericListChooser.setStaticTYPE(AT_OBJ_TYPE.TRACK);
            tracks = ListChooser.chooseStrings(StringMaster.openContainer(tracks));
        } else {
            // if (DialogMaster.confirm("Apply transformation?")){
            // int option = DialogMaster.optionChoice("", "Shuffle",
            // "Slice", "Criss-Cross");
            // //TODO
            // }
        }
        if (type == null) {
            if (DialogMaster.confirm("Input Name?")) {
                name = DialogMaster.inputText("Input Name!", name);
            }
            type = new ObjType(name, AT_OBJ_TYPE.MUSIC_LIST);
        }
        type.setProperty(AT_PROPS.TRACKS, tracks);

        String value = AHK_Master.CUSTOM_LISTS_FOLDER + type.getName() + ".m3u";
        type.setProperty(AT_PROPS.PATH, value);
        if (list != null) {
            type.setProperty(G_PROPS.HOTKEY, list.getProperty(G_PROPS.HOTKEY));
        } else {
            DialogMaster.inputText("HOTKEY?", "+#^!" + name.charAt(0));
        }

        if (list != null) {
            value = list.getProperty(AT_PROPS.MUSIC_TYPE);
            type.setProperty(AT_PROPS.MUSIC_TYPE, value);
            value = list.getProperty(AT_PROPS.MUSIC_GENRE);
            type.setProperty(AT_PROPS.MUSIC_GENRE, value);
        }
        list = new MusicList(type);
        // list.setName(name)
        // MusicCore.getList(name, keyPart, funcPart)
        DataManager.addType(type);
        MusicCore.saveList(list);
        MusicCore.saveAll();
        DialogMaster.inform("Created " + type.getName());
    }
}
