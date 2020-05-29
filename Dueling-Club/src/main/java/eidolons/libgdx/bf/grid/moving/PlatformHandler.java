package eidolons.libgdx.bf.grid.moving;

import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.GridUnitView;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PlatformHandler {
    /*
    defining in LE:
    special type of BLOCK?

    Logic
    - coordinates
    - stepping on and off

does it hold and wait for a while between it cycles?
in the most crazy variant, we could have a pendulum/circular rotation
     */
    Set<PlatformController> platforms = new LinkedHashSet<>();
    GridPanel grid;

    public PlatformController findByName(String name) {
        for (PlatformController platform : platforms) {
            if (platform.data.getValue(PlatformData.PLATFORM_VALUE.name).equalsIgnoreCase(name)) {
                return platform;
            }
        }
        return null;
    }

    public PlatformController get(Coordinates c) {
        for (PlatformController platform : platforms) {
            if (platform.coordinates.equals(c)) {
                return platform;
            }
        }
        return null;
    }

    public PlatformHandler(GridPanel grid) {
        //module cache? what about grid ?
        this.grid = grid;
        GuiEventManager.bind(GuiEventType.PLATFORM_CREATE, p -> {
            createPlatform((PlatformData) p.get());
        });

        GuiEventManager.bind(GuiEventType.INIT_PLATFORMS, p -> {
            init(p.get().toString());
        });
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p ->
        {
            GridUnitView view = (GridUnitView) p.get();

            PlatformController controller = view.
                    getPlatformController();

            for (PlatformController platform : platforms) {
                if (platform.coordinates == view.getUserObject().getCoordinates()) {
                    platform.entered(view.getUserObject());
                    view.setPlatformController(platform);
                    if (controller != null)
                        if (platform != controller) {
                            controller.left(view.getUserObject());
                        }
                    return;
                }
            }
            if (controller != null) //TODO check multi
            {
                controller.left(view.getUserObject());
                view.setPlatformController(null);
            }
        });
    }

    public void init(String platformData) {
        for (String substring : ContainerUtils.openContainer(platformData, StringMaster.AND_SEPARATOR)) {
            createPlatform(new PlatformData(substring));
        }
    }

    private void createPlatform(PlatformData data) {
        List<PlatformCell> cells = new ArrayList<>();
        for (String substring : ContainerUtils.openContainer(data.getValue(PlatformData.PLATFORM_VALUE.cells))) {
            Coordinates c = Coordinates.get(substring);
            PlatformCell cell = new PlatformCell(data.getType(), c.x, c.y, data.getDirection());
            cells.add(cell);
            cell.setUserObject(DC_Game.game.getCellByCoordinate(c));
            grid.addPlatform(cell);
        }
        platforms.add(new PlatformController(data, cells));

    }

    public void act(float delta) {
        for (PlatformController platform : platforms) {
            platform.act(delta);
        }
    }

    public Set<PlatformController> getPlatforms() {
        return platforms;
    }
}
