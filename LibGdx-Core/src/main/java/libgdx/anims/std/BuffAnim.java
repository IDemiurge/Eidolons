package libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.anims.ANIM_MODS.ANIM_MOD;
import libgdx.anims.ANIM_MODS.CONTINUOUS_ANIM_MODS;
import libgdx.anims.AnimData;
import libgdx.anims.AnimData.ANIM_VALUES;
import libgdx.anims.AnimEnums;
import libgdx.anims.sprite.SpriteAnimation;
import main.content.enums.system.MetaEnums.STD_BUFF_NAME;
import main.data.filesys.PathFinder;
import main.entity.obj.BuffObj;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;

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
        part = AnimEnums.ANIM_PART.AFTEREFFECT;
        // textureSupplier = () -> TextureCache.getOrCreate(buff.getImagePath());

        //setPlayContinuous(buff.checkBool());
        setPlayOnHover(true);
        initDuration();

    }

    private static AnimData getBuffAnimData(BuffObj buff) {
        AnimData data = new AnimData();
        //    buff.getVar
        if (buff.getName().equals("Channeling")) {
            //rotating!
        }
        /*
        ablaze, frozen, bleeding, wounded, charmed,
         */
        STD_BUFF_NAME name =
                new EnumMaster<STD_BUFF_NAME>().retrieveEnumConst(STD_BUFF_NAME.class, buff.getName());
        if (name == null) {
            return data;
        }
        String sfx = PathFinder.getVfxPath() + getStdSfx(name);
        String sprites = PathFinder.getSpritesPathFull() + "buffs/razorsharp 20 1.png";
        //        String std = PathFinder.getSpritesPathFull() + getStdSprites(name);
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

    private static String getStdSprites(STD_BUFF_NAME name) {
        return "buffs/" + name.getName() + ".png";
    }

    private static String getStdSfx(STD_BUFF_NAME name) {
        switch (name) {

            case Channeling:
            case Entangled:
            case Paralyzing_Poison:
            case Weakening_Poison:
            case Hallucinogetic_Poison:
            case Wounded:
            case Asleep:
            case Contaminated:
            case Frost:
            case Bleeding:
            case Poison:
            case Ablaze:
                break;
        }
        return "buffs/" + name.getName();
    }

    private boolean isPlayContinuous() {
        return playContinuous;
    }

    private boolean isPlayOnHover() {
        return playOnHover;
    }

    private void setPlayOnHover(boolean playOnHover) {
        this.playOnHover = playOnHover;
    }

    private boolean isPlayOnNewRound() {
        return playOnNewRound;
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
            setDuration(2);
        }
        if (isPlayOnHover()) {
            setDuration(3);
        }
        if (isPlayContinuous()) {
            duration = -1;
        }
    }

    @Override
    public Coordinates getDestinationCoordinates() {
        return buff.getBasis().getCoordinates();
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
