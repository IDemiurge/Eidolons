package main.client.cc.gui.tabs;

import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.content.*;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SkillTabNew extends HeroItemTab {

    private static final int offsetX = 8;
    List<HC_PagedListPanel> listPages;
    List<MasteryIconColumn> iconColumns;

    public SkillTabNew(MainViewPanel mvp, Unit hero) {
        super("Skills", mvp, hero);
        // TODO Auto-generated constructor stub
    }

    protected void initComps() {
        listPages = new ArrayList<>();
        for (SKILL_DISPLAY_GROUPS group : SKILL_DISPLAY_GROUPS.values()) {
            ArrayList<PARAMETER> list = new ArrayList<>(Arrays.asList(group.getMasteries()));
            ArrayList<ObjType> data = new ArrayList<>();
            for (ObjType type : this.data) {
                PARAMETER mastery = ContentManager.getPARAM(type.getProperty(G_PROPS.MASTERY));
                if (list.contains(mastery)) {
                    data.add(type);
                }
            }
            HC_SkillPagedListPanel pagedListPanel = new HC_SkillPagedListPanel(getTemplate(), hero,
                    getItemManager(), data, group.getName());
            listPages.add(pagedListPanel);
        }

    }

    @Override
    protected HC_LISTS getTemplate() {
        return HC_LISTS.SKILL;
    }

    protected void initData() {
        data = new ArrayList<>();
        String property = hero.getProperty(getPROP());
        for (String skill : StringMaster.open(property)) {
            skill = VariableManager.removeVarPart(skill);
            data.add(DataManager.getType(skill, getTYPE()));
        }
    }

    public void resetComps() {
        if (isReinitDataRequired() || isDirty()) {
            initData();
        }
        int i = 0;
        for (HC_PagedListPanel pagedListPanel : listPages) {
            ArrayList<ObjType> data = getData(SKILL_DISPLAY_GROUPS.values()[i]);
            i++;
            pagedListPanel.setData(data);
            pagedListPanel.initPages();
            getItemManager().add(pagedListPanel);
            pagedListPanel.refresh();
            if (pagedListPanel.getParent() == null) {
                addComps();
            }
        }
    }

    private ArrayList<ObjType> getData(SKILL_DISPLAY_GROUPS g) {
        ArrayList<PARAMETER> list = new ArrayList<>(Arrays.asList(g.getMasteries()));
        ArrayList<ObjType> data = new ArrayList<>();
        for (ObjType type : this.data) {
            PARAMETER mastery = ContentManager.getPARAM(type.getProperty(G_PROPS.MASTERY));
            if (list.contains(mastery)) {
                data.add(type);
            }
        }
        return data;
    }

    protected void addComps() {
        iconColumns = new ArrayList<>();
        int i = 0;
        int w = getTemplate().getTemplate().getVisuals().getWidth();
        int h = getTemplate().getTemplate().getVisuals().getHeight();
        for (HC_PagedListPanel pagedListPanel : listPages) {
            String constraints = "id " + LIST_ID + i + ", " + "pos " + offsetX + " " + h * i + "+"
                    + getMainPosY();
            add(pagedListPanel, constraints);
            MasteryIconColumn icons = new MasteryIconColumn(hero, SKILL_DISPLAY_GROUPS.values()[i]);
            constraints = "pos " + (offsetX + w + 3) + " " + (h * i) + "-2";
            add(icons, constraints);
            iconColumns.add(icons);

            i++;
            if (getItemManager() != null) {
                getItemManager().add(pagedListPanel);
            }

        }

    }

    @Override
    public void refresh() {
        for (HC_PagedListPanel pagedListPanel : listPages) {
            pagedListPanel.refresh();
        }
        for (MasteryIconColumn iconColumn : iconColumns) {
            iconColumn.refresh();
        }
    }

    protected void setBorderChecker() {
        for (HC_PagedListPanel pagedListPanel : listPages) {
            pagedListPanel.setBorderChecker(this);
        }

    }

    @Override
    protected HERO_VIEWS getVIEW() {
        return HERO_VIEWS.SKILLS;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        for (DC_FeatObj skill : hero.getSkills()) {
            if (!skill.getName().equals(value.getName())) {
                continue;
            }
            return skill.getRankBorder();
        }
        return null;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.SKILLS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.SKILLS;
    }

    @Override
    protected PROPERTY getPROP2() {
        return null;
    }

    public enum SKILL_DISPLAY_GROUPS {
        MAGIC(ValuePages.MASTERIES_MAGIC_DISPLAY),
        WEAPONS(ValuePages.MASTERIES_WEAPONS_DISPLAY),
        COMBAT(ValuePages.MASTERIES_COMBAT_DISPLAY),
        MISC(ValuePages.MASTERIES_MISC_DISPLAY);
        private PARAMETER[] masteries;

        SKILL_DISPLAY_GROUPS(PARAMETER[] masteries) {
            this.masteries = masteries;
        }

        public PARAMETER[] getMasteries() {
            return masteries;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    private final class HC_SkillPagedListPanel extends HC_PagedListPanel {
        private HC_SkillPagedListPanel(HC_LISTS list_type, Unit hero,
                                       ItemListManager itemListManager, List<ObjType> data, String listName) {
            super(list_type, hero, itemListManager, data, listName);
        }
    }

}
