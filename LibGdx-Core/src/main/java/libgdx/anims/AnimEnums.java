package libgdx.anims;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.content.VALUE;

public class AnimEnums {
    public static final VALUE[] anim_vals = {
            PROPS.ANIM_SPRITE_PRECAST,
            PROPS.ANIM_SPRITE_CAST,
            PROPS.ANIM_SPRITE_RESOLVE,
            PROPS.ANIM_SPRITE_MAIN,
            PROPS.ANIM_SPRITE_IMPACT,
            PROPS.ANIM_SPRITE_AFTEREFFECT,
            PROPS.ANIM_MISSILE_SPRITE,
            PROPS.ANIM_MODS_SPRITE,

            PROPS.ANIM_MISSILE_VFX,
            PROPS.ANIM_VFX_PRECAST,
            PROPS.ANIM_VFX_CAST,
            PROPS.ANIM_VFX_RESOLVE,
            PROPS.ANIM_VFX_MAIN,
            PROPS.ANIM_VFX_IMPACT,
            PROPS.ANIM_VFX_AFTEREFFECT,
            PROPS.ANIM_MODS_VFX,
            PARAMS.ANIM_SPEED,
            PARAMS.ANIM_FRAME_DURATION,
    };

    public enum ANIM_PART {
        PRECAST(2F), //channeling
        CAST(2.5f),
        RESOLVE(2),
        MISSILE(3) {
            @Override
            public String getPartPath() {
                return
                        "missile";
            }
        }, //flying missile
        IMPACT(1),
        AFTEREFFECT(2.5f);

        public String getPartPath() {
            return super.toString();
        }

        private final float defaultDuration;

        ANIM_PART(float defaultDuration) {
            this.defaultDuration = defaultDuration;
        }

        public float getDefaultDuration() {
            return defaultDuration;
        }
    }
}
