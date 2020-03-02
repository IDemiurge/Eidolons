package eidolons.game.battlecraft.logic.dungeon.puzzle.art;

import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.ManipulatorPuzzle;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ArtPuzzle extends ManipulatorPuzzle {

    @Override
    public void setup(PuzzleSetup... setups) {
//        for (PuzzleSetup setup : setups) {
//            setup.arg = getArtPiecePath();
//        }
        super.setup(setups);
    }

    public void init() {

    }

    @Override
    public void complete() {
        super.complete();
    }

    @Override
    public void activate() {
        super.activate();
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.MOSAIC);
    }

    protected int getRotateChance() {
        return 60;
    }
    public int getSoulforceBase() {
        return 10+ 60
                //TODO cyclic data!
                // getHeight()*getWidth()
                *getRotateChance()/50;
    }
    protected String getDefaultTitle() {
        return "Soul Fracture";
    }
}
