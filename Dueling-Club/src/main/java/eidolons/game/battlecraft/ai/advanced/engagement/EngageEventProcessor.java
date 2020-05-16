package eidolons.game.battlecraft.ai.advanced.engagement;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.rules.VisionEnums.ENGAGEMENT_LEVEL;
import main.content.enums.rules.VisionEnums.PLAYER_STATUS;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.SoundFx;
import main.system.sound.SoundMaster;
import main.system.text.LogManager;

import static main.system.auxiliary.log.LogMaster.log;

public class EngageEventProcessor {
    private DC_Game game;
    private ExplorationMaster master;

    public EngageEventProcessor(ExplorationMaster master) {
        this.game = master.getGame();
        this.master = master ;
    }

    public void process(EngageEvent event) {
        if (EngageEvents.isLogged()){
            log(1, "Event to process:" + event);
            game.getLogManager().log(event.toString());
        }
        if (event.type != null)
        switch (event.type) {
            case engagement_change:
                engagementChange( event.level, event.source);
                break;
            case status_change:
                statusChange(event.status, event.arg);
                break;
            case ai_status_change:
                //for ai - ambush/stalk
                break;
            case precombat:
                if (event.source.getAI().getGroup().getEngagementLevel() == ENGAGEMENT_LEVEL.PRE_COMBAT) {
                    return;
                }
                comment(event.source, event.type);
                event.source.getAI().getGroup().setEngagementLevel(ENGAGEMENT_LEVEL.PRE_COMBAT);
            case combat_start:
                statusChange(PLAYER_STATUS.COMBAT, event.arg);
                master.switchExplorationMode(false);
                //++ events -                 largeText();
                //      :: Text, music, camera, ui atb, pace!
                //        GuiEventManager.trigger(GuiEventType.SHOW_LARGE_TEXT,
                //                ImmutableList.of("Encounter", "The Dummies", 3f));
                break;
            case combat_end:
                master.switchExplorationMode(true);
                break;
            case view_anim:
                GuiEventManager.triggerWithParams(GuiEventType.GRID_OBJ_ANIM, event.arg, event.source, event.graphicData);
                break;
            case sound:
                DC_SoundMaster.playEffectSound((SoundMaster.SOUNDS) event.arg, event.source);
                break;
            case popup:
                popup(event.arg.toString());
                break;
        }
        if (event.logMsg != null) {
            game.getLogManager().log((LogManager.LOGGING_DETAIL_LEVEL) event.arg, event.logMsg);
        }
        if (event.soundPath != null) {
            Vector2 v = GridMaster.getCenteredPos(event.c);
            float vol = 1f;
            float delay = 0f;
            SoundFx sound = new SoundFx(event.soundPath, vol, delay, v);
            DC_SoundMaster.getSoundPlayer().playNow(sound);
        }
        if (event.popupText != null) {
            popup(event.popupText);
        }
        if (EngageEvents.isLogged()){
            log(1, "Event processed:" + event);
        }
    }

    private void popup(String popupText) {
        GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,  popupText);
    }

    private void comment(Unit source, EngageEvent.ENGAGE_EVENT type) {
        String comment = "I'll getcha!"; //TODO
        GuiEventManager.triggerWithParams(GuiEventType.SHOW_COMMENT_PORTRAIT, source, comment);
    }

    //called every now and then?
    private void propagateStatus(Unit source) {
        for (Unit member : source.getAI().getGroup().getMembers()) {
            //alerted vs alarmed - seen or been told
            // member.getAI().getEngagementLevel().getSeverity()
        }
    }

    private void engagementChange(ENGAGEMENT_LEVEL level, Unit source) {
        source.getAI().setEngagementLevel(level);
    }

    private void statusChange(PLAYER_STATUS combat, Object arg) {
        PlayerStatus status = new PlayerStatus(combat, (Integer) arg);
        game.getDungeonMaster().getExplorationMaster().setPlayerStatus(status);
        GuiEventManager.trigger(GuiEventType.PLAYER_STATUS_CHANGED,
                status);
    }


    private void forceAlarm(Unit source, Coordinates lastSeen) {
        //        Order orders= new Order(CONTENT_CONSTS2.ORDER_TYPE.MOVE, "");
        //        ai.setCurrentOrder(orders);
    }
}
