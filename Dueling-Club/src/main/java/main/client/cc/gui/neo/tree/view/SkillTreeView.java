package main.client.cc.gui.neo.tree.view;

import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tabs.HC_Tab;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillTreeView extends HT_View {

    public static final MASTERY[] COMBAT = new MASTERY[]{SkillEnums.MASTERY.DEFENSE_MASTERY,
     SkillEnums.MASTERY.DISCIPLINE_MASTERY, SkillEnums.MASTERY.ARMORER_MASTERY, SkillEnums.MASTERY.STEALTH_MASTERY,
     SkillEnums.MASTERY.DETECTION_MASTERY, SkillEnums.MASTERY.MOBILITY_MASTERY, SkillEnums.MASTERY.ATHLETICS_MASTERY,
     SkillEnums.MASTERY.BLADE_MASTERY, SkillEnums.MASTERY.AXE_MASTERY, SkillEnums.MASTERY.BLUNT_MASTERY,
     SkillEnums.MASTERY.POLEARM_MASTERY, SkillEnums.MASTERY.MARKSMANSHIP_MASTERY, SkillEnums.MASTERY.TWO_HANDED_MASTERY,
     SkillEnums.MASTERY.DUAL_WIELDING_MASTERY, SkillEnums.MASTERY.SHIELD_MASTERY, SkillEnums.MASTERY.UNARMED_MASTERY,};
    public static final MASTERY[] WEAPONS = new MASTERY[]{SkillEnums.MASTERY.BLADE_MASTERY,
     SkillEnums.MASTERY.AXE_MASTERY, SkillEnums.MASTERY.BLUNT_MASTERY, SkillEnums.MASTERY.POLEARM_MASTERY,
     SkillEnums.MASTERY.MARKSMANSHIP_MASTERY, SkillEnums.MASTERY.TWO_HANDED_MASTERY,
     SkillEnums.MASTERY.DUAL_WIELDING_MASTERY,};
    public static final MASTERY[] FOCUS_WORKSPACE = COMBAT;
    private static final MASTERY[] AERIDAN = new MASTERY[]{

     SkillEnums.MASTERY.SHADOW_MASTERY, SkillEnums.MASTERY.WITCHERY_MASTERY, SkillEnums.MASTERY.PSYCHIC_MASTERY,
     SkillEnums.MASTERY.SPELLCRAFT_MASTERY, SkillEnums.MASTERY.WIZARDRY_MASTERY, SkillEnums.MASTERY.ENCHANTMENT_MASTERY,
     SkillEnums.MASTERY.SORCERY_MASTERY, SkillEnums.MASTERY.CONJURATION_MASTERY, SkillEnums.MASTERY.MEDITATION_MASTERY,
     SkillEnums.MASTERY.WARP_MASTERY, SkillEnums.MASTERY.BLADE_MASTERY, SkillEnums.MASTERY.DEFENSE_MASTERY,
     SkillEnums.MASTERY.MOBILITY_MASTERY,};
    private static final MASTERY[] JIMMY = new MASTERY[]{SkillEnums.MASTERY.BLADE_MASTERY,
     SkillEnums.MASTERY.DEFENSE_MASTERY, SkillEnums.MASTERY.MOBILITY_MASTERY, SkillEnums.MASTERY.DUAL_WIELDING_MASTERY,
     SkillEnums.MASTERY.ITEM_MASTERY, SkillEnums.MASTERY.STEALTH_MASTERY, SkillEnums.MASTERY.DETECTION_MASTERY,
     SkillEnums.MASTERY.MARKSMANSHIP_MASTERY, SkillEnums.MASTERY.ATHLETICS_MASTERY, SkillEnums.MASTERY.ENCHANTMENT_MASTERY,};
    private static MASTERY[][] STD_WORKSPACES = {AERIDAN, JIMMY};
    // new ArrayMaster<MASTERY>().join(WEAPONS, COMBAT );
    private static MASTERY[][] STD_AV_WORKSPACES = {COMBAT, WEAPONS};
    private boolean mineOnly;
    private MASTERY[] workspace = AERIDAN;

    private List<MASTERY[]> workspaces;

    public SkillTreeView(Object arg, Unit hero) {
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
        List<HC_Tab> tabList = new ArrayList<>();
        // 'mine only' mode
        // for (SKILL_DISPLAY_GROUPS group : SKILL_DISPLAY_GROUPS.values())
        PARAMETER[] array = getParamsFromMasteryWorkspaceArray();
        if (array == null) {
            array = ValuePages.MASTERIES;
        }

        List<PARAMETER> masteries = new ArrayList<>(Arrays.asList(array));

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
                                    main.system.ExceptionMaster.printStackTrace(e);
                                    image = ImageManager.getNewBufferedImage(getTabCompWidth(),
                                     getTabCompWidth());
                                }
                                return image;
                            }

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
        List<PARAMETER> list = new ArrayList<>();
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
            workspaces = new ArrayList<>();
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
