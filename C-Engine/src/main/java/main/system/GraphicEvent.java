package main.system;

import main.game.event.Event.STANDARD_EVENT_TYPE;
//public enum GUI_EVENT implements GraphicEvent{
//
//}
//public enum ANIMATION_EVENT implements GraphicEvent{
//
//}
//public enum GRID_EVENT implements GraphicEvent{
//
//}
public enum GraphicEvent {


    GRID_CREATED,
    CREATE_RADIAL_MENU,
    SHOW_INFO_DIALOG, DIALOG_CLOSED,
    SHOW_PHASE_ANIM,
    UPDATE_PHASE_ANIM,
    UPDATE_PHASE_ANIMS,
UPDATE_GUI,
    UPDATE_LIGHT,
    UPDATE_EMITTERS,
    ANIMATION_ADDED(), EMITTER_ANIM_CREATED(),

    SHOW_GREEN_BORDER,
    SHOW_RED_BORDER,
    SHOW_BLUE_BORDERS,
    SELECT_MULTI_OBJECTS,
    INGAME_EVENT_TRIGGERED,
    ACTIVE_UNIT_SELECTED,
    @Deprecated
    CELL_UPDATE,
    SHOW_TOOLTIP,
    CREATE_UNITS_MODEL,
    DESTROY_UNIT_MODEL(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED ),
    ABILITY_RESOLVES(), EFFECT_APPLIED(), ACTION_RESOLVES(),
    ACTION_INTERRUPTED(), UNIT_MOVED(), ACTION_BEING_RESOLVED();
    public STANDARD_EVENT_TYPE[] boundEvents;

    GraphicEvent(STANDARD_EVENT_TYPE... boundEvents) {
        this.boundEvents = boundEvents;
    }
}
