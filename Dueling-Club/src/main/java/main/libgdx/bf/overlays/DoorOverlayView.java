package main.libgdx.bf.overlays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates.DIRECTION;
import main.libgdx.bf.SuperActor;

import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 9/23/2017.
 */
public class DoorOverlayView extends SuperActor {
//add to UnitView?
    List<DIRECTION> fragmentDirections;
    Map<DIRECTION, Image> fragmentImageMap; //direction to open
    boolean rotateOnOpen;
    boolean open;
//WALL_STYLE style;

    //sync with Lock overlay?

    private void initImageMap() {
        fragmentImageMap.clear();
        clear();
        fragmentDirections.forEach(direction -> {
            Image image = new Image(
//            WallMap.getTexture(direction, style);
            );
            Vector2 v = getPosition(direction);
            //rotate?
            //scale by x or y
            image.scaleBy(direction.isVertical() ? 0.5f : 1,
             !direction.isVertical() ? 0.5f : 1);
            image.setPosition(v.x, v.y);
            addActor(image);
            fragmentImageMap.put(direction, image);
        });
    }

    private Vector2 getPosition(DIRECTION direction) {
        return null;
    }


    public void setFragmentDirections(List<DIRECTION> fragmentDirections) {
        this.fragmentDirections = fragmentDirections;
        initImageMap();
    }

    public void open(DIRECTION from) {
        //mind that there should be kind of scissors or something
        //rotation will be different for different images...
        //
        //animation! ;)
    }
}
