package eidolons.game.battlecraft.logic.meta.igg.hero;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Game;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.hero.HeroListPanel;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.neo.UnitDescriptionPanel;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.text.DescriptionTooltips;
import eidolons.system.text.HelpMaster;
import main.entity.Entity;
import main.system.auxiliary.secondary.InfoMaster;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Is this the original choice? Will it be diff with death?
 *
 * logic
 *
 * ui
 *
 */

/**
 * tabbed?
 */

public class IggHeroSelectionPanel extends HeroSelectionPanel {
    TabbedPanel tabbedPanel;
    private IggHeroInfoPanel main;
    private UnitDescriptionPanel descr;

    public static final boolean TABBED_MODE= false;

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        if (!TABBED_MODE) {
            return new IggHeroInfoPanel(null );
        }
        tabbedPanel = new TabbedPanel();
        tabbedPanel.addTab(main=new IggHeroInfoPanel(null), "Main");
        tabbedPanel.addTab(descr=new UnitDescriptionPanel(), "Lore");
        return new SelectableItemDisplayer() {
            @Override
            public Actor getActor() {
                return tabbedPanel;
            }

            @Override
            public void setItem(ItemListPanel.SelectableItemData sub) {
                main.setItem(sub);
                descr.setUserObject(sub.getDescription());
            }

            @Override
            public void setDoneDisabled(boolean doneDisabled) {

            }

            @Override
            public void initStartButton(String doneText, Runnable o) {
                main.initStartButton(doneText, o);
            }
        };
    }

    @Override
    protected String getBackgroundSpritePath() {
        return Sprites.BG_DEFAULT;
    }

    @Override
    protected List<ItemListPanel.SelectableItemData> createListData() {
        List<ItemListPanel.SelectableItemData> list = super.createListData();
        if (isShuffleDataList())
            Collections.shuffle(list);
        for (ItemListPanel.SelectableItemData selectableItemData : list) {
            String descr = getHeroDescription(selectableItemData.getEntity());
            selectableItemData.setDescription(descr);
            selectableItemData.setDescription(descr);


        }
        return list;
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroListPanel(){
            @Override
            public boolean isBlocked(SelectableItemData item) {
                //check lives ?
                if (Eidolons.getGame() instanceof IGG_Game){
                    HeroChain chain = ((IGG_Game) Eidolons.getGame()).getMetaMaster().
                            getPartyManager().getHeroChain();
                    if (chain == null) {
                        return false;
                    }
                    return !chain.findHero(item.getName()).hasLives();
                }

                return super.isBlocked(item);
            }
        };
    }


    private String getHeroDescription(Entity entity) {
//        DescriptionTooltips.getLoreMap().getVar(entity.getName());
      return   DescriptionTooltips.getDescrMap().get(entity.getName().toLowerCase())+
              "\n\nLore\n"+DescriptionTooltips.getLoreMap().get(entity.getName().toLowerCase());
    }
//    protected String getOverviewText(Entity entity) {
//        return HelpMaster.getHeroMainInfoText(item.getName());
//    }
    private boolean isShuffleDataList() {
        return true;
    }

    public IggHeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected boolean isAutoDoneEnabled() {
        if (Eidolons.getGame().isStarted())
            return false;
        return super.isAutoDoneEnabled();
    }

    @Override
    protected String getDoneText() {
        return "Onward!";
    }

    @Override
    protected String getTitle() {
        return "Eidolon Avatars";
    }

}
