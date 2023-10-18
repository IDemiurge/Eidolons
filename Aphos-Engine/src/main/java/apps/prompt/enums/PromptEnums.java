package apps.prompt.enums;

import campaign.data.enums.AssetEnums;

/**
 * Created by Alexander on 9/2/2023
 */
public class PromptEnums {

    public enum PromptStyle {
        //the very FIRST words; with default being none
    }

    //use real game enums?
    public enum TokenType {
        // style,
        input,
        // content,
        // pic_type,
        // author,
        // generic,
        lava_env_noun,
        lava_env_adj,
        lava_char_noun,
        lava_char_adj,
        lava_theme_noun,
        lava_theme_adj,


        content_scene_temple,
        content_character_temple,

        content_character_wicked,
        content_character_bone,
        content_character_ice,
        content_character_anphis,
        content_character_winter_kingdom,
        content_env_wicked,
        content_env_bone,
        content_env_ice,
        content_env_winter_kingdom,
        content_scene_victim,
        content_env_desert,
        content_character_desert,
        content_env_autumn,
        content_character_autumn,
    }

    public enum PromptType {
        Hero_Portrait,
        NPC_Portrait,
        Sprite,

        Feat_Icon,
        Action_Icon,
        Item_Icon,

        Scene_Pic,
        Night_Scene_Pic,

        Event_Pic(AssetEnums.AphosEventType.class), //stuff that happens often - boon, fascination, sacrifice, ...
        //overworld_event

        Secret_Pic,
        Omen_Pic,

        Terrain,
        Ui_Asset,
        Poster,
        ;
        String subtypes;
        Object[] subs;
        Class subClass;

        PromptType() {
        }

        PromptType(Class subClass) {
            this.subClass = subClass;
            subs = subClass.getEnumConstants();
        }
    }
}
