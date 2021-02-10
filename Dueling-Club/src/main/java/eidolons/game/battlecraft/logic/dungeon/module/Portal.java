package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.core.EUtils;
import eidolons.game.module.cinematic.CinematicLib;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

class Portal extends GridObject {
    public boolean oneWay; //TODO
    FACING_DIRECTION exitFacing;
    Coordinates coordinates;
    Portal pair;
    PortalMaster.PORTAL_TYPE type;

    boolean open = false;
    boolean used;
    boolean facingDependent;

    public Portal(FACING_DIRECTION exitFacing, Coordinates coordinates, PortalMaster.PORTAL_TYPE type, boolean oneWay) {
        super(coordinates, Sprites.PORTAL);
        this.exitFacing = exitFacing;
        this.coordinates = coordinates;
        this.type = type;
        this.oneWay = oneWay;
    }

    @Override
    public float getOffsetY() {
        return 0;//-sprite.getHeight() / 2;
    }

    @Override
    public void init() {
        super.init();
        sprite.setBlending(GenericEnums.BLENDING.SCREEN);
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


    protected boolean isConfirm() {
        return false;
    }

    protected String getCloseAnim() {
        return Sprites.PORTAL_CLOSE;
    }

    protected String getOpenAnim() {
        return Sprites.PORTAL_OPEN;
    }

    public void entering(Unit unit) {
        if (!isConfirm()) {
            entered(unit);
        } else {
            EUtils.onConfirm("Enter the portal?", true, () -> entered(unit));
        }
    }

    public void entered(Unit unit) {
        Portal to = pair;
        used = true;
        if (oneWay)
            open = false;

        CinematicLib.run(CinematicLib.StdCinematic.ENTER_PORTAL, unit);

        GraphicData data = new GraphicData("x:" + getOffsetX() + ";y:" + getOffsetY());
        AnimMaster.onCustomAnim(data,
                "",
                getCloseAnim(), () -> {
                    to.exited(unit);
                });
    }

    protected void exited(Unit unit) {
        if (exitFacing != null) {
            unit.setFacing(exitFacing);
        }
        unit.setCoordinates(coordinates);
        unit.getGame().getMovementManager().moved(unit, true);
        open = true;
        GuiEventManager.trigger(GuiEventType.UNIT_MOVED, unit);
        AnimMaster.onCustomAnim(pair.getOpenAnim(), () -> {
            CinematicLib.run(CinematicLib.StdCinematic.EXIT_PORTAL, unit);

            if (oneWay)
                open = false;
        });
    }

    public void setPair(Portal pair) {
        this.pair = pair;
    }
}
