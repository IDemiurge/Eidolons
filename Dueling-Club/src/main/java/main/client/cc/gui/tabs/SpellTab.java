package main.client.cc.gui.tabs;

import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.client.cc.gui.tabs.lists.DivinationPanel;
import main.client.cc.gui.tabs.lists.MemorizedList;
import main.client.cc.gui.tabs.lists.VerbatimList;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.DC_RequirementsManager;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

public class SpellTab extends HeroItemTab {

    private static final String MEMORIZED_LIST_ID = "memlist";
    private static final String VERBATIM_LIST_ID = "verblist";

    private static final String something = "50";
    private static final String DIVINATION_LIST_ID = null;

    private MemorizedList memorized;
    private VerbatimList verbatim;
    private DivinationPanel divination;

    public SpellTab(MainViewPanel mvp, Unit hero) {
        super("Spellbook", mvp, hero);
    }

    public boolean isReinitDataRequired() {
        return true;
    }

    protected void initData() {
        data = DataManager.toTypeList(StringMaster.openContainer(hero
         .getProperty(getPROP2())), getTYPE());
    }

    @Override
    protected void addComps() {
        super.addComps();
        addVerbatim();
        addMemorized();
        addDivination();
    }

    protected HC_LISTS getTemplate() {
        return HC_LISTS.SPELLBOOK;
    }

    private void addMemorized() {
        memorized = new MemorizedList(hero, getItemManager());
        add(memorized, "id " + MEMORIZED_LIST_ID + ", pos " + VERBATIM_LIST_ID
         + ".x2+" + something + " " + LIST_ID + ".y2");

    }

    private void addVerbatim() {
        verbatim = new VerbatimList(hero, getItemManager());
        add(verbatim, "id " + VERBATIM_LIST_ID + ", pos @center_x-width*2/3+5 "
         + LIST_ID + ".y2");
    }

    private void addDivination() {
        divination = new DivinationPanel(hero, getItemManager());
        add(divination, "id " + DIVINATION_LIST_ID + ", pos @center_x "
         + VERBATIM_LIST_ID + ".y2");
    }

    // @Override
    // protected String getMainPosY() {
    // return VERBATIM_LIST_ID + ".y2";
    // }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.SPELLS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.LEARNED_SPELLS;
    }

    @Override
    protected PROPERTY getPROP2() {
        return PROPS.SPELLBOOK;
    }

    @Override
    protected HERO_VIEWS getVIEW() {
        return HERO_VIEWS.LIBRARY;
    }

    @Override
    public BORDER getBorder(ObjType item) {
        // verbatim OR memorized
        // if (checkHero(item, false))
        // return null;
        if (checkHero(item, true)) {
            return null;
        }
        return BORDER.HIDDEN;

    }

    private boolean checkHero(ObjType item, boolean alt) {
        return hero
         .getGame()
         .getRequirementsManager()
         .check(hero, item, !alt ? DC_RequirementsManager.NORMAL_MODE
          : DC_RequirementsManager.ALT_MODE) == null;
    }
}
