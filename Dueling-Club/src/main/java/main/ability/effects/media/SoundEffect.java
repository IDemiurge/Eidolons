package main.ability.effects.media;

import main.ability.effects.DC_Effect;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.data.ability.OmittedConstructor;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class SoundEffect extends DC_Effect {
    private STD_SOUNDS std_sound;
    private SOUNDSET soundset;
    private SOUNDS sound_type;
    private String key;
    private Boolean custom;
    private Obj obj;

    public SoundEffect(STD_SOUNDS std_sound) {
        this.std_sound = std_sound;
    }

    public SoundEffect(SOUNDSET soundset, SOUNDS sound_type) {
        this.sound_type = sound_type;
        this.soundset = soundset;
    }

    public SoundEffect(String key, SOUNDS sound_type, Boolean custom) {
        this.key = key;
        this.sound_type = sound_type;
        this.custom = custom;
    }

    public SoundEffect(SOUNDS sound_type) {
        this(KEYS.ACTIVE.toString(), sound_type, true);
    }

    @OmittedConstructor
    public SoundEffect(SOUNDS impact, Obj targetObj) {
        this(impact);
        this.obj = targetObj;
    }

    @Override
    public boolean applyThis() {
        if (std_sound != null)
            SoundMaster.playStandardSound(std_sound);
        else if (soundset != null)
            SoundMaster.playEffectSound(sound_type, soundset);
        else {
            if (obj == null)
                obj = ref.getObj(key);
            if (custom)
                SoundMaster.playCustomEffectSound(sound_type, obj);
            else
                SoundMaster.playEffectSound(sound_type, obj);
        }

        return true;
    }

}
