package eidolons.game.netherflame.boss.anims.old;

import eidolons.libgdx.anims.sprite.SpriteX;
import main.content.enums.GenericEnums;

/*

 */
public class PartAnimSprite extends SpriteX {
    // pool of reusables? there was a trick with their caching I recall

    public PartAnimSprite(String path) {
        super(path);
    }

    public PartAnimSprite(String path, SPRITE_TEMPLATE template, GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        super(path, template, alphaTemplate);
    }
}
