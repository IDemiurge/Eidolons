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
public enum GuiEventType implements EventType {


    MUSIC_START,
    MUSIC_PAUSE,
    MUSIC_STOP,
    MUSIC_RESUME,

    INGAME_EVENT_TRIGGERED,
    REFRESH_GRID,
    UPDATE_DUNGEON_BACKGROUND,
    DIALOGUE_UPDATED,
    DIALOGUE_OPTION_CHOSEN,
    DIALOG_SHOW,

    SCREEN_LOADED,
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
    SHOW_TARGET_BORDERS,
    TARGET_SELECTION,

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
    SHOW_INVENTORY, TOGGLE_INVENTORY,
    SHOW_LOOT_PANEL,
    SHOW_TOOLTIP,
    ADD_FLOATING_TEXT,
    ACTION_PANEL_UPDATE,

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

    VFX_PLAY_LAST,
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
    GAME_STARTED, GAME_FINISHED, ADD_LIGHT,  GAME_PAUSED, GAME_RESUMED,
    UPDATE_DOOR_MAP, UPDATE_DIAGONAL_WALL_MAP, UPDATE_WALL_MAP,
    SHOW_CLEARSHOT, OPEN_DOOR, ITEM_TAKEN,
    UNIT_GREYED_OUT_ON, UNIT_GREYED_OUT_OFF,
    UNIT_VISIBLE_ON, UNIT_VISIBLE_OFF, ANIMATION_QUEUE_FINISHED,
    SHOW_TEAM_COLOR_BORDER,
    HP_BAR_UPDATE, HP_BAR_UPDATE_MANY,
    SHOW_SELECTION_PANEL,SHOW_WEAVE, SHOW_LOAD_PANEL,
    SHOW_MANUAL_PANEL, SHOW_DIFFICULTY_SELECTION_PANEL, FADE_OUT, FADE_IN, FADE_OUT_AND_BACK,
    BATTLE_FINISHED, ATB_POS_PREVIEW, ACTION_HOVERED_OFF, ACTION_HOVERED, VALUE_MOD, PREVIEW_ATB_READINESS, TIME_PASSED, UPDATE_MAIN_HERO, NEW_ATB_TIME, UPDATE_LAST_SEEN_VIEWS, OPEN_OPTIONS,
    SHOW_HQ_SCREEN, SHOW_MASTERY_LEARN, UPDATE_INVENTORY_PANEL, INIT_AMBIENCE, GAME_RESET, SHOW_SKILL_CHOICE, SHOW_CLASS_CHOICE, SHOW_PERK_CHOICE, SHOW_INFO_TEXT, ACTION_BEING_ACTIVATED, CONFIRM, SHOW_VFX, SHOW_CUSTOM_VFX, PARRY, HC_PORTRAIT_CHOSEN, HC_GENDER_CHOSEN, HC_RACE_CHOSEN, HC_SUBRACE_CHOSEN, HC_SHOW, HC_DEITY_ASPECT_CHOSEN, HC_BACKGROUND_CHOSEN, SHOW_QUEST_SELECTION,
    QUEST_UPDATE,
    QUEST_ENDED,
    QUEST_STARTED,
    QUESTS_UPDATE_REQUIRED, SHOW_QUESTS_INFO, INTERACTIVE_OBJ_RESET,
    DISPOSE_TEXTURES, SHOW_FULLSCREEN_ANIM, SHOW_TOWN_PANEL,
    QUEST_TAKEN, QUEST_CANCELLED, UPDATE_SHOP, SHOW_ACHIEVEMENTS, QUEST_COMPLETED, UNIT_VIEW_MOVED, HIDE_ALL_TEXT, HIDE_ACTION_INFO_TEXT, HIDE_INFO_TEXT, UNIT_VIEW_CREATED, SHOW_NAVIGATION_PANEL,
    BRIEFING_NEXT, BRIEFING_FINISHED, TIP_MESSAGE, BRIEFING_START, SHOW_SPRITE, CAMERA_PAN_TO_UNIT,
    POST_PROCESSING,
    POST_PROCESSING_RESET, QUICK_RADIAL, SHOW_SPRITE_SUPPLIER, POST_PROCESS_FX_ANIM, INIT_DIALOG, BOSS_ACTION, BOSS_CREATED, BOSS_VIEW_CREATED, CUSTOM_ANIMATION, SHOW_LORD_PANEL, TOGGLE_LORD_PANEL, UPDATE_LORD_PANEL, MOVE_OVERLAYING, CELL_RESET, RESET_LIGHT_EMITTER, LIGHT_EMITTER_MOVED, INIT_CELL_OVERLAY, INIT_MANIPULATOR, SHOW_MAZE, HIDE_MAZE, INIT_CELL_DECOR, ADD_GRID_OBJ, CAMERA_SHAKE, UPDATE_SOULS_PANEL, PUZZLE_STARTED, PUZZLE_FINISHED, PUZZLE_COMPLETED, CAMERA_PAN_TO_COORDINATE,



}
