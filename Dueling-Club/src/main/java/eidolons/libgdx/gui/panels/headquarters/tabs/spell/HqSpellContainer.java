package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.active.DC_SpellObj;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JustMe on 4/17/2018.
 */
public abstract class HqSpellContainer extends ValueTable<DC_SpellObj, SpellActor>
implements HqActor {

    public HqSpellContainer(int wrap, int size) {
        super(wrap, size);
        setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanel()));
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(64, 64);

    }

    @Override
    protected SpellActor createElement(DC_SpellObj datum) {
        SpellActor actor = new SpellActor(datum);
        if (datum != null) {
            actor.addListener(createSpellListener(datum, actor));
            actor.addListener(new ActionCostTooltip(datum).getController());
        }
        return actor;
    }

    private EventListener createSpellListener(DC_SpellObj spell, SpellActor  actor) {
       return  new SmartClickListener(actor){
           @Override
           protected void onTouchDown(InputEvent event, float x, float y) {
               click(event.getButton(), spell);
           }

           @Override
           protected void onDoubleTouchDown(InputEvent event, float x, float y) {
              doubleClick(event.getButton(), spell);
           }

           @Override
           protected void entered() {
               HqSpellContainer.this.enter (spell, actor);
           }

           @Override
           protected void exited() {
               super.exited();
           }
       };
    }

    protected abstract void click(int button, DC_SpellObj spell);

    protected abstract void doubleClick(int button, DC_SpellObj spell);

    protected void enter(DC_SpellObj spell, SpellActor actor) {
    }

    @Override
    protected SpellActor[] initActorArray() {
        return new SpellActor[size];
    }

    @Override
    protected DC_SpellObj[] initDataArray() {
        List<DC_SpellObj> list =    new ArrayList<>( getSpells()) ;
        Collections.sort(list, getSorter());
        ListMaster.fillWithNullElements(list, size);
        return list.toArray(new DC_SpellObj[list.size()]);
    }

    protected Comparator<DC_SpellObj> getSorter() {
        return new Comparator<DC_SpellObj>() {
            @Override
            public int compare(DC_SpellObj o1, DC_SpellObj o2) {
                if (o1==null  && o2==null )
                    return 0;
                if (o1==null)
                    return -1;
                if ( o2==null )
                    return 1;
                if (o1.getId()< o2.getId())
                    return 1;
                return -1;
            }
        };
    }

    protected abstract List<DC_SpellObj> getSpells();


    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
