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
    MUSIC_START,
    MUSIC_PAUSE,
    MUSIC_STOP,
    MUSIC_RESUME,

    BF_CREATED,
    INGAME_EVENT_TRIGGERED,
    REFRESH_GRID,
    UPDATE_DUNGEON_BACKGROUND,
    DUNGEON_LOADED,
    DIALOGUE_UPDATED,
    DIALOGUE_OPTION_CHOSEN,
    DIALOG_SHOW,

    SCREEN_LOADED,
    LOAD_SCREEN, // pass BFDataCreatedEvent as param
    SWITCH_SCREEN, //pass ScreenData as param

    CREATE_RADIAL_MENU,
    RADIAL_MENU_CLOSE,
    DIALOG_CLOSED,
    ADD_OR_UPDATE_INITIATIVE,
    REMOVE_FROM_INITIATIVE_PANEL,
    UPDATE_UNIT_VISIBLE,
    UPDATE_UNIT_ACT_STATE,


    SHOW_TEXT_CENTERED,
    SHOW_GREEN_BORDER,
    SHOW_RED_BORDER,
    SHOW_BLUE_BORDERS,
    CALL_BLUE_BORDER_ACTION,

    INITIATIVE_CHANGED,
    UNIT_CREATED,
    SELECT_MULTI_OBJECTS,
    ACTIVE_UNIT_SELECTED,
    CREATE_UNITS_MODEL,
    DESTROY_UNIT_MODEL,
    UNIT_STARTS_MOVING,
    UNIT_MOVED,
    ADD_CORPSE,
    REMOVE_CORPSE,

    MOUSE_HOVER,
    SHOW_UNIT_INFO_PANEL,
    SHOW_INVENTORY,
    SHOW_LOOT_PANEL,
    SHOW_TOOLTIP,
    ADD_FLOATING_TEXT,
    UPDATE_QUICK_SLOT_PANEL,

    ABILITY_RESOLVES,
    EFFECT_APPLIED,
    ACTION_RESOLVES,
    ACTION_INTERRUPTED,
    ACTION_BEING_RESOLVED,

    UPDATE_BUFFS,
    UPDATE_GUI,
    UPDATE_LIGHT,
    UPDATE_EMITTERS,
    UPDATE_GRAVEYARD,
    UPDATE_AMBIENCE,
    CREATE_EMITTER,
    LOG_ENTRY_ADDED,
    FULL_LOG_ENTRY_ADDED,

    SFX_PLAY_LAST,
    SHOW_PHASE_ANIM,
    UPDATE_PHASE_ANIM,
    UPDATE_PHASE_ANIMS,

    ANIMATION_STARTED,
    COMPOSITE_ANIMATION_STARTED,
    COMPOSITE_ANIMATION_DONE,
    ANIMATION_DONE,
    GRID_OBJ_HOVER_ON,
    GRID_OBJ_HOVER_OFF,
    SHOW_MODE_ICON,
    GAME_FINISHED, ADD_LIGHT, GAME_PAUSED, GAME_RESUMED,
    UPDATE_DOOR_MAP,UPDATE_DIAGONAL_WALL_MAP, UPDATE_WALL_MAP,
    SHOW_CLEARSHOT, OPEN_DOOR, ITEM_TAKEN,
    UNIT_GREYED_OUT_ON, UNIT_GREYED_OUT_OFF,
    UNIT_VISIBLE_ON,UNIT_VISIBLE_OFF, ANIMATION_QUEUE_FINISHED,
    SHOW_TEAM_COLOR_BORDER,
    HP_BAR_UPDATE,    HP_BAR_UPDATE_MANY, SHOW_SELECTION_PANEL,
}
