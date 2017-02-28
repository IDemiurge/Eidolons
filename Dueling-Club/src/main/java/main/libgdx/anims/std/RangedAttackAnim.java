package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.texture.TextureCache;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/19/2017.
 */
public class RangedAttackAnim extends AttackAnim {

    Texture rangedWeaponImage;
    private Obj ammo;
    private SpriteAnimation weaponSprite;

    public RangedAttackAnim(Entity active) {
        super(active);
        this.anims = GET();
        //draw bow on the source, ammo as missile

        //another actor? separate sprite?
        rangedWeaponImage = TextureCache.getOrCreate(getRangedWeaponImage(getActive()));
        ammo = weapon.getRef().getObj(KEYS.AMMO);
        ammo = weapon.getRef().getLastRemovedObj(KEYS.AMMO);
        if (ammo instanceof DC_QuickItemObj) {
            ammo = ((DC_QuickItemObj) ammo).getWrappedWeapon();

        }
    }

    private ATK_ANIMS[] GET() {
        List<ATK_ANIMS> list = new LinkedList<>();
        switch (weapon.getWeaponGroup()) {
            case CROSSBOWS:
            case BOWS:
                list.
                        add(ATK_ANIMS.SHOT);
//            case RIFLES:
//            case PISTOLS:
        }
        return list.toArray(new ATK_ANIMS[list.size()]);


    }

    @Override
    public void start() {
        super.start();
        weaponSprite = new SpriteAnimation(getRangedWeaponImage(getActive()));
        weaponSprite.setAttached(false);
        sprites.add(weaponSprite);
    }

    @Override
    protected int getInitialAngle() {
        return super.getInitialAngle() - 90;
    }

    @Override
    protected void initFlip() {
        super.initFlip();
    }

    @Override
    protected RotateByAction getRotateAction(float angle, float duration) {
        return super.getRotateAction(0, 0);
    }

    private String getRangedWeaponImage(DC_ActiveObj active) {
        return findWeaponSprite(active.getActiveWeapon());

    }

    @Override
    public String getTexturePath() {
        return findWeaponSprite((DC_WeaponObj) ammo);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
//        Texture texture = rangedWeaponImage;
//        batch.draw(texture, origin.x, origin.y, this.getOriginX(), this.getOriginY(), this.getWidth(),
//                this.getHeight(), this.getScaleX(), this.getScaleY(), initialAngle + this.getRotation(), 0, 0,
//                texture.getWidth(), texture.getHeight(), flipX, flipY);

    }


    @Override
    protected void initDuration() {
        super.initDuration();


    }


    @Override
    protected Action getAction() {
        return super.getAction();

    }
}
