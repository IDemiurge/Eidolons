package main.swing.components.panels.page;

import main.content.DC_TYPE;
import main.content.enums.rules.VisionEnums;
import main.content.values.properties.G_PROPS;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.entity.type.UnitType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.turn.TurnManager;
import main.rules.RuleMaster.RULE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.net.DC_IdManager;

import java.util.LinkedList;
import java.util.List;

public class DC_PagedPriorityPanel extends G_PagePanel<Unit> {

    public static final int PLP_MIN_ITEMS = 12;
    public static final String CLOCK_IMAGE = "UI\\custom\\Time.JPG";
    public static final String CLOCK_UNIT = "Time";
    private static final int VERSION = 3;
    private static Unit clockUnit;
    ObjType clockType;
    private TurnManager manager;

    public DC_PagedPriorityPanel(DC_Game game) {
        super(PLP_MIN_ITEMS, true, VERSION);
        manager = game.getTurnManager();
        clockType = new UnitType();
        clockType.setProperty(G_PROPS.TYPE, DC_TYPE.META.getName());
        // type.setTYPE_ENUM(obj_type);
        clockType.setName(CLOCK_UNIT);
        clockType.setImage(CLOCK_IMAGE);
        game.initType(clockType);
        game.getIdManager().setSpecialTypeId(clockType, DC_IdManager.TIME_ID);

        clockUnit = new Unit(clockType, game) {
            @Override
            public void construct() {
            }

            @Override
            public void toBase() {
            }

            @Override
            public void resetObjects() {
            }

            @Override
            public void afterEffects() {
            }

            @Override
            public Integer getId() {
                return DC_IdManager.TIME_ID;
            }

            @Override
            public int getZ() {
                return -999;
            }

            @Override
            public VISIBILITY_LEVEL getVisibilityLevel() {
                return VisionEnums.VISIBILITY_LEVEL.CONCEALED;
            }

            @Override
            public void invokeRightClicked() {
                getGame().getToolTipMaster().initRuleTooltip(RULE.TIME);
                super.invokeRightClicked();
            }

            public void clicked() {
                // getGame().getManager().displayRoundInfo(); pages?
                getGame().getManager().deselectInfo();
                getGame().getManager().infoSelect(getGame().getDungeonMaster().getDungeon());
            }

            ;
        };

        game.getState().removeObject(clockUnit.getId());

    }

    public static Unit getClockUnit() {
        return clockUnit;
    }

    @Override
    protected int getArrowOffsetX() {
        return 0;
        // (GuiManager.getSmallObjSize() - arrowWidth) / 2;
    }

    @Override
    protected int getArrowOffsetX2() {
        return 0;
    }

    @Override
    protected int getArrowOffsetY2() {
        return -arrowHeight;
    }

    @Override
    protected boolean isComponentAfterControls() {
        return false;
    }

    @Override
    public boolean isButtonsOnBothEnds() {
        return true;
    }

    protected boolean isControlsInverted() {
        return true;
    }

    @Override
    protected boolean isForwardPreferred() {
        return super.isForwardPreferred();
    }

    @Override
    protected G_Component createPageComponent(List<Unit> list) {
        return new PriorityPage(list);
    }

    @Override
    protected List<List<Unit>> getPageData() {
        pageSize--;
        List<List<Unit>> lists = splitList(new LinkedList(manager.getDisplayedUnitQueue()));

        for (List<Unit> list : lists) {
            list.add(clockUnit);
        }

        pageSize++;
        return lists;

    }

}
