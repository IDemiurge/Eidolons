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

    //VIEW
    INPUT_MOVE, UNIT_MOVE, POS_UPDATE,
    //DTO
    DTO_FrontField,
    DTO_HeroZone,
    DTO_LaneField,
    //CAM
    CAMERA_SHAKE, CAMERA_OFFSET, CAMERA_SET_TO, CAMERA_ZOOM,
    RESET_CAMERA,    RESET_ZOOM,
    ///////////ATB
    NEW_ATB_TIME, TIME_PASSED, ATB_ACTIVE,

    ///////////ANIM
    DUMMY_ANIM_ATK, DUMMY_ANIM_DEATH, DUMMY_ANIM_HIT,
    DUMMY_ANIM_EXPLODE, DUMMY_ANIM_HIT_HERO, DUMMY_ANIM_ATK_HERO, DUMMY_ANIM_DEATH_HERO, CORE_HP, DUMMY_ANIM_ATK_CORE;

    @Override
    public String toString() {
        return super.toString();
    }
}
