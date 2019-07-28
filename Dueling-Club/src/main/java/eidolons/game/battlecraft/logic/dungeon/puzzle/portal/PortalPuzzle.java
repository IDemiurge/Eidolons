package eidolons.game.battlecraft.logic.dungeon.puzzle.portal;

import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
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
        public  boolean selfValid;
        public boolean allyValid;

        POWER_SLOT(boolean selfValid, String... validObjectNames) {
            this.validObjectNames = validObjectNames;
            this.selfValid = selfValid;
        }}

    public Map<Coordinates, POWER_SLOT> getSlots() {
        return slots;
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
