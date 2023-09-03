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
        style,
        input,
        content,
        pic_type,
        author,
        generic,
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
