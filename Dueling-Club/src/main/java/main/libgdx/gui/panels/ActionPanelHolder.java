package main.libgdx.gui.panels;

import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_QuickItemObj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.DC_UnitAction;

/**
 * Created by JustMe on 1/5/2017.
 */
public class ActionPanelHolder {
    DC_HeroObj hero;

    ActionPanel itemPanel;
    ActionPanel centerPanel;
    ActionPanel spellPanel;

    public void refresh(){
        itemPanel.init(hero.getQuickItems());
        centerPanel.init(hero.getActives()); //TODO filter actions!!!
        spellPanel.init(hero.getSpells());

    }
        public void init(){
    itemPanel= new ActionPanel<DC_QuickItemObj>(hero,
     action->{
         DC_QuickItemObj item= (DC_QuickItemObj) action.get();
         item.invokeClicked();
    });

    centerPanel= new ActionPanel<DC_UnitAction>(hero,
     action->{
         DC_UnitAction active = (DC_UnitAction) action.get();
         active.invokeClicked();
     });

    spellPanel= new ActionPanel<DC_SpellObj>(hero,
     action->{
         DC_SpellObj spell = (DC_SpellObj) action.get();
         spell.invokeClicked();
     });
    refresh();
}
}
