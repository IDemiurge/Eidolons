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

    ADD_OR_UPDATE_INITIATIVE(),
    REMOVE_FROM_INITIATIVE_PANEL(),

    INITIATIVE_CHANGED(),

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
    DESTROY_UNIT_MODEL(),
    ABILITY_RESOLVES(),
    EFFECT_APPLIED(),
    ACTION_RESOLVES(),
    ACTION_INTERRUPTED(),
    UNIT_MOVED(),
    ACTION_BEING_RESOLVED(),
    UPDATE_BUFFS(),
    LOG_ENTRY_ADDED(),
    UPDATE_AMBIENCE(),

    CREATE_EMITTER,


    SFX_PLAY_LAST();

}
