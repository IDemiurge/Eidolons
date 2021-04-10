package libgdx.gui.controls.radial;

import eidolons.entity.obj.unit.Unit;
import main.entity.Entity;

import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class EntityNode implements RADIAL_ITEM {
    private Entity spell;

    public EntityNode(Entity s) {
        spell = s;
    }

    @Override
    public List<RADIAL_ITEM> getItems(Unit source) {
        return null;
    }

    @Override
    public Object getContents() {
        return spell;
    }

    @Override
    public String getTexturePath() {
        return ((Entity) getContents()).getImagePath();
    }
}
