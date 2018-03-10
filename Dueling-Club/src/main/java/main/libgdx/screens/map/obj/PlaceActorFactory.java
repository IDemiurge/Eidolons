package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.game.module.adventure.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import main.game.module.adventure.map.Place;
import main.libgdx.screens.map.ui.tooltips.PlaceTooltip;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

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
        parameters.border = TextureCache.getOrCreateR(StringMaster.getAppendedImageFile(place.getIconPath(), " hl"));
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

        PlaceActor actor = new PlaceActor(parameters);
        actor.addListener(new PlaceTooltip(place, actor).getController());
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
