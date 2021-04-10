package libgdx.map.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import eidolons.macro.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import eidolons.macro.map.Place;
import libgdx.map.ui.tooltips.PlaceTooltip;
import libgdx.texture.TextureCache;
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

    public static PlaceActor getPlace(Place place) {
        return getInstance().create(place);
    }

    public PlaceActor get(Place place) {
        PlaceActorParameters parameters = new PlaceActorParameters();
        parameters.mainIcon = TextureCache.getOrCreateR(place.getIconPath());
        parameters.border = TextureCache.getOrCreateR(StringMaster.getAppendedImageFile(place.getIconPath(), " hl"));
        parameters.place = place;
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
        private TextureRegion mainIcon;
        private TextureRegion border;
        private MAP_OBJ_INFO_LEVEL visibility;
        private Vector2 position;
        private Color color;
        private String name;
        private TextureRegion preview;
        private Place place;


        public TextureRegion getMainIcon() {
            return mainIcon;
        }

        public TextureRegion getBorder() {
            return border;
        }

        public MAP_OBJ_INFO_LEVEL getVisibility() {
            return visibility;
        }

        public Vector2 getPosition() {
            return position;
        }

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public TextureRegion getPreview() {
            return preview;
        }

        public Place getPlace() {
            return place;
        }
    }
}
