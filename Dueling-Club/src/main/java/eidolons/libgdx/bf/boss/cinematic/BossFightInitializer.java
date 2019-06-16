package eidolons.libgdx.bf.boss.cinematic;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.core.game.DC_Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class BossFightInitializer {

    public static void initVisuals(IGG_Meta meta){

        GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, "atlas.txt");

    }
}
