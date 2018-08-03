package eidolons.game.module.dungeoncrawl.generator.init;

import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.system.datatypes.WeightMap;

import static main.content.enums.entity.UnitEnums.UNITS_TYPES.*;

/**
 * Created by JustMe on 8/1/2018.
 */
public class RngUnitProvider {
    public static WeightMap<String> getGroupWeightMap( DUNGEON_STYLE style, boolean underground) {
        switch (style){
            case Castle:
                break;
            case Holy:
                break;
            case Pagan:
                break;
            case DarkElegance:
                break;
            case PureEvil:
                break;
            case Brimstone:
                break;
            case Survivor:
                break;
            case Grimy:
                return underground? new WeightMap<String>()
                 .chain(UNIT_GROUP.DUNGEON, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 : new WeightMap<String>()
                 .chain(UNIT_GROUP.BANDITS, 10)
                 .chain(UNIT_GROUP.DWARVES, 10)
                 .chain(UNIT_GROUP.UNDEAD_CRIMSON, 10)
                 .chain(UNIT_GROUP.UNDEAD_PLAGUE, 10);
            case Somber:
                return  new WeightMap<String>()
                 .chain(UNIT_GROUP.BANDITS, 10)
                 .chain(UNIT_GROUP.UNDEAD, 10)
                 .chain(UNIT_GROUP.RAVENGUARD, 10);
            case Arcane:
                break;
            case Cold:
                break;
        }
        return  new WeightMap<String>().chain(UNIT_GROUP.BANDITS, 10);

    }

        public static WeightMap<String> getBossWeightMap(UNIT_GROUP group) {
        return getUnitWeightMap(group, true);
    }
        public static WeightMap<String> getUnitWeightMap(UNIT_GROUP group, boolean boss) {
        switch (group) {
            case ELEMENTALS:
                break;
            case RAVENGUARD:
                return boss ? new WeightMap<String>().
                 putChain(RAVENGUARD_COMMANDER.getName(), 10).
                 putChain(RAVENGUARD_KNIGHT.getName(), 1).
                 putChain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT.getName(), 1).
                 putChain(RAVENGUARD_SPECIALIST.getName(), 1)
                 :new WeightMap<String>().
                 putChain(RAVENGUARD_ENFORCER.getName(), 10).
                 putChain(SWORDSMAN.getName(), 6).
                 putChain(SHIELDMAN.getName(), 8).
                 putChain(PIKEMAN.getName(), 4).
                 putChain(RAVENGUARD_CROSSBOWMAN.getName(), 8).
                 putChain(RAVENGUARD_TORTURER.getName(), 5).
                 putChain(RAVENGUARD_SPECIALIST.getName(), 6).
                 putChain(RAVENGUARD_WARDEN.getName(), 5);
            case HUMANS:
                return boss ? new WeightMap<String>().
                 putChain(RAVENGUARD_COMMANDER.getName(), 10).
                 putChain(RAVENGUARD_KNIGHT.getName(), 1).
                 putChain(RAVENGUARD_WITCHUNICODE45CODEENDKNIGHT.getName(), 1).
                 putChain(RAVENGUARD_SPECIALIST.getName(), 1)
                 :new WeightMap<String>().
                 putChain(GUARDSMAN.getName(), 10).
                 putChain(SWORDSMAN.getName(), 6).
                 putChain(SHIELDMAN.getName(), 8).
                 putChain(PIKEMAN.getName(), 4).
                 putChain(RAVENGUARD_CROSSBOWMAN.getName(), 8).
                 putChain(RAVENGUARD_TORTURER.getName(), 5).
                 putChain(RAVENGUARD_SPECIALIST.getName(), 6).
                 putChain(RAVENGUARD_WARDEN.getName(), 5);
            case GREENSKINS:
                break;
            case BANDITS:
                break;
            case KNIGHTS:
                break;
            case DWARVES:
                break;
            case NORTH:
                break;
            case UNDEAD:
                break;
            case DEMONS:
                break;
            case ANIMALS:
                break;
            case MAGI:
                break;
            case CRITTERS:
                break;
            case DUNGEON:
                break;
            case FOREST:
                break;
        }
        return null;
    }

}
