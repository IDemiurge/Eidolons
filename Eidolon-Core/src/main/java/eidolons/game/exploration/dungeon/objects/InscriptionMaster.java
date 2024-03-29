package eidolons.game.exploration.dungeon.objects;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.system.text.DescriptionTooltips;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.Map;

public class InscriptionMaster {
    static Map<String, Map<String, String>> maps = new HashMap<>();
    private final Map<String, String> map;
    String levelName;

    public InscriptionMaster(String levelName) {
        this.levelName = levelName;
        map = getTextMap(levelName);
    }

    public String getTextForInscription(BattleFieldObject obj) {
        String text = map.get(
                StringMaster.wrapInParenthesis(
                        obj.getCoordinates().toString()));
        if (text != null)
            return text;
        text = map.get(obj.getCoordinates().toString());
        if (text == null) {
            initCustomData(obj);
            text = map.get(obj.getCoordinates().toString());
        }
        text = DescriptionTooltips.getTutorialMap().get(text.toLowerCase());
        return text;
    }

    private void initCustomData(BattleFieldObject obj) {
//       TODO   wtf for (String substring : ContainerUtils.openContainer(
//                obj.getGame().getDungeon().getProperty(PROPS.COORDINATE_SCRIPTS))) {
//            String key = substring.split("=")[0];
//            String value = substring.split("=")[1];
//            map.put(key, value);
//        }
    }

    public static Map<String, String> getTextMap(String levelName) {
        Map<String, String> map = maps.get(levelName.toLowerCase());
        if (map != null) {
            return map;
        }
        String path = getSourcePath(levelName);
        String textMapSource = FileManager.readFile(path);
        map = MapMaster.createStringMap(true, textMapSource);
        maps.put(levelName.toLowerCase(), map);

        return map;
    }

    private static String getSourcePath(String levelName) {
//        switch (IGG_Demo.getByXmlLevelName(levelName)) {
//        }
        return PathFinder.getTextPathLocale() + "/messages/messages - " + StringMaster.cropFormat(levelName) + ".txt";
    }
}
