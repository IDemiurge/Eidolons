package main.swing.components.panels.page;

import main.client.cc.gui.misc.BorderChecker;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager.BORDER;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DC_PagedSpellPanel extends G_PagedListPanel<DC_SpellObj> implements BorderChecker {

    private static final int PAGE_SIZE = 10;
    private static final int WRAP = 2;
    private static final boolean vertical = true;
    private static final int VERSION = 3;
    private DC_Game game;

    public DC_PagedSpellPanel(DC_Game game) {
        super(PAGE_SIZE, vertical, VERSION);
        this.game = game;
    }

    @Override
    public boolean isButtonsOnBothEnds() {
        return true;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        return new SpellPage(getObj(), game.getState(), WRAP, PAGE_SIZE, null);
    }

    @Override
    protected G_Component createPageComponent(List<DC_SpellObj> list) {
        return new SpellPage(getObj(), game.getState(), WRAP, PAGE_SIZE, list);
    }

    @Override
    protected List<List<DC_SpellObj>> getPageData() {
        // sort spells?
        List<DC_SpellObj> spells = new LinkedList<>(game.getManager().getSpells(
                (DC_HeroObj) getObj()));
        // List<List<DC_SpellObj>> list = new ListMaster<DC_SpellObj>()
        // .splitList(PAGE_SIZE, spells);
        // if (list.isEmpty())
        // return list;
        //
        // List<DC_SpellObj> lastList = list.get(list.size() - 1);
        // ListMaster.fillWithNullElements(lastList, PAGE_SIZE);

        return splitList(spells);
    }

    @Override
    protected boolean isDoubleButtons() {
        return false;
    }

    public void highlight(Set<Obj> set) {
        refresh();

    }

    public void highlightsOff() {
        refresh();

    }

    public int getPanelHeight() {
        return PAGE_SIZE / WRAP * GuiManager.getSmallObjSize();
    }

    public int getPanelWidth() {
        return GuiManager.getSmallObjSize() * WRAP;
    }

}
