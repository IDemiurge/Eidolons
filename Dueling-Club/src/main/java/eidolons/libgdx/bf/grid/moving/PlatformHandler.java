package eidolons.libgdx.bf.grid.moving;

import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
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
            if (platform.contains(c)) {
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
        GuiEventManager.bind(GuiEventType.PLATFORM_REMOVE, p -> {
            removePlatform((String) p.get());
        });

        GuiEventManager.bind(GuiEventType.INIT_PLATFORMS, p -> {
            init(p.get().toString());
        });
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p ->
        {
            UnitGridView view = (UnitGridView) p.get();

            PlatformController controller = view.
                    getPlatformController();

            for (PlatformController platform : platforms) {
                if (platform.contains(view.getUserObject().getCoordinates())) {
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

    private void removePlatform(String s) {
        PlatformController byName = findByName(s);
        if (byName == null) {
            main.system.auxiliary.log.LogMaster.log(1, "No such platform to remove: " + s);
            return;
        }
        platforms.remove(byName);
    }

    public void init(String platformData) {
        for (String substring : ContainerUtils.openContainer(platformData, StringMaster.VERTICAL_BAR)) {
            createPlatform(new PlatformData(substring));
        }
    }

    private void createPlatform(PlatformData data) {
        List<PlatformCell> cells = new ArrayList<>();
        for (String substring : ContainerUtils.openContainer(data.getValue(PlatformData.PLATFORM_VALUE.cells), ",")) {
            Coordinates c = Coordinates.get(substring);
            PlatformCell cell = create(c, data);

            cells.add(cell);
            cell.setUserObject(DC_Game.game.getCellByCoordinate(c));
        }
        PlatformDecor visuals = grid.addPlatform(cells, data);
        platforms.add(new PlatformController(data, cells, visuals));

    }

    protected PlatformCell create(Coordinates c, PlatformData data) {
        return new PlatformCell(data.getType().getTexture(), c.x, c.y, data.getDirection());
    }

    public void act(float delta) {
        for (PlatformController platform : platforms) {
            platform.act(delta);
        }
    }

    public Set<PlatformController> getPlatforms() {
        return platforms;
    }

    public PlatformDecor createCellVisuals(List<PlatformCell> cells, PlatformData data) {
        return new PlatformDecor(data.getType(), cells);
    }
}
