package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.active.Spell;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.Tooltip;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JustMe on 4/17/2018.
 */
public abstract class HqSpellContainer extends ValueTable<Spell, SpellActor>
implements HqActor {

    public HqSpellContainer(int wrap, int size) {
        super(wrap, size);
        setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanelFilled()));
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(64, 64);

    }

    @Override
    public void init() {
        if (isLabelBefore()){
            add(new LabelX(getLabelText(), 16)).center().colspan(wrap);
            row();
        }
        super.init();
        if (!isLabelBefore()){
            row();
            add(new LabelX(getLabelText(), 16)).center().colspan(wrap);
        }
    }

    protected abstract CharSequence getLabelText();

    private boolean isLabelBefore() {
        return false;
    }

    @Override
    protected SpellActor createElement(Spell datum) {
        SpellActor actor = new SpellActor(datum){
            @Override
            public boolean isOverlayOn() {
                return HqSpellContainer.this.isOverlayOn();
            }
        };
        if (datum != null) {
            actor.addListener(createSpellListener(datum, actor));
            actor.addListener(createTooltip(datum).getController());
        actor.setValid(checkValid(datum));
        actor.setAvailable(checkAvailable(datum));
        }
        return actor;
    }

    private Tooltip<Actor> createTooltip(Spell datum) {
        ActionCostTooltip tooltip = new ActionCostTooltip(datum);
        return tooltip;
    }

    protected boolean checkValid(Spell datum) {
        return true;
    }
    protected boolean checkAvailable(Spell datum) {
        return true;
    }

    protected abstract boolean isOverlayOn();

    private EventListener createSpellListener(Spell spell, SpellActor  actor) {
       return  new SmartClickListener(actor){
           @Override
           protected void onTouchDown(InputEvent event, float x, float y) {
               click(event.getButton(), spell);
           }

           @Override
           protected void onDoubleClick(InputEvent event, float x, float y) {
              doubleClick(event.getButton(), spell);
           }

           @Override
           protected void entered() {
               HqSpellContainer.this.enter (spell,(SpellActor) actor);
           }

           @Override
           protected void exited() {
               super.exited();
           }
       };
    }

    protected abstract void click(int button, Spell spell);

    protected abstract void doubleClick(int button, Spell spell);

    protected void enter(Spell spell, SpellActor actor) {
    }

    @Override
    protected SpellActor[] initActorArray() {
        return new SpellActor[size];
    }

    @Override
    protected Spell[] initDataArray() {
        List<Spell> list =    new ArrayList<>( getSpells()) ;
        Collections.sort(list, getSorter());
        ListMaster.fillWithNullElements(list, size);
        return list.toArray(new Spell[list.size()]);
    }

    protected Comparator<Spell> getSorter() {
        return new Comparator<Spell>() {
            @Override
            public int compare(Spell o1, Spell o2) {
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

    protected abstract List<Spell> getSpells();


    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
