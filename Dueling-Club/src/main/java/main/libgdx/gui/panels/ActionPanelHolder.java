package main.libgdx.gui.panels;

import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.DC_UnitAction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/5/2017.
 */
public class ActionPanelHolder {
    DC_HeroObj hero;

    ActionPanel itemPanel;
    ActionPanel centerPanel;
    ActionPanel spellPanel;
    private int columns=6;

    public void refresh(){
        itemPanel.init(hero.getQuickItems());
        Collection<DC_UnitAction> actives = getFilteredActions();
        centerPanel.init(actives); //TODO filter actions!!!
        spellPanel.init(hero.getSpells());

    }

    private Collection<DC_UnitAction> getFilteredActions() {
         List<DC_UnitAction> list = new LinkedList<>();
         hero.getActives().forEach(a->{
             list.add((DC_UnitAction) a);
        });
        return list;
    }

    public void init(){
    itemPanel= new ActionPanel<DC_QuickItemObj>(hero,
     action->{
         DC_QuickItemObj item= (DC_QuickItemObj) action.get();
         item.invokeClicked();
    }, columns);

    centerPanel= new ActionPanel<DC_UnitAction>(hero,
     action->{
         DC_UnitAction active = (DC_UnitAction) action.get();
         active.invokeClicked();
     }, columns);

    spellPanel= new ActionPanel<DC_SpellObj>(hero,
     action->{
         DC_SpellObj spell = (DC_SpellObj) action.get();
         spell.invokeClicked();
     }, columns);
    refresh();
}
}
