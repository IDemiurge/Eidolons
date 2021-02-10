package libgdx.screens.map.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.screens.map.MapScreen;
import libgdx.texture.TextureCache;
import eidolons.macro.MacroGame;
import eidolons.macro.map.Place;
import libgdx.screens.map.layers.MapMoveLayers;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.Flags;
import main.system.util.DialogMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2018.
 */
public class MapPointMaster {

    Map<String, Coordinates> map;
    private String last;
    private static MapPointMaster instance;

    private MapPointMaster() {
        if (Flags.isMapEditor()) {
            GuiEventManager.bind(MapEvent.LOCATION_ADDED, p -> {
                Pair<String, Coordinates> pair = (Pair<String, Coordinates>) p.get();
                TextureRegion region = TextureCache.getOrCreateR(STD_IMAGES.MAP_PLACE.getPath());
                Actor actor = new Image(region);
                actor.addListener(new ValueTooltip(pair.getKey()).getController());
                Coordinates c = pair.getValue();
                actor.setPosition(c.x - region.getRegionWidth() / 2, c.y - region.getRegionHeight() / 2);
                MapScreen.getInstance().getObjectStage().getPointsGroup().
                 addActor(actor);
            });
        }
        load();
    }

    public static MapPointMaster getInstance() {
        if (instance == null) {
            instance=new MapPointMaster();
        }
        return instance;
    }


    public Place getPlaceForPoint(String point) {
        //        Map<String, Place> map = new HashMap<>();
        //getVar closest?
        float minDistance = Float.MAX_VALUE;
        Coordinates c = MacroGame.getGame().getPointMaster().getCoordinates(point);
        Place place = null;
        if (c==null )
            return null;
        for (Place sub : MacroGame. getGame().getPlaces()) {
            float distance = new Vector2(c.x, c.y).dst(new Vector2(sub.getX(), sub.getY()));
            if (distance < minDistance) {
                minDistance = distance;
                place = sub;
            }
            //can we not attach click listeners to emtiterActors?!
        }
        return place;
    }
    public void save() {
        StringBuilder s = new StringBuilder();
        for (String substring : map.keySet()) {
            s.append(substring).append(StringMaster.wrapInParenthesis(map.get(substring).toString())).append(StringMaster.getSeparator());
        }
        FileManager.write(s.toString(), getPath());
    }

    public void added() {
        for (String substring : map.keySet()) {
            GuiEventManager.trigger(MapEvent.LOCATION_ADDED
             , new ImmutablePair<>(substring, map.get(substring)));
        }
    }

    private void load() {
        map = new LinkedHashMap<>();
        for (String substring : ContainerUtils.openContainer(FileManager.readFile(getPath()))) {
            map.put(VariableManager.removeVarPart(substring),
             Coordinates.get(true, VariableManager.getVar(substring)));
        }
        for (MapMoveLayers.MAP_POINTS sub : MapMoveLayers.MAP_POINTS.values()) {
            map.put(StringMaster.format(sub.name()),
             Coordinates.get(true, sub.x, sub.y));
        }
    }

    private String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroXmlPath(), "map", "locations.txt");
    }

    public void removeClosest(int x, int y) {

    }

    public void clicked(int x, int y) {
//        Eidolons.getLauncher().getScreen().

        new Thread(new Runnable() {
            public void run() {
                String name = DialogMaster.inputText("Enter location's name", last);
                if (name != null) last = name;
                else return;
                map.put(name, Coordinates.get(true, x, y));
                GuiEventManager.trigger(MapEvent.LOCATION_ADDED, new ImmutablePair(name, map.get(name)));
            }
        }, " thread").start();
    }

    public Coordinates getCoordinates(String point) {
        return map.get(point);
    }
}
