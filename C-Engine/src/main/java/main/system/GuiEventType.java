package main.system;

//public enum GUI_EVENT implements GraphicEvent{
//
//}
//public enum ANIMATION_EVENT implements GraphicEvent{
//
//}
//public enum GRID_EVENT implements GraphicEvent{
//
//}
public enum GuiEventType {
ADD_FLOATING_TEXT,
    ADD_CORPSE,
    REMOVE_CORPSE,

    BF_CREATED,
    CREATE_RADIAL_MENU,
    SHOW_INFO_DIALOG, DIALOG_CLOSED,
    SHOW_PHASE_ANIM,
    UPDATE_PHASE_ANIM,
    UPDATE_PHASE_ANIMS,
    UPDATE_GUI,
    UPDATE_LIGHT,
    UPDATE_EMITTERS,
    UPDATE_GRAVEYARD,
    ANIMATION_ADDED, EMITTER_ANIM_CREATED,

    ADD_OR_UPDATE_INITIATIVE,
    REMOVE_FROM_INITIATIVE_PANEL,

    UPDATE_UNIT_VISIBLE,
    UPDATE_UNIT_ACT_STATE,

    INITIATIVE_CHANGED,

    SHOW_UNIT_INFO_PANEL,
    SHOW_INVENTORY,

    UPDATE_QUICK_SLOT_PANEL,

    SHOW_GREEN_BORDER,
    SHOW_RED_BORDER,
    SHOW_BLUE_BORDERS,
    CALL_BLUE_BORDER_ACTION,

    SELECT_MULTI_OBJECTS,
    INGAME_EVENT_TRIGGERED,
    ACTIVE_UNIT_SELECTED,
    SHOW_TOOLTIP,
    CREATE_UNITS_MODEL,
    DESTROY_UNIT_MODEL,
    ABILITY_RESOLVES,
    EFFECT_APPLIED,
    ACTION_RESOLVES,
    ACTION_INTERRUPTED,
    UNIT_MOVED,
    ACTION_BEING_RESOLVED,
    UPDATE_BUFFS,
    LOG_ENTRY_ADDED,
    FULL_LOG_ENTRY_ADDED,
    UPDATE_AMBIENCE,

    CREATE_EMITTER,


    SFX_PLAY_LAST, MOUSE_HOVER, UNIT_CREATED, REFRESH_GRID,

    UPDATE_DUNGEON_BACKGROUND,
    DUNGEON_LOADED, DIALOGUE_UPDATED, DIALOGUE_OPTION_CHOSEN;

}
