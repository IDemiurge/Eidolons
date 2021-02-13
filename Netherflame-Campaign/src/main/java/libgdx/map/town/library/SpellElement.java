package libgdx.map.town.library;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.Spell;
import libgdx.anims.AnimData;
import libgdx.anims.AnimData.ANIM_VALUES;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.construct.AnimConstructor;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.panels.headquarters.HqSlotActor;
import libgdx.particles.spell.SpellVfx;
import libgdx.particles.spell.SpellVfxPool;

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

    ANIM_PART vfxType= VisualEnums.ANIM_PART.CAST;
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
        AnimData data = AnimConstructor.getStandardData(model, VisualEnums.ANIM_PART.CAST, 1);
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
