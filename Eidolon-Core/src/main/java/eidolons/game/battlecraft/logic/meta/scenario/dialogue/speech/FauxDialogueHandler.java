package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.core.game.DC_Game;

import java.util.ArrayList;

public class FauxDialogueHandler extends DialogueHandler {
    public FauxDialogueHandler() {
        super(DC_Game.game.getMetaMaster().getDialogueManager(), null, DC_Game.game, new ArrayList<>());
    }


}
