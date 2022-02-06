package libgdx.anims.std;

import eidolons.entity.feat.active.ActiveObj;
import libgdx.anims.AnimData;
import libgdx.anims.AnimData.ANIM_VALUES;
import main.entity.Ref.KEYS;

/**
 * Created by JustMe on 2/5/2017.
 */
public class CaseAnim extends ActionAnim {

    public CaseAnim(ActiveObj active,
                    ANIM_CASES CASE) {
        super(active, getAnimData(active, CASE));
    }

    private static AnimData getAnimData(ActiveObj active, ANIM_CASES aCase) {
        AnimData data = new AnimData();
        String sprite = getSprite(active, aCase);
        data.setValue(ANIM_VALUES.SPRITES, sprite);
        return data;
    }

    private static String getSprite(ActiveObj active, ANIM_CASES aCase) {
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
