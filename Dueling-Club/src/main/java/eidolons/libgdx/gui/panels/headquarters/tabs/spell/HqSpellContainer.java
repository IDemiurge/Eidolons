package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_SpellObj;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/17/2018.
 */
public abstract class HqSpellContainer extends ValueTable<DC_SpellObj, SpellActor>
implements HqActor {

    public HqSpellContainer(int wrap, int size) {
        super(wrap, size);
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(64, 64);

    }

    @Override
    protected SpellActor createElement(DC_SpellObj datum) {
        return new SpellActor(datum);
    }

    @Override
    protected SpellActor[] initActorArray() {
        return new SpellActor[size];
    }

    @Override
    protected DC_SpellObj[] initDataArray() {
        List<DC_SpellObj> list =    new ArrayList<>( getSpells()) ;
        ListMaster.fillWithNullElements(list, size);
        return list.toArray(new DC_SpellObj[list.size()]);
    }

    protected abstract List<DC_SpellObj> getSpells();


    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
