package main.libgdx.bf.overlays;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 9/12/2017.
 */
public class WallMap extends Group{
    private Map<Coordinates, List<DIRECTION>> wallMap = new HashMap<>();
    private Map<Coordinates, List<DIRECTION>> diagonalJoints = new HashMap<>();

    List<Image> images;
    public WallMap() {
    bindEvents();
    //idea - just statically modify wall actors?
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_WALL_MAP, p->{

        });
    }

}
