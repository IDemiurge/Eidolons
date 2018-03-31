package eidolons.system.audio;

import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * Created by JustMe on 8/30/2017.
 */
public class SoundController {
    //bind on gui events? 
    DC_SoundMaster soundMaster;

    public SoundController(DC_SoundMaster soundMaster) {
        this.soundMaster = soundMaster;
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
            try {
                String sound = getEventSound((Event) p.get());
                if (sound != null)
                    DC_SoundMaster.play(sound);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
//        GuiEventManager.bind(CREATE_RADIAL_MENU, p-> playGuiSound(p,CREATE_RADIAL_MENU));
//        GuiEventManager.bind(GuiEventType.MUSIC_PAUSE, p->playGuiSound(p,MUSIC_PAUSE));
//        GuiEventManager.bind(GuiEventType.GAME_FINISHED, p->playGuiSound(p,GAME_FINISHED));
//        GuiEventManager.bind(GuiEventType.SHOW_INVENTORY, p->playGuiSound(p,SHOW_INVENTORY));
//        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, p->playGuiSound(p,SHOW_UNIT_INFO_PANEL));
//        GuiEventManager.bind(GuiEventType.GAME_PAUSED, p->playGuiSound(p,GAME_PAUSED));
//        GuiEventManager.bind(GuiEventType.GAME_RESUMED, p->playGuiSound(p,GAME_RESUMED));
//        GuiEventManager.bind(GuiEventType.SHOW_BLUE_BORDERS, p->playGuiSound(p,SHOW_BLUE_BORDERS));
//        GuiEventManager.bind(GuiEventType.CALL_BLUE_BORDER_ACTION, p->playGuiSound(p,CALL_BLUE_BORDER_ACTION));
//        GuiEventManager.bind(GuiEventType.GRID_OBJ_HOVER_OFF, p->playGuiSound(p,GRID_OBJ_HOVER_OFF));
//        GuiEventManager.bind(GuiEventType.GRID_OBJ_HOVER_ON, p->playGuiSound(p,GRID_OBJ_HOVER_ON));
    }

    public static void playCustomEventSound(SOUND_EVENT e) {
        String sound = getCustomEventSound(e);
        if (sound != null)
            DC_SoundMaster.play(sound);
    }

    public static String getCustomEventSound(SOUND_EVENT e) {
        switch (e) {
            case RADIAL_CLOSED:
                return STD_SOUNDS.ACTION_CANCELLED.getPath();
        }
        return null;
    }

    private void playGuiSound(EventCallbackParam p, GuiEventType event) {
        String sound = getGraphicEventSound(p.get(), event);
        if (sound != null)
            DC_SoundMaster.play(sound);
    }

    public String getGraphicEventSound(Object o, GuiEventType e) {
        switch (e) {
            case CREATE_RADIAL_MENU:
                return STD_SOUNDS.DIS__OPEN_MENU.getPath();
            case UNIT_MOVED:
//        return DC_SoundMaster.playMoveSound();
            case MUSIC_PAUSE:
                return STD_SOUNDS.SLING.getPath();
            case GAME_FINISHED:
            case SHOW_INVENTORY:
                return STD_SOUNDS.OPEN.getPath();
            case SHOW_UNIT_INFO_PANEL:
                return STD_SOUNDS.DIS__BOOK_OPEN.getPath();
            case GAME_PAUSED:
                return STD_SOUNDS.CLOCK.getPath();
            case GAME_RESUMED:
                return STD_SOUNDS.DONE2.getPath();
            case SHOW_BLUE_BORDERS:
                return STD_SOUNDS.CLICK_ACTIVATE.getPath();
            case CALL_BLUE_BORDER_ACTION:
                return STD_SOUNDS.CLICK_TARGET_SELECTED.getPath();
            case GRID_OBJ_HOVER_OFF:
                return STD_SOUNDS.CLICK_TARGET_SELECTED.getPath();
            case GRID_OBJ_HOVER_ON:
                return STD_SOUNDS.HERO.getPath();
        }
        return null;

    }

    public String getEventSound(Event e) {
        if (!(e.getType() instanceof STANDARD_EVENT_TYPE))
            return null;
        STANDARD_EVENT_TYPE type = (STANDARD_EVENT_TYPE) e.getType();
        switch (type) {
            case ROUND_ENDS:
                return STD_SOUNDS.DEATH.getPath();
//            case UNIT_HAS_FALLEN_UNCONSCIOUS:
//                if (e.getRef().getSourceObj().isMine())
//                    return STD_SOUNDS.FAIL.getPath();

        }
        return null;
    }

    public enum SOUND_EVENT {
        RADIAL_CLOSED,
        CREATE_RADIAL_MENU,
        UNIT_VIEW_HOVER_ON,;
    }
}
