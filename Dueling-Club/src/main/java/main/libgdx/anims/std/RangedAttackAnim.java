package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.texture.TextureManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/19/2017.
 */
public class RangedAttackAnim extends AttackAnim {

    Texture rangedWeaponImage;
    private Obj ammo;

    public RangedAttackAnim(Entity active) {
        super(active);
        this.anims = GET();
        //draw bow on the source, ammo as missile

        //another actor? separate sprite?
        rangedWeaponImage = TextureManager.getOrCreate(getRangedWeaponImage(getActive()));
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
    public String getWeaponSpritePath() {
        return findWeaponSprite((DC_WeaponObj) ammo);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
        Texture texture = rangedWeaponImage;
        batch.draw(texture, origin.x, origin.y, this.getOriginX(), this.getOriginY(), this.getWidth(),
                this.getHeight(), this.getScaleX(), this.getScaleY(), initialAngle + this.getRotation(), 0, 0,
                texture.getWidth(), texture.getHeight(), flipX, flipY);

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
