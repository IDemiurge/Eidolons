package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.data.ability.construct.VariableManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.threading.WaitMaster;

import java.util.List;

public class SpeechExecutor  {

    private final DialogueManager dialogueManager;
    DialogueHandler handler;
    DialogueContainer container;
    MetaGameMaster master;

    public SpeechExecutor(MetaGameMaster master, DialogueManager dialogueManager) {
        this.master = master;
        this.dialogueManager = dialogueManager;
    }

    public void execute(SpeechScript.SPEECH_ACTION speechAction, String value) {
        execute(speechAction, value, false);
    }
        public void execute(SpeechScript.SPEECH_ACTION speechAction, String value, boolean wait) {
        container = dialogueManager.getContainer();
        value = value.trim().toLowerCase();
            boolean ui = false;

        switch (speechAction) {
            case COMMENT:
                List<String> vars = VariableManager.getVarList(value);
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.COMMENT,
                        Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));

                break;
            case SCRIPT:
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));
                 vars = VariableManager.getVarList(value);
                master.getBattleMaster().getScriptManager().execute(func, Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));
                break;
            case WAIT:
                WaitMaster.WAIT(Integer.valueOf(value));
                break;
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case SOUND:
                DC_SoundMaster.playKeySound(value);
                break;
            case PORTRAIT_ANIM:
                //animate the portait displayed in UI?
                AnimMaster.onCustomAnim(value, true, 1, ()->{
//                    handler.continues();
                });
                break;
            case CAMERA_SET:
                Vector2 v=null ;
                switch (value) {
                    case "orig":
                       v= new Vector2(0, 0);
                        break;
                }
                GuiEventManager.trigger(GuiEventType.CAMERA_SET_TO, v);
                break;
            case CAMERA:
                switch (value) {
                    case "me":
                        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero());
                        break;
                }
                break;
            case UI_ANIM:
                 ui = true;
            case BG_ANIM:
                Float alpha=null ;
                switch (value) {
                    case "out":
                        alpha= 0f;
                        break;
                }
                if (alpha != null) {
                    ActionMaster.addAlphaAction(ui? container : container.getBgSprite() , 2, alpha);
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
        }
    }
}
