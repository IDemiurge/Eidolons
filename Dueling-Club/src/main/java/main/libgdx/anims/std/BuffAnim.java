package main.libgdx.anims.std;

import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.data.filesys.PathFinder;
import main.entity.obj.specific.BuffObj;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.ANIM_MODS.CONTINUOUS_ANIM_MODS;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.bf.BaseView;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;

import java.util.List;

/**
 * Created by JustMe on 1/22/2017.
 */
public class BuffAnim extends ActionAnim {

    BuffObj buff;

    public BuffAnim(BuffObj buff) {
        super(buff.getActive(), getBuffAnimData(buff));
        this.buff = buff;
        mods = new ANIM_MOD[]{
                CONTINUOUS_ANIM_MODS.PENDULUM_ALPHA
        };
        part = ANIM_PART.AFTEREFFECT;

        textureSupplier = () -> TextureManager.getOrCreate(buff.getImagePath());
//alpha?

    }

    private static AnimData getBuffAnimData(BuffObj buff) {
        AnimData data = new AnimData();
//    buff.get
        if (buff.getName().equals("Channeling")) {
//rotating!
        }
        /*
        ablaze, frozen, bleeding, wounded, charmed,
         */
        STD_BUFF_NAMES name =
                new EnumMaster<STD_BUFF_NAMES>().retrieveEnumConst(STD_BUFF_NAMES.class, buff.getName());
        if (name == null) return data;
        String sfx = PathFinder.getSfxPath() + getStdSfx(name);
        String sprites = PathFinder.getSpritesPath() + getStdSprites(name);
        sprites += ";" + buff.getImagePath();

        if (sfx.split(";").length > 1 || FileManager.isFile(sfx))
            data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, sfx);
        if (sprites.split(";").length > 1 || FileManager.isFile(sprites))
            data.setValue(ANIM_VALUES.SPRITES, sprites);
        return data;
    }

    private static String getStdSprites(STD_BUFF_NAMES name) {
        return "buffs\\" + name.getName();
    }

    private static String getStdSfx(STD_BUFF_NAMES name) {
        switch (name) {

            case Channeling:
                break;
            case Ablaze:
                break;
            case Poison:
                break;
            case Bleeding:
                break;
            case Frost:
                break;
            case Contaminated:
                break;
            case Asleep:
                break;
            case Wounded:
                break;
            case Hallucinogetic_Poison:
                break;
            case Weakening_Poison:
                break;
            case Paralyzing_Poison:
                break;
            case Entangled:
                break;
        }
        return "buffs\\" + name.getName();
    }

    @Override
    public String toString() {
        return buff.getName() + " anim";
    }

    @Override
    public void setSprites(List<SpriteAnimation> sprites) {
        super.setSprites(sprites);
        lifecycleDuration = 0;
        for (SpriteAnimation s : getSprites()) {
            if (s.getLifecycleDuration() > lifecycleDuration)
                lifecycleDuration = s.getLifecycleDuration();
        }
    }

    public BuffObj getBuff() {
        return buff;
    }

    public void setBuff(BuffObj buff) {
        this.buff = buff;
    }

    @Override
    protected void initDuration() {
        duration = -1;
    }

    @Override
    public BaseView getActor() {
        return super.getActor();
    }
}
