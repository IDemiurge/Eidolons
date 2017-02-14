package main.client.cc.gui.neo.tree.view;

import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tree.logic.ClassTreeBuilder;
import main.client.cc.gui.neo.tree.logic.TreeMap;
import main.content.CONTENT_CONSTS.CLASS_GROUP;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.EnumMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClassTreeView extends HT_View {

    public static CLASS_GROUP[] FOCUS_WORKSPACE = {CLASS_GROUP.FIGHTER, CLASS_GROUP.KNIGHT,
            CLASS_GROUP.RANGER, CLASS_GROUP.ROGUE, CLASS_GROUP.TRICKSTER};

    public ClassTreeView(Object arg, DC_HeroObj hero) {
        super(arg, hero);
    }

    @Override
    protected PROPS getPROP() {
        return PROPS.CLASSES;
    }

    @Override
    protected OBJ_TYPES getTYPE() {
        return OBJ_TYPES.CLASSES;
    }

    @Override
    protected List<HC_Tab> initTabList() {
        List<HC_Tab> tabList = new LinkedList<>();
        List<CLASS_GROUP> classes = new LinkedList<>(Arrays.asList(CLASS_GROUP.values()));
        // if (mineOnly) {
        // }
        // for (CLASS_GROUP group : CLASS_GROUP.values())
        // if (hasClass(group)) {
        // classes.set(0, group);
        // break; // TODO needed?
        // }
        for (final CLASS_GROUP classGroup : classes) {
            if (classGroup == CLASS_GROUP.MULTICLASS) {
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
                                    e.printStackTrace();
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
        return CLASS_GROUP.values().length;
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
