package main.client.cc.gui.neo.tree.view;

import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.content.CONTENT_CONSTS.MASTERY;
import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SkillTreeView extends HT_View {

    public static final MASTERY[] COMBAT = new MASTERY[]{MASTERY.DEFENSE_MASTERY,
            MASTERY.DISCIPLINE_MASTERY, MASTERY.ARMORER_MASTERY, MASTERY.STEALTH_MASTERY,
            MASTERY.DETECTION_MASTERY, MASTERY.MOBILITY_MASTERY, MASTERY.ATHLETICS_MASTERY,
            MASTERY.BLADE_MASTERY, MASTERY.AXE_MASTERY, MASTERY.BLUNT_MASTERY,
            MASTERY.POLEARM_MASTERY, MASTERY.MARKSMANSHIP_MASTERY, MASTERY.TWO_HANDED_MASTERY,
            MASTERY.DUAL_WIELDING_MASTERY, MASTERY.SHIELD_MASTERY, MASTERY.UNARMED_MASTERY,};
    public static final MASTERY[] WEAPONS = new MASTERY[]{MASTERY.BLADE_MASTERY,
            MASTERY.AXE_MASTERY, MASTERY.BLUNT_MASTERY, MASTERY.POLEARM_MASTERY,
            MASTERY.MARKSMANSHIP_MASTERY, MASTERY.TWO_HANDED_MASTERY,
            MASTERY.DUAL_WIELDING_MASTERY,};
    public static final MASTERY[] FOCUS_WORKSPACE = COMBAT;
    private static final MASTERY[] AERIDAN = new MASTERY[]{

            MASTERY.SHADOW_MASTERY, MASTERY.WITCHERY_MASTERY, MASTERY.PSYCHIC_MASTERY,
            MASTERY.SPELLCRAFT_MASTERY, MASTERY.WIZARDRY_MASTERY, MASTERY.ENCHANTMENT_MASTERY,
            MASTERY.SORCERY_MASTERY, MASTERY.CONJURATION_MASTERY, MASTERY.MEDITATION_MASTERY,
            MASTERY.WARP_MASTERY, MASTERY.BLADE_MASTERY, MASTERY.DEFENSE_MASTERY,
            MASTERY.MOBILITY_MASTERY,};
    private static final MASTERY[] JIMMY = new MASTERY[]{MASTERY.BLADE_MASTERY,
            MASTERY.DEFENSE_MASTERY, MASTERY.MOBILITY_MASTERY, MASTERY.DUAL_WIELDING_MASTERY,
            MASTERY.ITEM_MASTERY, MASTERY.STEALTH_MASTERY, MASTERY.DETECTION_MASTERY,
            MASTERY.MARKSMANSHIP_MASTERY, MASTERY.ATHLETICS_MASTERY, MASTERY.ENCHANTMENT_MASTERY,};
    private static MASTERY[][] STD_WORKSPACES = {AERIDAN, JIMMY};
    // new ArrayMaster<MASTERY>().join(WEAPONS, COMBAT );
    private static MASTERY[][] STD_AV_WORKSPACES = {COMBAT, WEAPONS};
    private boolean mineOnly;
    private MASTERY[] workspace = AERIDAN;

    private List<MASTERY[]> workspaces;

    public SkillTreeView(Object arg, DC_HeroObj hero) {
        super(arg, hero);
        if (CoreEngine.isArcaneVault()) {
            STD_WORKSPACES = STD_AV_WORKSPACES;
        }
        workspace = COMBAT;
    }

    @Override
    protected int getTabCompHeight() {
        return 40;
    }

    protected int getTabCompWidth() {
        return 40;
    }

    protected int getTabPageSize() {
        return 13;
    }

    public Component initBottomPanel() {
        return new SkillBottomPanel((PARAMS) arg, hero, tree);
    }

    public Object getArg(String name) {
        return ContentManager.getPARAM(name);
    }

    protected List<HC_Tab> initTabList() {
        List<HC_Tab> tabList = new LinkedList<>();
        // 'mine only' mode
        // for (SKILL_DISPLAY_GROUPS group : SKILL_DISPLAY_GROUPS.values())
        PARAMETER[] array = getParamsFromMasteryWorkspaceArray();
        if (array == null) {
            array = ValuePages.MASTERIES;
        }

        List<PARAMETER> masteries = new LinkedList<>(Arrays.asList(array));

        if (mineOnly) {
            masteries = DC_MathManager.getUnlockedMasteries(hero);
        }
        DC_ContentManager.sortMasteries(hero, masteries);
        for (final PARAMETER mastery : masteries) {
            // DC_ContentManager.getMasteries()
            HC_Tab tab = tabMap.get(mastery);
            if (tab == null) {
                G_Component comp = new G_Panel(getPanelVisuals());
                int index = 0;
                tab = new HC_Tab(mastery.getName(), comp, index) {
                    @Override
                    public Component generateTabComp(HC_TabPanel panel) {
                        return new GraphicComponent(ImageManager.getNewBufferedImage(
                                getTabCompWidth(), getTabCompWidth())) {
                            public Image getImg() {
                                boolean locked = !DC_MathManager.isMasteryUnlocked(hero, mastery);
                                try {
                                    image = HC_Master.generateValueIcon(mastery, isSelected(),
                                            locked, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    image = ImageManager.getNewBufferedImage(getTabCompWidth(),
                                            getTabCompWidth());
                                }
                                return image;
                            }

                            ;
                        };
                    }
                };
            }
            tabList.add(tab);
            tabMap.put(mastery, tab);

        }
        return tabList;
    }

    private PARAMETER[] getParamsFromMasteryWorkspaceArray() {
        if (workspace == null) {
            return null;
        }
        List<PARAMETER> list = new LinkedList<>();
        for (MASTERY m : workspace) {
            list.add(ContentManager.getPARAM(m.toString()));
        }
        return list.toArray(new PARAMETER[list.size()]);
    }

    public boolean isSkill() {
        return true;
    }

    @Override
    public MASTERY[] getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Object[] ARRAY) {
        workspace = (MASTERY[]) ARRAY;
        setWorkspaceMode(workspace != null);
        if (workspace != null) {
            setWorkspaceTabs(null);
            if (!getWorkspaces().contains(workspace)) {
                getWorkspaces().add(workspace);
            }
        }
    }

    @Override
    public List<MASTERY[]> getWorkspaces() {
        if (workspaces == null) {
            workspaces = new LinkedList<>();
            for (MASTERY[] stdWorkspace : STD_WORKSPACES) {
                workspaces.add(stdWorkspace);
            }

        }
        return workspaces;
    }

    public void setWorkspaces(List<MASTERY[]> workspaces) {
        this.workspaces = workspaces;
    }

}
