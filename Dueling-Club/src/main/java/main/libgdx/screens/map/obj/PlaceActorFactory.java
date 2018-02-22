package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.game.module.adventure.map.Place;
import main.libgdx.screens.map.obj.MapActor.MAP_OBJ_INFO_LEVEL;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PlaceActorFactory extends MapObjFactory<PlaceActor, Place> {
    private static PlaceActorFactory instance;

    public static PlaceActorFactory getInstance() {
        if (instance == null) {
            instance = new PlaceActorFactory();
        }
        return instance;
    }

    public static PlaceActor getPlace(Place party) {
        return getInstance().create(party);
    }

    public PlaceActor get(Place place) {
        PlaceActorParameters parameters = new PlaceActorParameters();
        parameters.mainIcon = TextureCache.getOrCreateR(place.getIconPath());
        parameters.place = place ;
        parameters.name = place.getName();
        parameters.position = new Vector2(
         place.getX(),
         place.getY());
        //TODO size?
        parameters.preview =
         TextureCache.getOrCreateR(
          place.getImagePath());
//        parameters.color=  GdxColorMaster.getColor(place.getLeader().getOwner().getFlagColor());

//        ToolTipManager ;
//      actor.addListener()

        PlaceActor actor = new PlaceActor(parameters);
        return actor;
    }

    public static class PlaceActorParameters {
        public TextureRegion mainIcon;
        public TextureRegion border;
        public MAP_OBJ_INFO_LEVEL visibility;
        public Vector2 position;
        public Color color;
        public String name;
        TextureRegion preview;
          Place place;
    }
}
