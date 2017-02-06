package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.content.CONTENT_CONSTS.STD_BUFF_NAMES;
import main.data.filesys.PathFinder;
import main.entity.obj.BuffObj;
import main.libgdx.anims.ANIM_MODS.ANIM_MOD;
import main.libgdx.anims.ANIM_MODS.CONTINUOUS_ANIM_MODS;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.AnimData.ANIM_VALUES;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.FileManager;

import java.util.List;

/**
 * Created by JustMe on 1/22/2017.
 */
public class BuffAnim extends ActionAnim {

    BuffObj buff;
    boolean playContinuous;
    boolean playOnHover;
    boolean playOnNewRound;

    public BuffAnim(BuffObj buff) {
        super(buff.getActive(), getBuffAnimData(buff));
        this.buff = buff;
        mods = new ANIM_MOD[]{
                CONTINUOUS_ANIM_MODS.PENDULUM_ALPHA
        };
        part = ANIM_PART.AFTEREFFECT;
        textureSupplier = () -> TextureManager.getOrCreate(buff.getImagePath());

//setPlayContinuous(buff.checkBool());
        setPlayOnHover(true);
        initDuration();

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
        if (name == null) {
            return data;
        }
        String sfx = PathFinder.getSfxPath() + getStdSfx(name);
        String sprites = PathFinder.getSpritesPath() + "buffs\\razorsharp 20 1.png";
//        String std = PathFinder.getSpritesPath() + getStdSprites(name);
//        if (FileManager.isFile(std))
//        sprites=std+";";
//        else
//        sprites  =   buff.getImagePath()+TextureManager.SINGLE_SPRITE+";";;

        if (sfx.split(";").length > 1 || FileManager.isFile(sfx)) {
            data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, sfx);
        }
        data.setValue(ANIM_VALUES.SPRITES, sprites);
        return data;
    }

    private static String getStdSprites(STD_BUFF_NAMES name) {
        return "buffs\\" + name.getName() + ".png";
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

    public boolean isPlayContinuous() {
        return playContinuous;
    }

    public void setPlayContinuous(boolean playContinuous) {
        this.playContinuous = playContinuous;
    }

    public boolean isPlayOnHover() {
        return playOnHover;
    }

    public void setPlayOnHover(boolean playOnHover) {
        this.playOnHover = playOnHover;
    }

    public boolean isPlayOnNewRound() {
        return playOnNewRound;
    }

    public void setPlayOnNewRound(boolean playOnNewRound) {
        this.playOnNewRound = playOnNewRound;
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
            if (s.getLifecycleDuration() > lifecycleDuration) {
                lifecycleDuration = s.getLifecycleDuration();
            }
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
        if (isPlayOnNewRound()) {
            duration = 2;
        }
        if (isPlayOnHover()) {
            duration = 3;
        }
        if (isPlayContinuous()) {
            duration = -1;
        }
    }

    @Override
    public Actor getActor() {
        return super.getActor();
    }

    public enum BUFF_ANIM_PLAY_MODE {
        CONTINUOUS,
        ON_HOVER,
        ON_NEW_ROUND,
    }
}
