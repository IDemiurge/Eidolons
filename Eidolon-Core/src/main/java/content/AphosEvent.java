package content;

import main.system.EventType;

//public enum GUI_EVENT implements GraphicEvent{
//
//}
//public enum ANIMATION_EVENT implements GraphicEvent{
//
//}
//public enum GRID_EVENT implements GraphicEvent{
//
//}
public enum AphosEvent implements EventType {
    DUMMY_ANIM_ATK, DUMMY_ANIM_DEATH, DUMMY_ANIM_HIT,

    INPUT_MOVE, UNIT_MOVE,
    POS_UPDATE,
    RESET_CAMERA,
    RESET_ZOOM,
    DTO_FrontField,
    DTO_HeroZone,
    DTO_LaneField, CAMERA_SHAKE, CAMERA_OFFSET, CAMERA_SET_TO, NEW_ATB_TIME, TIME_PASSED, ATB_ACTIVE, CAMERA_ZOOM
}
