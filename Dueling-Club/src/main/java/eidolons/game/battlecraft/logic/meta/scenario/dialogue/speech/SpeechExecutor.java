package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.dungeon.module.PortalMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueContainer;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.SimpleAnim;
import eidolons.libgdx.anims.fullscreen.FullscreenAnimDataSource;
import eidolons.libgdx.anims.fullscreen.FullscreenAnims.FULLSCREEN_ANIM;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.shaders.post.PostFxUpdater.POST_FX_TEMPLATE;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.List;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SPEECH_ACTION.CAMERA_SET;

public class SpeechExecutor {

    private final DialogueManager dialogueManager;
    DialogueHandler handler;
    DialogueContainer container;
    MetaGameMaster master;
    private int waitOnEachLine;

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
        List<String> vars = VariableManager.getVarList(value);
        value = VariableManager.removeVarPart(value);
        boolean ui = false;
        Vector2 v=null ;
//        if (WAIT)
//        if (dialogue.getTimeBetweenLines()!=0) {
//            WaitMaster.WAIT(dialogue.getTimeBetweenLines());
////                dialogueManager.getSpeechExecutor().execute(SpeechScript.SPEECH_ACTION.WAIT, );
//        }

        if (waitOnEachLine != 0) {
           WAIT(waitOnEachLine);
        }

        switch (speechAction) {
            case WAIT_OFF:
            case WAIT_EACH:
//                dialogueManager.getd
                if (value.isEmpty() || !NumberUtils.isInteger(value)) {
                    waitOnEachLine=0;
                } else
                    waitOnEachLine = Integer.valueOf(value);
                break;
            case TIME:
                container.getCurrent().setTime(new Float( Integer.valueOf(value)));
                break;
            case WAIT:
                WAIT(Integer.valueOf(value));
                break;
            case SPRITE:
                SimpleAnim simpleAnim = new SimpleAnim(value, () -> {
                });
                  v = GridMaster.getCenteredPos(Coordinates.get(vars.get(0)));
                simpleAnim.setOrigin(v);
//                simpleAnim.setBlending(b);
//                simpleAnim.setFps(f);
                AnimMaster.onCustomAnim(simpleAnim);
                break;
            case FULLSCREEN:
                FULLSCREEN_ANIM anim = new EnumMaster<FULLSCREEN_ANIM>().retrieveEnumConst(FULLSCREEN_ANIM.class, vars.get(0));
                FullscreenAnimDataSource data = new FullscreenAnimDataSource(anim, 1,
                        FACING_DIRECTION.NORTH, SuperActor.BLENDING.SCREEN);
                GuiEventManager.trigger(GuiEventType.SHOW_FULLSCREEN_ANIM, data);
                break;

            case BLACKOUT:
                GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 3);
                break;
            case POSTFX:
                POST_FX_TEMPLATE template = new EnumMaster<POST_FX_TEMPLATE>().retrieveEnumConst(POST_FX_TEMPLATE.class, value);
                if (template != null) {
                    GuiEventManager.trigger(GuiEventType.POST_PROCESSING, template);
                } else GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
                break;
            case ACTION:
                vars.add(value);
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.ACTION,
                        Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));
                break;
            case SCRIPT:
                COMBAT_SCRIPT_FUNCTION func = new EnumMaster<COMBAT_SCRIPT_FUNCTION>().
                        retrieveEnumConst(COMBAT_SCRIPT_FUNCTION.class, VariableManager.removeVarPart(value));
                master.getBattleMaster().getScriptManager().execute(func, Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));
                break;
            case MOMENT:
                MusicMaster.playMoment(value);
                break;
            case MUSIC:
                MusicMaster.getInstance().overrideWithTrack(value);
                break;
            case SOUND:
                DC_SoundMaster.playKeySound(value);
                break;
            case PORTRAIT_ANIM:
                //animate the portait displayed in UI?
                AnimMaster.onCustomAnim(value, true, 1, () -> {
//                    handler.continues();
                });
                break;
            case REVEAL_AREA:
                break;
            case PORTAL:
                switch (value) {
                    case "loop":
                    case "open":
                    case "close":
//                        PortalMaster
                        break;
                }
            case COMMENT:
                master.getBattleMaster().getScriptManager().execute(COMBAT_SCRIPT_FUNCTION.COMMENT,
                        Eidolons.getMainHero().getRef(), vars.toArray(new String[vars.size()]));
                break;

            case ZOOM:
                CameraMan.MotionData motion = new CameraMan.MotionData(Float.valueOf(value)/100, Float.valueOf(vars.get(0))/1000, Interpolation.swing);
                GuiEventManager.trigger(GuiEventType.CAMERA_ZOOM, motion);
                break;
            case CAMERA_SET:
            case CAMERA:
                switch (value) {
                    case "me":
                        v = GridMaster.getCenteredPos(Eidolons.getMainHero().getCoordinates());
                        break;
                    case "orig":
                        v = new Vector2(0, 0);
                        break;
                }
                if (v == null) {
                    v = GridMaster.getCenteredPos(Coordinates.get(value));
                }
                //TODO offset considering the UI...?
                if(speechAction==CAMERA_SET){
                    GuiEventManager.trigger(GuiEventType.CAMERA_SET_TO, v);
                } else {
                    GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO, v);
                }
                break;
            case ANIM:
            case UI_ANIM:
                ui = true;
            case BG_ANIM:
                Float alpha = null;
                switch (value) {
                    case "out":
                        alpha = 0f;
                        break;
                    case "in":
                        alpha =1f;
                        break;
                }
                if (alpha != null) {
                    ActionMaster.addAlphaAction(ui ? container : container.getBgSprite(), 2, alpha);
                    return;
                }
                break;
            case GRID_OBJ:
//GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, new LinkedGridObject());
                break;

            case UNIT:
                //same find-alg!
                String unitdata = vars.get(0);
                Unit unit = master.getGame().getAiManager().getScriptExecutor().findUnit(Eidolons.getMainHero().getRef(), unitdata);
                if (unit != null) {
                    switch (value) {
                        case "remove":
                            unit.kill(unit, false, true);
                            break;
                        case "show":
                            unit.setHidden(false);
                            break;
                        case "hide":
                            unit.setHidden(true);
                            break;
                        case "die":
                            unit.kill();
                            break;
                    }
                }
                break;
            case DIALOG:
                switch (value) {
                    case "end":
                    case "done":
                        container.done();
                        break;
                    case "continue":
                    case "next":
//                    TODO do we need it?    container.getCurrent().tryNext();
                        break;
                }
//                container.setOnDoneCallback();
                break;
        }
    }

    private void WAIT(int millis) {
//        if (!CoreEngine.isIDE())
        GdxMaster.setLoadingCursor();
        WaitMaster.WAIT(millis);
        GdxMaster.setDefaultCursor();
    }

    public void execute(String text) {
        SpeechScript script = new SpeechScript(text, master);
        script.execute();
    }
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

