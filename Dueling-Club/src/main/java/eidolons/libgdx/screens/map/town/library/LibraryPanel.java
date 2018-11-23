package eidolons.libgdx.screens.map.town.library;


import eidolons.libgdx.gui.panels.AdjustingVerticalGroup;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.map.town.library.hero.HeroSpellPanel;
import eidolons.libgdx.screens.map.town.library.repertoire.SpellRepertoirePanel;

/**
 * Created by JustMe on 3/14/2018.
 * <p>
 * on the level of ShopPanel, QuestPanel?
 * <p>
 * the new Town Interface is independent of separate Place UI's -
 */
public class LibraryPanel extends TablePanelX {

    HeroSpellPanel heroPanel;
    TablePanelX categoriesTable;
    TablePanelX bookDecor; //vfx?
    SpellInfoPanel infoPanel;
    SpellRepertoirePanel repertoirePanel;

    AdjustingVerticalGroup left;
    AdjustingVerticalGroup center;
    AdjustingVerticalGroup right;

    public LibraryPanel() {
    }

    @Override
    protected Class<?> getUserObjectClass() {
        return super.getUserObjectClass();
    }

    @Override
    public Object getUserObject() {
        return super.getUserObject();
    }

}
