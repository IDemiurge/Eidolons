package eidolons.libgdx.anims.std;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import main.entity.Ref.KEYS;

/**
 * Created by JustMe on 2/5/2017.
 */
public class CaseAnim extends ActionAnim {

    public CaseAnim(DC_ActiveObj active,
                    ANIM_CASES CASE) {
        super(active, getAnimData(active, CASE));
    }

    private static AnimData getAnimData(DC_ActiveObj active, ANIM_CASES aCase) {
        AnimData data = new AnimData();
        String sprite = getSprite(active, aCase);
        data.setValue(ANIM_VALUES.SPRITES, sprite);
        return data;
    }

    private static String getSprite(DC_ActiveObj active, ANIM_CASES aCase) {
        switch (aCase) {
            case BLOCK:
                active.getRef().getTargetObj().getRef().getObj(KEYS.OFFHAND).getImagePath();
                break;
            case DODGE:
            case PARRY:
                break;
        }
        return null;
    }

    public enum ANIM_CASES {
        BLOCK,
        DODGE,
        PARRY,
    }
}
