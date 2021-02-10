package eidolons.libgdx.screens.map.town.navigation.data;

import eidolons.macro.entity.town.TownPlace;
import eidolons.macro.map.Place;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigationMaster {

    Map<Navigable, NavigationDataSource> map = new HashMap<>();

    public NavigationDataSource getDataSource(Navigable navigable) {
        return map.get(navigable);
    }
        public Navigable getNavigable(Nested obj) {
            return createNavigable(obj);
    }

    private Navigable createNavigable(Nested obj) {
        Navigable navigable =null ;
        if (obj instanceof TownPlace){
            navigable = new NavigableTownPlace(
             ((TownPlace) obj));
        }
//        if (obj instanceof NPC){
//            navigable = new NavigableNPC(
//             ((NPC) obj));
//        }
        if (obj instanceof Place){
            navigable = new NavigableDungeon(((Place) obj));
        }

        addChildren(obj, navigable, map);
        return navigable;
    }

    private void addChildren(Nested obj, Navigable root, Map<Navigable, NavigationDataSource> map) {
        NavigationDataSource data = createDataSource(obj,
         root);
        map.put(root, data);
        for (Navigable navigable : root.getChildren()) {
            addChildren(obj, navigable, map);
        }
    }

    private NavigationDataSource createDataSource(Nested obj, Navigable root) {
        Set<Navigable> children = new LinkedHashSet<>();
        Set<Navigable> path = null;
        Set<Nested> places = obj.getNested();
        for (Nested place : places) {
            children.add(createNavigable(place));
        }
        root.setChildren(children);
        return new NavigationDataSource(root, children, path);
    }

    public static boolean isTestOn() {
        return false;
    }
}
