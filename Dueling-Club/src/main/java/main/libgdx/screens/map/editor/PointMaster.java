package main.libgdx.screens.map.editor;

import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.libgdx.utils.GdxDialogMaster;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2018.
 */
public class PointMaster {

    Map<String, Coordinates> map;
    private String last;

    public PointMaster() {
        load();
    }

    public void save() {
        String s = "";
        for (String substring : map.keySet()) {
            s += substring + StringMaster.wrapInParenthesis(map.get(substring).toString())
             + StringMaster.getSeparator();
        }
        FileManager.write(s, getPath());
    }

    public void added() {
        for (String substring : map.keySet()) {
            GuiEventManager.trigger(MapEvent.LOCATION_ADDED
             , new ImmutablePair<>(substring, map.get(substring)));
        }
    }

    private void load() {
        map = new LinkedHashMap<>();
        for (String substring : StringMaster.openContainer(FileManager.readFile(getPath()))) {
            map.put(VariableManager.removeVarPart(substring),
             new Coordinates(true, VariableManager.getVar(substring)));
        }

    }

    private String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroPath(), "map", "locations.txt");
    }

    public void removeClosest(int x, int y) {

    }

    public void clicked(int x, int y) {
//        Eidolons.getLauncher().getScreen().

        new Thread(new Runnable() {
            public void run() {
                String name = GdxDialogMaster.inputText("Enter location's name", last);
                if (name != null) last = name;
                else return;
                map.put(name, new Coordinates(true, x, y));
                GuiEventManager.trigger(MapEvent.LOCATION_ADDED, new ImmutablePair(name, map.get(name)));
            }
        }, " thread").start();
    }

    public Coordinates getCoordinates(String point) {
        return map.get(point);
    }
}
