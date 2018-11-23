package eidolons.libgdx.screens.map.town.navigation.data;

import eidolons.game.module.adventure.entity.npc.NPC;
import eidolons.macro.entity.MacroObj;
import eidolons.macro.entity.town.TownPlace;
import eidolons.macro.map.Place;
import main.data.XLinkedMap;

import java.util.*;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigationMaster {

    Map<Navigable, NavigationDataSource> map = new HashMap<>();

    public NavigationDataSource getDataSource(Navigable navigable) {
        return map.get(navigable);
    }
        public Navigable getNavigable(MacroObj obj) {
        Navigable root = createNavigable(obj);
        return root;
    }

    private Navigable createNavigable(MacroObj obj) {
        Navigable navigable =null ;
        if (obj instanceof TownPlace){
            navigable = new NavigableTownPlace(
             ((TownPlace) obj));
        }
        if (obj instanceof NPC){
            navigable = new NavigableNPC(
             ((NPC) obj));
        }
        if (obj instanceof Place){
            navigable = new NavigableDungeon(((Place) obj));
        }

        addChildren(obj, navigable, map);
        return navigable;
    }

    private void addChildren(MacroObj obj, Navigable root, Map<Navigable, NavigationDataSource> map) {
        NavigationDataSource data = createDataSource(obj,
         root);
        map.put(root, data);
        for (Navigable navigable : root.getChildren()) {
            addChildren(obj, navigable, map);
        }
    }

    private NavigationDataSource createDataSource(MacroObj obj, Navigable root) {
        Set<Navigable> children = new LinkedHashSet<>();
        Set<Navigable> path = null;
        Set<MacroObj> places = obj.getNested();
        for (MacroObj place : places) {
            children.add(createNavigable(place));
        }
        root.setChildren(children);
        NavigationDataSource data = new NavigationDataSource(root, children, path);
        return data;
    }
}
