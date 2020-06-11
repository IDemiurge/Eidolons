package eidolons.game.netherflame.boss.demo.knight;

import eidolons.game.netherflame.boss.anims.BossAnims;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.BlendImageContainer;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.texture.Images;
import main.content.enums.GenericEnums;

public class AriusVessel extends BossVisual {

    BlendImageContainer runes;
    BlendImageContainer glaive;
    FadeImageContainer orb;
    private final float speed=1f;

    public AriusVessel(BossUnit unit) {
        super(unit);
        setSize(400,400);
        addActor(runes = new BlendImageContainer(Images.RUNE_CIRCLE, GenericEnums.BLENDING.SCREEN));
        addActor(glaive = new BlendImageContainer(Images.GLAIVE,GenericEnums.BLENDING.INVERT_SCREEN));
        addActor(orb = new FadeImageContainer(Images.ARIUS_ORB));
        GdxMaster.center(runes);
        GdxMaster.center(glaive);
        GdxMaster.center(orb); //use a planet from macro? :)
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //up and down?
        // speed = speedAction.getValue();
        runes.setRotation(runes.getRotation()+delta*speed);
        glaive.setRotation(runes.getRotation()-delta*speed);
        //TODO manual!
    }

    @Override
    public void animate(BossAnims.BOSS_ANIM_COMMON anim) {

    }
}
