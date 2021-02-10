package eidolons.libgdx.screens.map.town.library;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.active.Spell;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.libgdx.anims.AnimEnums;
import eidolons.libgdx.anims.AnimEnums.ANIM_PART;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.headquarters.HqSlotActor;
import eidolons.libgdx.particles.spell.SpellVfx;
import eidolons.libgdx.particles.spell.SpellVfxPool;

import java.util.Map;

/**
 * Created by JustMe on 11/21/2018.
 * <p>
 * emitters attached
 * <p>
 * no scale on hover, but..
 * <p>
 * more than one emitter-state case
 * > explode on add/remove
 * <p>
 * > missile trail into book on learn
 */
public class SpellElement extends HqSlotActor<Spell> {

    ANIM_PART vfxType= AnimEnums.ANIM_PART.CAST;
    SpellVfx activeVfx;
    Map<ANIM_PART, SpellVfx> vfxMap;
    //some are kind of one-shot, right? 

    public SpellElement(Spell model) {
        super(model);
        initVfx();
//        initBorder();
//blending?
    }

    public Map<ANIM_PART, SpellVfx> getVfxMap() {
        return vfxMap;
    }
public void setVfxPart(ANIM_PART part){
    activeVfx.hide();

        activeVfx= getVfx(part);
        activeVfx.start();

}
    public SpellVfx getVfx(ANIM_PART part) {
        return getVfxMap().get(part);
    }
    public SpellVfx getActiveVfx() {
        return activeVfx;
    }
    @Override
    protected FadeImageContainer createBackground() {
        return super.createBackground();
    }

    @Override
    protected FadeImageContainer createBackgroundOverlay(Spell model) {
        return super.createBackgroundOverlay(model);
    }

    @Override
    protected String getImagePath(Spell model) {
        return super.getImagePath(model);
    }
    @Override
    protected String getOverlay(Spell model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return null;
    }
    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void initVfx() {
        AnimData data = AnimConstructor.getStandardData(model, AnimEnums.ANIM_PART.CAST, 1);
        String path = data.getValue(ANIM_VALUES.PARTICLE_EFFECTS);
        SpellVfxPool.getEmitterActor(path);
    }


    @Override
    protected ClickListener createListener() {
        return super.createListener();
    }


    public enum SPELL_STATUS {
        CAN_LEARN,
        UNLOCKED,
        BLOCKED,
        UNFAMILIAR,
        REVIEW,

    }
        public enum SPELL_CONTAINER {
        LIBRARY,
        KNOWN,
        ACTIVE
    }
}
