package main.client.cc.gui.neo.tree.view;

import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tree.logic.ClassTreeBuilder;
import main.client.cc.gui.neo.tree.logic.TreeMap;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.EnumMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class ClassTreeView extends HT_View {

    public static CLASS_GROUP[] FOCUS_WORKSPACE = {HeroEnums.CLASS_GROUP.FIGHTER, HeroEnums.CLASS_GROUP.KNIGHT,
            HeroEnums.CLASS_GROUP.RANGER, HeroEnums.CLASS_GROUP.ROGUE, HeroEnums.CLASS_GROUP.TRICKSTER};

    public ClassTreeView(Object arg, Unit hero) {
        super(arg, hero);
    }

    @Override
    protected PROPS getPROP() {
        return PROPS.CLASSES;
    }

    @Override
    protected DC_TYPE getTYPE() {
        return DC_TYPE.CLASSES;
    }

    @Override
    protected List<HC_Tab> initTabList() {
        List<HC_Tab> tabList = new ArrayList<>();
        List<CLASS_GROUP> classes = new ArrayList<>(Arrays.asList(HeroEnums.CLASS_GROUP.values()));
        // if (mineOnly) {
        // }
        // for (CLASS_GROUP group : CLASS_GROUP.values())
        // if (hasClass(group)) {
        // classes.set(0, group);
        // break; // TODO needed?
        // }
        for (final CLASS_GROUP classGroup : classes) {
            if (classGroup == HeroEnums.CLASS_GROUP.MULTICLASS) {
                continue;
            }
            HC_Tab tab = tabMap.get(classGroup);
            if (tab == null) {
                G_Component comp = new G_Panel(getPanelVisuals()) {
                    public boolean isBackgroundVisuals() {
                        return !super.isBackgroundVisuals();
                    }

                    ;
                };
                int index = 0;
                tab = new HC_Tab(classGroup.getName(), comp, index) {
                    @Override
                    public Component generateTabComp(HC_TabPanel panel) {
                        return new GraphicComponent(ImageManager.getNewBufferedImage(
                                isSelected() ? getSelectedTabCompWidth() : getTabCompWidth(),
                                getTabCompHeight())) {
                            public Image getImg() {
                                boolean locked = !hasClass(classGroup);
                                try {
                                    image = HC_Master.generateClassIcon(classGroup, isSelected(),
                                            locked, hero);
                                } catch (Exception e) {
                                    main.system.ExceptionMaster.printStackTrace(e);
                                    image = ImageManager.getNewBufferedImage(getTabCompWidth(),
                                            getTabCompWidth());
                                }
                                setCompSize(new Dimension(image.getWidth(null), image
                                        .getHeight(null)));
                                return image;
                            }

                            ;
                        };
                    }
                };
            }
            tabList.add(tab);
            tabMap.put(classGroup, tab);
        }

        return tabList;
    }

    protected int getSelectedTabCompWidth() {
        return 67;
    }

    protected TreeMap buildTree() {
        return new ClassTreeBuilder(arg).build();
    }

    @Override
    protected boolean tryIncrementRank(ObjType type) {
        return super.tryIncrementRank(type);
    }

    @Override
    public boolean isSkill() {
        return false;
    }

    public HC_TabPanel getDisplayedTabPanel() {
        return tabs;
    }

    private boolean hasClass(CLASS_GROUP group) {
        if (CoreEngine.isArcaneVault()) {
            return false;
        }
        for (DC_FeatObj CLASS : hero.getClasses()) {
            if (CLASS.checkProperty(G_PROPS.CLASS_GROUP, group.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Component initBottomPanel() {
        return new ClassBottomPanel((CLASS_GROUP) arg, hero, tree);
    }

    @Override
    public Object getArg(String name) {
        return new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class, name);
    }

    @Override
    protected int getTabCompWidth() {
        return 60;
    }

    @Override
    protected int getTabPageSize() {
        return HeroEnums.CLASS_GROUP.values().length;
    }

    @Override
    protected int getTabCompHeight() {
        return 40;
    }

    @Override
    public Object[] getWorkspace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List getWorkspaces() {
        // TODO Auto-generated method stub
        return null;
    }

}
