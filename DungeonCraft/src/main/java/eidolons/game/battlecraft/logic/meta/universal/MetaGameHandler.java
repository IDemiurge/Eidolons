package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/10/2017.
 */
public class MetaGameHandler<E extends MetaGame> {
    protected MetaGameMaster<E> master;

    public MetaGameHandler(MetaGameMaster master) {
        this.master = master;
    }

    public MetaGameMaster<E> getMaster() {
        return master;
    }

    public SpawnManager getPartyManager() {
        return master.getPartyManager();
    }

    public String getData() {
        return master.getData();
    }

    public E getMetaGame() {
        return (E) master.getMetaGame();
    }

    public DC_Game getGame() {
        return master.getGame();
    }

    public MetaInitializer getInitializer() {
        return master.getInitializer();
    }

    public MetaDataManager getMetaDataManager() {
        return master.getMetaDataManager();
    }

    public IntroFactory getIntroFactory() {
        return master.getIntroFactory();
    }

    public DialogueFactory getDialogueFactory() {
        return master.getDialogueFactory();
    }
}
