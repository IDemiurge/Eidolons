package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;

public class SpeechExecutor  {

    DialogueHandler handler;
    DialogueContainer container;
    MetaGameMaster master;

    public SpeechExecutor(MetaGameMaster master) {
        this.master = master;
    }

    public void execute(SpeechScript.SPEECH_ACTION speechAction, String value) {
        switch (speechAction) {
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case SOUND:
                DC_SoundMaster.playKeySound(value);
                break;
            case PORTRAIT_ANIM:
                break;
            case BG_ANIM:
                Float alpha=null ;
                switch (value) {
                    case "out":
                        alpha= 0f;
                }
                if (alpha != null) {

                    ActionMaster.addAlphaAction(container , 2, alpha);
                    return;
                }
//                Sprites.getSprite(value);
//
//                container.setBg(value);
//
//                container.getBg();

//                container.addAnimation();
                //finish anim if interrupt?
                // force wait?
                //

                break;
            case CUSTOM_ANIM:
                AnimMaster.onCustomAnim(value, true, 1, ()->{
//                    handler.continues();
                });
                break;
            case timed:
                break;
            case SCRIPT:
//       master.getBattleMaster().getScriptManager().execute(value);
                break;
        }
    }
}
