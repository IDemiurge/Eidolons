package main.system;

import main.system.launch.CoreEngine;

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

    SCREEN_LOADED(false, false),
    SWITCH_SCREEN(false, false), //pass ScreenData as param

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
    UNIT_CREATED(false, true),
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

    EFFECT_APPLIED,
    ACTION_RESOLVES,
    ACTION_INTERRUPTED,
    ACTION_BEING_RESOLVED,

    UPDATE_BUFFS,
    UPDATE_GUI,
    UPDATE_SHADOW_MAP,
    UPDATE_EMITTERS,
    UPDATE_GRAVEYARD,
    CREATE_EMITTER,
    LOG_ENTRY_ADDED,
    FULL_LOG_ENTRY_ADDED,

    ANIMATION_STARTED,
    COMPOSITE_ANIMATION_STARTED,
    COMPOSITE_ANIMATION_DONE,
    ANIMATION_DONE,
    GRID_OBJ_HOVER_ON,
    GRID_OBJ_HOVER_OFF,
    SHOW_MODE_ICON,
    GAME_STARTED, GAME_FINISHED, ADD_LIGHT, GAME_PAUSED, GAME_RESUMED,
    UPDATE_DOOR_MAP, UPDATE_DIAGONAL_WALL_MAP, UPDATE_WALL_MAP,
    CELLS_MASS_SET_VOID, CELLS_MASS_RESET_VOID,
    ITEM_TAKEN,
    UNIT_VIEW_MOVED(false, true),
    UNIT_GREYED_OUT_ON, UNIT_GREYED_OUT_OFF,
    UNIT_VISIBLE_ON, UNIT_VISIBLE_OFF,
    ANIMATION_QUEUE_FINISHED,
    SHOW_TEAM_COLOR_BORDER,
    HP_BAR_UPDATE, HP_BAR_UPDATE_MANY,
    SHOW_SELECTION_PANEL, SHOW_WEAVE, SHOW_LOAD_PANEL,
    SHOW_MANUAL_PANEL, SHOW_DIFFICULTY_SELECTION_PANEL,
    BLACKOUT_IN, BLACKOUT_OUT, BLACKOUT_AND_BACK,

    UPDATE_MAIN_HERO,
    ATB_POS_PREVIEW, ACTION_HOVERED_OFF, ACTION_HOVERED, VALUE_MOD, PREVIEW_ATB_READINESS,
    TIME_PASSED, NEW_ATB_TIME, UPDATE_LAST_SEEN_VIEWS, OPEN_OPTIONS,
    SHOW_HQ_SCREEN, SHOW_MASTERY_LEARN, UPDATE_INVENTORY_PANEL, INIT_AMBIENCE,
    SHOW_SKILL_CHOICE, SHOW_CLASS_CHOICE, SHOW_PERK_CHOICE, SHOW_INFO_TEXT,
    ACTION_BEING_ACTIVATED, CONFIRM, SHOW_VFX, SHOW_CUSTOM_VFX, PARRY,
    HC_PORTRAIT_CHOSEN, HC_GENDER_CHOSEN, HC_RACE_CHOSEN, HC_SUBRACE_CHOSEN, HC_SHOW,
    HC_DEITY_ASPECT_CHOSEN, HC_BACKGROUND_CHOSEN,

    SHOW_QUEST_SELECTION, QUEST_UPDATE, QUEST_ENDED,
    QUEST_STARTED, QUESTS_UPDATE_REQUIRED, SHOW_QUESTS_INFO,
    QUEST_TAKEN, QUEST_CANCELLED, QUEST_COMPLETED,
    INTERACTIVE_OBJ_RESET,
    DISPOSE_TEXTURES, SHOW_FULLSCREEN_ANIM, SHOW_TOWN_PANEL,
    UPDATE_SHOP, SHOW_ACHIEVEMENTS,
    HIDE_ALL_TEXT, HIDE_ACTION_INFO_TEXT, HIDE_INFO_TEXT,
    SHOW_NAVIGATION_PANEL,
    BRIEFING_NEXT, BRIEFING_FINISHED, TIP_MESSAGE, BRIEFING_START, SHOW_SPRITE,
    POST_PROCESSING, POST_PROCESS_FX_ANIM,
    POST_PROCESSING_RESET, QUICK_RADIAL, SHOW_SPRITE_SUPPLIER, INIT_DIALOG,
    BOSS_ACTION, BOSS_CREATED, BOSS_VIEW_CREATED,
    CUSTOM_ANIMATION,
