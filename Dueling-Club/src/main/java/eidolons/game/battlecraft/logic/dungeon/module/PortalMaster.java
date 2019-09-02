package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.texture.Sprites;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

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
        GuiEventManager.bind(GuiEventType.PORTAL_OPEN, p -> {
            Coordinates c = (Coordinates) p.get();
            boolean done = false;
            for (Portal portal : portalMap.keySet()) {
                if (portal.getCoordinates().equals(c)) {
                    portal.open = true;
                    done = true;
                    break;
                }
            }
            if (!done) {
                Portal portal = new Portal(FACING_DIRECTION.NORTH, c, PORTAL_TYPE.DARK);
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

    public void entered(Portal portal) {
        Unit unit = Eidolons.getMainHero();
        entered(unit, portal);
    }

    public void entered(Unit unit, Portal portal) {

        Portal to = portalMap.get(portal);
        if (to == null) {
            return;
        }
        portal.open = false;
        GuiEventManager.trigger(GuiEventType.UNIT_FADE_OUT_AND_BACK, unit);
        AnimMaster.onCustomAnim(getCloseAnim(portal, to), () -> {
            if (to.exitFacing != null) {
                unit.setFacing(to.exitFacing);
            }
            unit.setCoordinates(to.coordinates);
            to.open = true;
            GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
            AnimMaster.onCustomAnim(getOpenAnim(portal, to), () -> {
//                GuiEventManager.trigger(GuiEventType.BLACKOUT_OUT, 2.0f);
//                GuiEventManager.trigger(GuiEventType.BLACKOUT_IN, 1.4f);
//                GuiEventManager.trigger(GuiEventType.CAMERA_LAPSE_TO,  unit.getCoordinates());

                if (unit.isPlayerCharacter())
                    GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, unit);

            });
        });
    }

    public void unitMoved(Unit unit) {

        if (unit.isMine()) {
            for (Portal portal : portalMap.keySet()) {
                if (portal.open)
                    if (unit.getCoordinates().equals(portal.coordinates)) {
                        if (!isConfirm()) {
                            entered(portal);
                        } else {
                            EUtils.onConfirm("Enter the portal?", true, () -> entered(portal));
                        }
                    }

            }
        }

    }

    public boolean addPortal(String coordinate, String data) {
        if (VariableManager.removeVarPart(data.toLowerCase()).equals(PORTAL_KEY)) {
            FACING_DIRECTION facing = null;
            Coordinates to = null;
            FACING_DIRECTION facing2 = null;
            Coordinates from = Coordinates.get(coordinate);
            if (data.contains(",")) {
                to = Coordinates.get(VariableManager.getVar(data, 0));
                facing = FacingMaster.getFacing(VariableManager.getVar(data, 1));
                facing2 = FacingMaster.getFacing(VariableManager.getVar(data, 2));
            } else {
                if (VariableManager.getVars(data).isEmpty()) {
                    to = from;
                } else
                    to = Coordinates.get(VariableManager.getVars(data));
            }
            addPortal(from, to, facing, facing2);
            return true;
        }


        return false;
    }

    private void addPortal(Coordinates from, Coordinates to, FACING_DIRECTION facing, FACING_DIRECTION facing2) {
        Portal enter = createPortal(from);
        Portal exit;
        portalMap.put(enter, exit = createPortal(to));
        portalMap.put(exit, enter);
        enter.open = true;
        enter.exitFacing = facing;
        exit.exitFacing = facing2;
    }

    private Portal createPortal(Coordinates from) {
        Portal p = new Portal(null, from, PORTAL_TYPE.DARK);
        GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, p);
        return p;
    }


    private boolean isConfirm() {
        return false;
    }

    private String getCloseAnim(Portal portal, Portal to) {
        return Sprites.PORTAL_CLOSE;
    }

    private String getOpenAnim(Portal portal, Portal to) {
        return Sprites.PORTAL_OPEN;
    }

    public enum PORTAL_TYPE {
        OBLIVION, LIGHT, DARK
    }

    private class Portal extends GridObject {
        FACING_DIRECTION exitFacing;
        Coordinates coordinates;
        PORTAL_TYPE type;

        boolean open = false;
        boolean used;
        boolean facingDependent;

        public Portal(FACING_DIRECTION exitFacing, Coordinates coordinates, PORTAL_TYPE type) {
            super(coordinates, Sprites.PORTAL);
            this.exitFacing = exitFacing;
            this.coordinates = coordinates;
            this.type = type;
        }

        @Override
        protected void init() {
            super.init();
            sprite.setBlending(SuperActor.BLENDING.SCREEN);
        }

        public FACING_DIRECTION getExitFacing() {
            return exitFacing;
        }

        @Override
        public boolean checkVisible() {
            if (!open) {
                return false;
            }
            return super.checkVisible();
        }

        @Override
        protected boolean isClearshotRequired() {
            return false;
        }

        @Override
        protected float getFadeInDuration() {
            return 1.1f;
        }

        @Override
        protected float getFadeOutDuration() {
            return 2.3f;
        }

        @Override
        protected double getDefaultVisionRange() {
            return 8;
        }

        @Override
        protected int getFps() {
            return 14;
        }
    }
}
