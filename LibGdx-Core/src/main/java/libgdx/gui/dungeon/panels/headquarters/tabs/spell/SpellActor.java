package libgdx.gui.dungeon.panels.headquarters.tabs.spell;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.entity.active.Spell;
import libgdx.gui.dungeon.panels.headquarters.HqSlotActor;
import libgdx.shaders.DarkGrayscaleShader;
import libgdx.shaders.DarkShader;
import libgdx.shaders.ShaderDrawer;
import eidolons.content.consts.Images;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 4/17/2018.
 */
public class SpellActor extends HqSlotActor<Spell> {

    private boolean valid;
    private boolean available;

    public SpellActor(Spell spellObj) {
        super(spellObj);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (parentAlpha == ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
        }
        else
        {
            if (isValid()){
            ShaderDrawer.drawWithCustomShader(this, batch, null);
            } else
            {
                if (!isAvailable()) {
                    ShaderDrawer.drawWithCustomShader(this, batch, DarkGrayscaleShader.getShader_());
                }
                else
                ShaderDrawer.drawWithCustomShader(this, batch, DarkShader.getDarkShader());
            }
        }
    }

    @Override
    protected String getOverlay(Spell model) {
        return HqSpellMaster.getOverlay(model);
    }

    @Override
    protected String getEmptyImage() {
        return Images.EMPTY_SPELL;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public enum SPELL_OVERLAY {
        VERBATIM,
        MEMORIZED,
        AVAILABLE,
        UNAVAILABLE,
        KNOWN,
        CANNOT_PAY, DIVINED;

        public String imagePath;

        SPELL_OVERLAY() {
            imagePath = StrPathBuilder.build(PathFinder.getComponentsPath(),
             "hq", "spell", "overlay", name() + ".png");
        }

    }
}