//UI
    SHOW_LORD_PANEL, TOGGLE_LORD_PANEL, UPDATE_LORD_PANEL, SOULFORCE_LOST,
    SOULS_CONSUMED, SOULS_CLAIMED,
    LOG_ROLLED_OUT, LOG_ROLLED_IN,
    //GRID
    REMOVE_UNIT_VIEW(false, true), UNIT_VIEW_CREATED(false, true),
    CELL_RESET(true, true), INIT_CELL_OVERLAY,
    RESET_LIGHT_EMITTER, LIGHT_EMITTER_MOVED, MOVE_OVERLAYING,
    INIT_MANIPULATOR, SHOW_MAZE, HIDE_MAZE, INIT_CELL_DECOR,
    ADD_GRID_OBJ, CAMERA_SHAKE, UPDATE_SOULS_PANEL, PUZZLE_STARTED, PUZZLE_FINISHED, PUZZLE_COMPLETED,

    ADD_AMBI_VFX,
// SCREEN


    CAMERA_PAN_TO_UNIT, CAMERA_PAN_TO, CAMERA_PAN_TO_COORDINATE, CAMERA_LAPSE_TO, CAMERA_SET_TO,

    UNIT_FADE_OUT_AND_BACK,SHOW_COMMENT_PORTRAIT, CAMERA_ZOOM, REMOVE_GRID_OBJ,
    WHITEOUT_AND_BACK, WHITEOUT_IN, WHITEOUT_OUT,  SET_PARTICLES_ALPHA, CAMERA_OFFSET,
    GRID_DISPLACE, GRID_SET_VIEW, GRID_SCREEN, GRID_COLOR, GRID_OBJ_ANIM, CUSTOM_VIEW_ANIM,
    PORTAL_OPEN, PORTAL_CLOSE, GRID_ATTACHED,

    LOAD_SCOPE, DISPOSE_SCOPE, HIGHLIGHT_ACTION, HIGHLIGHT_ACTION_OFF, SCALE_UP_VIEW,


    SHOW_LAST_COMMENT, SHOW_LAST_TUTORIAL_COMMENT,
    CLEAR_COMMENTS, ACTOR_SPEAKS,

    //GAME
    BATTLE_FINISHED,

    //SYSTEM
    CHOOSE_FILE,
    TOGGLE_LOG_GL_PROFILER,

    PLAY_VIDEO,
    //AUDIO
    SET_SOUNDSCAPE_VOLUME, STOP_LOOPING_TRACK, ADD_LOOPING_TRACK,STOP_LOOPING_TRACK_NOW,
    // LEVEL EDITOR
     LE_FLOOR_CHANGED, LE_FLOOR_LOADED,
    LE_TREE_RESET(false, true), LE_REMAP_MODULES(false, true), LE_CHOOSE_BLOCK(false, true),
    CELL_SET_VOID(false, true), CELL_RESET_VOID(false, true),
    LE_GUI_RESET(false, true), LE_SELECTION_CHANGED(false, true), LE_EDIT(false, true),
    LE_CENTER_ON_SELECT(false, true), LE_ENUM_CHOICE(false, true),
    LE_DISPLAY_MODE_UPDATE(false, true), LE_AI_DATA_UPDATE(false, true),
    LE_CELL_SCRIPTS_LABEL_UPDATE(false, true), LE_CELL_AI_LABEL_UPDATE(false, true),
    LE_PALETTE_SELECTION(false, true), LE_BLOCK_PALETTE_SELECTION(false, true),
    LE_PALETTE_RESELECT(false, true), RESET_VIEW(true, true),
    LE_TREE_SELECT(false, true), LE_TREE_RESELECT(false, true), REMOVE_OVERLAY_VIEW(false, true),
    GRID_RESET(false, true), LE_GUI_TOGGLE(false, true),
    CAMERA_CUSTOM_MOVE, INITIAL_LOAD_DONE, CHOOSE_GRID_ANIM, PLAYER_STATUS_CHANGED, SHOW_LARGE_TEXT,

    LE_FLOORS_TABS(false, true), WAITING_ON, WAITING_OFF, SOULFORCE_GAINED, COMBAT_STARTED, COMBAT_ENDED,
    VISUAL_CHOICE, PLATFORM_CREATE(false, true), INIT_PLATFORMS(false, true), CAMERA_FOLLOW_MAIN, CAMERA_FOLLOW_OFF;

    private boolean screenCheck;
    private boolean multiArgs;

    @Override
    public boolean isMultiArgsInvocationSupported() {
        return multiArgs;
    }

    GuiEventType() {
    }

    GuiEventType(boolean multiArgs) {
        this.multiArgs = multiArgs;
    }

    GuiEventType(boolean multiArgs, boolean screenCheck) {
        this.multiArgs = multiArgs;
        this.screenCheck = screenCheck;
    }

    @Override
    public boolean isScreenCheck() {
        if (!CoreEngine.isLevelEditor()) {
            return false;
        }
        return screenCheck;
    }
}
