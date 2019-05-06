package eidolons.game.battlecraft.rules.combat.attack;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import main.system.auxiliary.RandomWizard;

public class ShieldMaster {
    public static SpriteAnimation getSprite(DC_WeaponObj shield, DC_ActiveObj atk, Integer blockValue) {
        Array<TextureAtlas.AtlasRegion> regions = getShieldRegions(shield, atk, blockValue);
        SpriteAnimation sprite = SpriteAnimationFactory.getSpriteAnimation(regions, AnimMaster3d.getFps(), 0);
        sprite.setPlayMode(Animation.PlayMode.REVERSED);
        return sprite;
    }


    public static Array<TextureAtlas.AtlasRegion> getShieldRegions(DC_WeaponObj shield,
                                                                   DC_ActiveObj atk,
                                                                   Integer blockValue) {

//        String path =
        TextureAtlas atlas = AnimMaster3d.getOrCreateAtlas(shield);

        Boolean proj = AnimMaster3d.getProjection(shield.getOwnerObj().getRef(), atk).bool;
        int i = RandomWizard.getRandomIndex(shield.getAttackActions());
        String name = AnimMaster3d.getAtlasFileKeyForAction(proj,
                shield.getAttackActions().get(i),
                AnimMaster3d.WEAPON_ANIM_CASE.NORMAL);

        return atlas.findRegions(name);
    }
}
