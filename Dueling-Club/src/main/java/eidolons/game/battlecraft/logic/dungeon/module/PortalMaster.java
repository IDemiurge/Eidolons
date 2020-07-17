package eidolons.game.battlecraft.logic.dungeon.module;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.Flags;

import java.util.LinkedHashMap;
import java.util.Map;

public class PortalMaster extends DungeonHandler {
    private static final String PORTAL_KEY = "portal";
    Map<Portal, Portal> portalMap = new LinkedHashMap<>();

    /**
     * support for multi-directional?
     */
    public PortalMaster(DungeonMaster master) {
        super(master);
        if (!TextureCache.atlasesOn)
        if (!Flags.ONE_FRAME_SPRITES) {
            Assets.get().getManager().load(Sprites.PORTAL_OPEN, TextureAtlas.class);
            Assets.get().getManager().load(Sprites.PORTAL_CLOSE, TextureAtlas.class);
        }
        GuiEventManager.bind(GuiEventType.PORTAL_OPEN, p -> {
            Coordinates c = (Coordinates) p.get();
            boolean done = false;
            boolean oneWay = false;
            for (Portal portal : portalMap.keySet()) {
                if (portal.getCoordinates().equals(c)) {
                    portal.open = true;
                    done = true;
                    oneWay = portal.oneWay;
                    break;
                }
            }
            if (!done) {
                Portal portal = new Portal(FACING_DIRECTION.NORTH, c, PORTAL_TYPE.DARK, oneWay);
                portalMap.put(portal, portal);
                portal.open = true;
            }
        });
        GuiEventManager.bind(GuiEventType.PORTAL_CLOSE, p -> {
            Coordinates c = (Coordinates) p.get();
            for (Portal portal : portalMap.keySet()) {
                if (portal.getCoordinates().equals(c)) {
                    portal.open = false;
                }
            }
        });
    }

    public void unitMoved(Unit unit) {
        if (unit.isMine()) {
            for (Portal portal : portalMap.keySet()) {
                if (portal.open)
                    if (unit.getCoordinates().equals(portal.coordinates)) {
                        //TODO area?
                       portal.entering(unit);
                    }

            }
        }

    }

    public boolean addPortal(Coordinates from, String data) {
        //format:
        FACING_DIRECTION facing = null;
        Coordinates to = null;
        FACING_DIRECTION facing2 = null;
        Boolean oneWay = null;
        for (String substring : ContainerUtils.openContainer(data, ",")) {
            substring = substring.trim();
            if (to == null) {
                to = Coordinates.get(substring);
                continue;
            }
            if (facing == null) {
                facing = FacingMaster.getFacing(substring);
                continue;
            }
            if (facing2 == null) {
                facing2 = FacingMaster.getFacing(substring);
                continue;
            }

            if (oneWay == null) {
                oneWay = new Boolean(substring);
                continue;
            }
            }
            if (oneWay == null) {
                oneWay = false;
            }
            addPortal(from, to, facing, facing2, oneWay);
            return true;
        }

    private boolean addOmniPortal(Coordinates from, String data) {
        new OmniPortal(from, data).addToGrid();
        return true;
    }

    private void addPortal (Coordinates from, Coordinates to, FACING_DIRECTION facing, FACING_DIRECTION facing2
        , Boolean oneWay){
            Portal enter = createPortal(from,oneWay);
            Portal exit;
            portalMap.put(enter, exit = createPortal(to,oneWay));
            portalMap.put(exit, enter);
            enter.open = true;
            enter.exitFacing = facing;
            exit.exitFacing = facing2;
        }

        private Portal createPortal(Coordinates from, Boolean oneWay){
            Portal p = new Portal(null, from, PORTAL_TYPE.DARK, oneWay);
            GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, p);
            return p;
        }


        public void init (Map < Coordinates, CellScriptData > textDataMap){
            for (Coordinates c : textDataMap.keySet()) {
                String data = textDataMap.get(c).getValue(CellScriptData.CELL_SCRIPT_VALUE.portals);
                if (!data.isEmpty()) {
                    addPortal(c, data);
                }
                  data = textDataMap.get(c).getValue(CellScriptData.CELL_SCRIPT_VALUE.omni_portals);
                if (!data.isEmpty()) {
                    addOmniPortal(c, data);
                }
            }
        }

        public enum PORTAL_TYPE {
            OBLIVION, LIGHT, DARK
        }

}
