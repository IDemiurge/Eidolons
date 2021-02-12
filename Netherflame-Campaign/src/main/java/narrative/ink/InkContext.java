package narrative.ink;

import narrative.ink.InkEnums.INK_STD_CONTEXT;
import eidolons.macro.MacroGame;
import eidolons.macro.global.Campaign;
import eidolons.macro.global.World;
import eidolons.macro.map.Region;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;

/**
 * Created by JustMe on 11/20/2018.
 *
 * e.g. for Hollowfern:
 * region=Ravenwood
 * town_name ...
 * type = village
 *
 * where and how created?
 * in a separate txt perhaps or in an enum ?
 */
public class InkContext {

    private final MacroGame game;
    INK_STD_CONTEXT base;
    Campaign campaign;
    World world;

    public InkContext(INK_STD_CONTEXT base, Campaign campaign, World world) {
        this.base = base;
        this.campaign = campaign;
        this.world = world;
        game = world.getGame();
    }

    public DAY_TIME getTime() {
        return game.getTime();
    }

    public Region getRegion() {
        return getCampaign().getRegion();
    }

    public INK_STD_CONTEXT getBase() {
        return base;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public World getWorld() {
        return world;
    }
}
