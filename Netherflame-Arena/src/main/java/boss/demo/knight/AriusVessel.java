package boss.demo.knight;

import boss.anims.BossAnims;
import boss.anims.generic.BossVisual;
import boss.logic.entity.BossUnit;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.BlendImageContainer;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.texture.Images;
import main.content.enums.GenericEnums;

public class AriusVessel extends BossVisual {

    BlendImageContainer runes;
    BlendImageContainer glaive;
    FadeImageContainer orb;

    EmitterActor emitterActor;

    public AriusVessel(BossUnit unit) {
        super(unit);
        setSize(400,400);
        addActor(runes = new BlendImageContainer(Images.RUNE_CIRCLE, GenericEnums.BLENDING.SCREEN));
        addActor(glaive = new BlendImageContainer(Images.GLAIVE,GenericEnums.BLENDING.INVERT_SCREEN));
        addActor(orb = new BlendImageContainer(Images.ARIUS_ORB,GenericEnums.BLENDING.NORMAL));
        GdxMaster.center(runes);
        GdxMaster.center(glaive);
        GdxMaster.center(orb); //use a planet from macro? :)

        runes.setOrigin(Align.center);
        glaive.setOrigin(Align.center);
        orb.setOrigin(Align.center);

        // addActor(emitterActor = new EmitterActor(GenericEnums.VFX.invert_bloody_bleed2));
        // emitterActor.start();
        // emitterActor.setX(orb.getX());
        // emitterActor.setY(orb.getY());
        runes.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.SHARD_OVERLAY);
        // orb.setSclaeTemplate(GenericEnums.ALPHA_TEMPLATE.HEART);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //up and down?
        // speed = speedAction.getValue();

        glaive.setX(glaive.getX()+12);
        glaive.setX(glaive.getY()+5);

        float speed = 2f;
        runes.setRotation(runes.getRotation()+delta* speed);
        glaive.setRotation(runes.getRotation()-delta* speed *2);
        orb.setRotation(runes.getRotation()-delta* speed /2);
        //TODO manual!
    }

    @Override
    public void animate(BossAnims.BOSS_ANIM_COMMON anim) {

    }
}
