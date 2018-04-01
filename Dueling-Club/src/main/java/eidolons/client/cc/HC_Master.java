package eidolons.client.cc;

import eidolons.client.cc.gui.MainPanel.HERO_TABS;
import eidolons.client.cc.gui.MainViewPanel.HERO_VIEWS;
import eidolons.client.cc.gui.neo.tree.HT_Node;
import eidolons.client.cc.gui.neo.tree.t3.ThreeTreeView;
import eidolons.client.cc.gui.neo.tree.view.ClassTreeView;
import eidolons.client.cc.gui.neo.tree.view.HT_View;
import eidolons.client.cc.gui.neo.tree.view.SkillTreeView;
import eidolons.client.dc.Launcher;
import eidolons.client.dc.Launcher.VIEWS;
import eidolons.content.DC_ContentManager;
import eidolons.content.DC_ContentManager.ATTRIBUTE;
import eidolons.content.PROPS;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.system.MetaEnums;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HC_Master {

    private static WORKSPACE_GROUP filterWorkspaceGroup = MetaEnums.WORKSPACE_GROUP.COMPLETE;
    private static PARAMETER lastClickedMastery;
    private static PARAMETER lastClickedAttribute;
    private static Map<Object, Image> valueImgCache = new HashMap<>();
    private static Map<Object, Image> valueImgCacheLockedSelected = new HashMap<>();
    private static Map<Object, Image> valueImgCacheLocked = new HashMap<>();
    private static Map<Object, Image> valueImgCacheSelected = new HashMap<>();
    private static HT_Node selectedTreeNode;
    private static HT_Node selectedTreeNodePrevious;
    private static Map<Unit, ThreeTreeView> t3SkillMap = new HashMap<>();
    private static Map<Unit, ThreeTreeView> t3ClassMap = new HashMap<>();
    private static ThreeTreeView t3View;
    private static JFrame avTreeWindow;
    private static HT_View avTreeView;
    private static boolean skill;
    private static JFrame avClassTreeWindow;
    private static HT_View avClassTreeView;
    private static Object treeArg;
    private static Comparator<? super Entity> entitySorter;

    public static PARAMETER getSelectedMastery() {

        return lastClickedMastery;
    }

    public static ATTRIBUTE getSelectedAttribute() {
        for (ATTRIBUTE attr : ATTRIBUTE.values()) {
            if (attr.getParameter() == lastClickedAttribute) {
                return attr;
            }
        }
        // for (ATTRIBUTE attr : ATTRIBUTE.values()) {
        // if (attr.getParameter() == getHeroPanel(getHeroPanel().getHero()
        // // getHero()
        // ).getMiddlePanel().getScc().getAttrPanel().getParams())
        // return attr;
        // }
        return null;
    }

    public static List<ObjType> getRequiredSkills(ObjType selectedType, boolean or) {
        List<ObjType> list = new ArrayList<>();
        for (String s : StringMaster.open(selectedType
         .getProperty(PROPS.SKILL_REQUIREMENTS))) {
            if (s.contains(StringMaster.OR)) {
                if (!or) {
                    continue;
                } else {
                    for (String s1 : StringMaster.open(s, StringMaster.OR)) {
                        list.add(DataManager.getType(s1, DC_TYPE.SKILLS));
                    }
                }
            }

            list.add(DataManager.getType(s, DC_TYPE.SKILLS));
        }
        return list;

    }

    public static Image generateValueIcon(PARAMETER param, boolean locked) {
        boolean selected = param == lastClickedAttribute || param == lastClickedMastery;
        return generateValueIcon(param, selected, locked, true);
    }

    public static Image generateClassIcon(CLASS_GROUP classGroup, boolean selected, boolean locked,
                                          Unit hero) {
        if (CoreEngine.isArcaneVault()) {
            locked = false;
        }
        Image classIcon = ImageManager.getImage("ui\\components\\ht\\class icons\\"
         + classGroup.getName() + ".jpg");
        if (!ImageManager.isValidImage(classIcon)) {
            classIcon = ImageManager.getNewBufferedImage(54, 36);
        }
        String tabImagePath = ("ui\\components\\ht\\class tab");
        if (selected) {
            tabImagePath += " selected glow shadow";
        } else if (locked) {
            tabImagePath += " darkened";
        }
        Image tabImage = ImageManager.getImage(tabImagePath + ".png");
        Image image = ImageManager.getNewBufferedImage(tabImage.getWidth(null), tabImage
         .getHeight(null));

        image.getGraphics().drawImage(classIcon, 2, 6, null);
        image.getGraphics().drawImage(tabImage, 0, 0, null);

        return image;

    }

    public static Image getClassIcon(CLASS_GROUP classGroup, Unit hero) {
        if (hero == null) {
            hero = CharacterCreator.getHero();
        }
        for (DC_FeatObj c : new ListMaster<DC_FeatObj>().invertList(new ArrayList<>(hero
         .getClasses()))) {

            if (StringMaster
             .compareByChar(c.getProperty(G_PROPS.CLASS_GROUP), classGroup.getName())) {
                return c.getIcon().getImage();
            }

        }

        ObjType baseClassType = DC_ContentManager.getBaseClassType(classGroup);
        if (baseClassType == null) {
            return ImageManager.getNewBufferedImage(40, 40);
        }
        return baseClassType.getIcon().getImage();
    }

    public static Image generateValueIcon(Object arg, boolean selected, boolean locked,
                                          boolean skill) {
        // return ImageManager.getValueIcon(param);
        if (CoreEngine.isArcaneVault()) {
            locked = false;
        }
        Map<Object, Image> cache = valueImgCache;
        if (locked) {
            if (selected) {
                cache = valueImgCacheLockedSelected;
            } else {
                cache = valueImgCacheLocked;
            }
        } else if (selected) {
            cache = valueImgCacheSelected;
        }

        Image img = cache.get(arg);
        // if (img != null)
        // return img;
        img = ImageManager.getNewBufferedImage(40, 40);
        if (selected)
        // img = ImageManager.applyBorder(img,
        // BORDER.BACKGROUND_HIGHLIGHT_32);
        {
            img = ImageManager.applyImage(img, BORDER.BACKGROUND_HIGHLIGHT_32.getImage(), 1, 1,
             false);
        }
        // Image valueIcon = ImageManager.getValueIcon(param);
        // if (valueIcon.getHeight(null)<40){
        // // replace with composite of the right size
        // }

        Image valueIcon = skill ? ImageManager.getValueIcon((VALUE) arg) : getClassIcon(
         (CLASS_GROUP) arg, null);
        img = ImageManager.applyImage(img, valueIcon, 1, 1, false);
        if (locked)
        // additional darkening?
        {
            img = ImageManager.applyBorder(img, BORDER.DARKENING_32);
        }
        // STD_IMAGES.background_highlight_32;

        cache.put(arg, img);
        return img;
    }

    public static void setLastClickedMastery(PARAMETER param) {
        lastClickedMastery = param;
    }

    public static void setLastClickedAttribute(PARAMETER param) {
        lastClickedAttribute = param;

    }

    public static void goToSkillTree(PARAMETER param) {
        int index = EnumMaster.getEnumConstIndex(HERO_TABS.class, HERO_TABS.SKILLS);
        if (CharacterCreator.getPanel().getIndex() != index) {
            CharacterCreator.getPanel().getTabs().select(index);
        }
        CharacterCreator.getPanel().getMvp().goToSkillTree(param);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.SLING);

    }

    public static HT_Node getSelectedTreeNode() {
        return selectedTreeNode;
    }

    public static void setSelectedTreeNode(HT_Node node) {
        if (node != null) {
            DC_Game.game.getValueHelper().setEntity(node.getType());
        }
        setPreviousSelectedTreeNode(getSelectedTreeNode());
        selectedTreeNode = node;
    }

    public static HT_Node getPreviousSelectedTreeNode() {
        return selectedTreeNodePrevious;
    }

    public static void setPreviousSelectedTreeNode(HT_Node node) {
        selectedTreeNodePrevious = node;
    }

    public static void toggleT3ClassSkillView() {
        Boolean skill_class_spell = getT3Mode();
        if (skill_class_spell != null) {
            // CharacterCreator.getPanel().getMvp().activateView(
            // !skill_class_spell ? HERO_VIEWS.SKILLS : HERO_VIEWS.CLASSES);
            CharacterCreator.getPanel().getMvp().setCurrentView(getHeroView(!skill_class_spell));
            Launcher.setView(getT3View(CharacterCreator.getHero(), !skill_class_spell), VIEWS.T3);
        }

    }

    private static HERO_VIEWS getHeroView(Boolean skill_class_spell) {
        return skill_class_spell ? HERO_VIEWS.SKILLS : HERO_VIEWS.CLASSES;
    }

    public static void nextHero() {
        Unit hero = CharacterCreator.getHero();
        Unit nextHero = CharacterCreator.getParty().getNextHero(hero);
        if (hero == nextHero) {
            return;
        }
        CharacterCreator.setHero(nextHero);
        Boolean skill_class_spell = getT3Mode();
        Launcher.setView(getT3View(hero, skill_class_spell), VIEWS.T3);

    }

    public static void toggleT3View() {
        Boolean skill_class_spell = getT3Mode();
        if (Launcher.getView() == VIEWS.T3) {
            CharacterCreator.getPanel().getMvp().setCurrentView(getHeroView(skill_class_spell));
            Launcher.resetView(VIEWS.HC);
            CharacterCreator.refreshGUI();
        } else {
            Launcher.setView(getT3View(CharacterCreator.getHero(), skill_class_spell), VIEWS.T3);
        }

    }

    private static Boolean getT3Mode() {
        Boolean skill_class_spell;
        // if (CharacterCreator.getPanel().getMvp().getCurrentView() !=
        // HERO_VIEWS.LIBRARY) { }
        skill_class_spell = !(CharacterCreator.getPanel().getMvp().getCurrentView() == HERO_VIEWS.CLASS_TREE
         || CharacterCreator.getPanel().getMvp().getCurrentView() == HERO_VIEWS.CLASSES);
        return skill_class_spell;
    }

    public static ThreeTreeView getT3View(Unit hero, Boolean skill_class_spell) {
        t3View = getT3Map(skill_class_spell).get(hero);
        if (t3View == null) {
            t3View = new ThreeTreeView(hero, skill_class_spell);
            getT3Map(skill_class_spell).put(hero, t3View);
        }
        return t3View;
    }

    public static Map<Unit, ThreeTreeView> getT3Map(Boolean skill_class_spell) {
        if (skill_class_spell == null) {
            return t3SkillMap;
        }

        return skill_class_spell ? t3SkillMap : t3ClassMap;
    }

    public static ThreeTreeView getT3View() {
        return t3View;
    }

    public static JFrame showHeroTreeInWindow(ObjType selectedType) {
        initTreeArg(selectedType);

        if (getAvTreeWindow() != null) {
            // gotoTree
            // disable caching!
            getAvTreeView().tabSelected(treeArg.toString());
            return getAvTreeWindow();
        }
        boolean reinitView = getAvTreeWindow() == null;
        if (getAvTreeWindow() != null) {
            reinitView = !getAvTreeWindow().isVisible();
        }
        if (reinitView) {
            initTreeView();
        } else {
            getAvTreeView().tabSelected(treeArg.toString());
        }

        if (getAvTreeWindow() != null) {
            getAvTreeWindow().setVisible(true);
        } else {

            // G_Panel comp = new G_Panel();
            setAvTreeWindow(GuiManager.inNewWindow(getAvTreeView(), "Tree", new Dimension(
             VISUALS.TREE_VIEW.getWidth() + 15, VISUALS.TREE_VIEW.getHeight() + 50)));

            getAvTreeWindow().setVisible(false);
            getAvTreeWindow().setUndecorated(true); // 'displayable'??
            getAvTreeWindow().setVisible(true);

            getAvTreeWindow().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        }
        return getAvTreeWindow();

    }

    public static void initTreeArg(ObjType selectedType) {
        skill = selectedType.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS;
        if (skill) {
            treeArg = ContentManager.getPARAM(selectedType.getProperty(G_PROPS.MASTERY));
        } else {
            treeArg = new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class,
             selectedType.getProperty(G_PROPS.CLASS_GROUP));
        }
    }

    public static void initTreeView() {
        Unit hero = new Unit(new ObjType(DC_Game.game));// CharacterCreator.getNewHero();
        DC_Game.game.getRequirementsManager().setHero(hero);
        CharacterCreator.setHero(hero);

        setAvTreeView(null);
        if (skill) {
            setAvTreeView(new SkillTreeView(treeArg, hero));
        } else {
            setAvTreeView(new ClassTreeView(treeArg, hero));
        }

        // treeView.getTabPanel().adjustPageIndexToSelectTab(treeView.getTabList().getOrCreate(i));
        // treeView.getTabPanel().select(i)
        getAvTreeView().tabSelected(treeArg.toString());
        getAvTreeView().refresh();
    }

    public static JFrame getAvTreeWindow() {
        if (!skill) {
            return avClassTreeWindow;
        }
        return avTreeWindow;
    }

    public static void setAvTreeWindow(JFrame avTreeWindow) {
        if (!skill) {
            HC_Master.avClassTreeWindow = avTreeWindow;
        }
        HC_Master.avTreeWindow = avTreeWindow;
    }

    public static HT_View getAvTreeView() {
        if (!skill) {
            return avClassTreeView;
        }
        return avTreeView;
    }

    public static void setAvTreeView(HT_View avTreeView) {
        if (!skill) {
            HC_Master.avClassTreeView = avTreeView;
        }
        HC_Master.avTreeView = avTreeView;
    }

    public static WORKSPACE_GROUP getFilterWorkspaceGroup() {
        return filterWorkspaceGroup;
    }

    public static void setFilterWorkspaceGroup(WORKSPACE_GROUP filterWorkspaceGroup) {
        HC_Master.filterWorkspaceGroup = filterWorkspaceGroup;
    }

    public static Comparator<? super Entity> getEntitySorter() {
        return entitySorter;
    }

    public static void setEntitySorter(Comparator<? super Entity> entitySorter) {
        HC_Master.entitySorter = entitySorter;
    }

}
