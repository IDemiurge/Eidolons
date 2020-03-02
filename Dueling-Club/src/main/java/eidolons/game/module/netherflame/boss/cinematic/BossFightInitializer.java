package eidolons.game.module.netherflame.boss.cinematic;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class BossFightInitializer {

    public static void initVisuals(IGG_Meta meta){

        GuiEventManager.trigger(GuiEventType.UPDATE_DUNGEON_BACKGROUND, "atlas.txt");

    }
}
