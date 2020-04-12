package eidolons.game.battlecraft.logic.dungeon.puzzle.portal;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.art.PortalSlotsCondition;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;

import java.util.Map;

public class PortalPuzzle extends Puzzle {

    Map<Coordinates, POWER_SLOT> slots = new XLinkedMap<>();

    /**
     * power the portal via Slots
     * <p>
     * step onto a slot to use your own Soulforce?
     */

    public enum POWER_SLOT {
        sphere(false, "Eldritch Sphere"),
        diamond(false, "Eldritch Hedron"),
        any(false, "Eldritch Sphere", "Eldritch Hedron"),
        any_self(true, "Eldritch Sphere", "Eldritch Hedron"),
        self(true),
        ;
        public String[] validObjectNames;
        public boolean selfValid;
        public boolean allyValid;

        POWER_SLOT(boolean selfValid, String... validObjectNames) {
            this.validObjectNames = validObjectNames;
            this.selfValid = selfValid;
        }}

    public Map<Coordinates, POWER_SLOT> getSlots() {
        return slots;
    }

    @Override
    public void setup(PuzzleSetup... setups) {
        super.setup(setups);

        for (Coordinates coordinates : slots.keySet()) {
            POWER_SLOT type = slots.get(coordinates);

            DungeonEnums.CELL_IMAGE t = getCellType(type);
            int v = getCellVariant(type);
            GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY,
                    TextureCache.getOrCreateR(GridMaster.getImagePath(t, v)));
        }

    }

    private int getCellVariant(POWER_SLOT type) {
        return 1;
    }

    @Override
    public Condition createSolutionCondition() {
        return new PortalSlotsCondition(this);
    }

    private DungeonEnums.CELL_IMAGE getCellType(POWER_SLOT type) {
        switch (type) {
            case sphere:
                return DungeonEnums.CELL_IMAGE.circle;
            case diamond:
                return DungeonEnums.CELL_IMAGE.diamond;
            case any:
                return DungeonEnums.CELL_IMAGE.octagonal;
            case any_self:
                return DungeonEnums.CELL_IMAGE.star;
            case self:
                return DungeonEnums.CELL_IMAGE.cross;
        }
        return null;
    }

    public void init() {
        String data = getData().getValue(PuzzleData.PUZZLE_VALUE.TIP);
        for (String substring : ContainerUtils.openContainer(data)) {
            POWER_SLOT slot = new EnumMaster<POWER_SLOT>().retrieveEnumConst(POWER_SLOT.class, VariableManager.removeVarPart(substring));
            Coordinates c = Coordinates.get(VariableManager.getVars(substring));

            slots.put(c, slot);
        }
    }

}
