package eidolons.game.battlecraft.rules.combat.attack;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.assets.AnimMaster3d;
import eidolons.libgdx.assets.Atlases;
import main.system.auxiliary.RandomWizard;

public class ShieldMaster {
    public static SpriteAnimation getSprite(DC_WeaponObj shield, DC_ActiveObj atk, Integer blockValue) {
        Array<TextureAtlas.AtlasRegion> regions = null;
        try {
            regions = getShieldRegions(shield, atk, blockValue);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return null ;
        }
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(regions, AnimMaster3d.getFps(), 0);
        sprite.setPlayMode(Animation.PlayMode.REVERSED);
        return sprite;
    }


    public static Array<TextureAtlas.AtlasRegion> getShieldRegions(DC_WeaponObj shield,
                                                                   DC_ActiveObj atk,
                                                                   Integer blockValue) {

//        String path =
        TextureAtlas atlas = Atlases.getOrCreateAtlas(shield);

//        shield.getAttackActions()
        //bash
        Boolean proj = AnimMaster3d.getProjection(shield.getOwnerObj().getRef(), atk).bool;
        int i = RandomWizard.getRandomIndex(shield.getAttackActions());
        String name = Atlases.getAtlasFileKeyForAction(proj,
                shield.getAttackActions().get(i),
                AnimMaster3d.WEAPON_ANIM_CASE.NORMAL);

        return atlas.findRegions(name);
    }
}
