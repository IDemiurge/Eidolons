package main.libgdx.gui.panels.dc.unitinfo.dto;

import main.content.PARAMS;
import main.content.VALUE;
import main.content.ValuePages;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.dto.DTO_Master;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.data.ArrayMaster;
import main.system.datatypes.DequeImpl;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by JustMe on 3/1/2017.
 */
public class UnitDTO_Master {
    public static UnitInfoDTO createDTO(Unit unit) {
        UnitInfoDTO dto = new UnitInfoDTO();
        dto.setAvatar(getAvatar(unit));
        dto.setMainParams(getMainParams(unit));
        dto.setAttributes(getStats(unit));
        dto.setCombatStats(getStatTabs(unit));
        dto.setResistTabs(getResistTabs(unit));
        dto.setAbilities(DTO_Master.getIconGrid(unit.getPassives()));
        dto.setEffects(DTO_Master.getIconGrid(unit.getBuffs()));
        return dto;
    }

    private static AvatarDTO getAvatar(Unit unit) {
        return new AvatarDTO(TextureCache.getOrCreate(unit.getImagePath()),
         unit.getName(),
         unit.getValue("Race"),
         unit.getParam("Level")
         );
    }

    private static MainParamsDTO getMainParams(Unit unit) {
        return new MainParamsDTO(
          unit.getParam(PARAMS. RESISTANCE),
          unit.getParam(PARAMS. DEFENSE),  unit.getParam(PARAMS. ARMOR),
          unit.getParam(PARAMS. FORTITUDE),
          unit.getParam(PARAMS. SPIRIT));
      
    }

    private static TabsDTO getResistTabs(Unit unit) {
        return DTO_Master.getTabs(unit,
         new DequeImpl<Collection<VALUE>>().addChained(
          Arrays.asList(ValuePages.RESISTANCES)));
    }

    private static TabsDTO getStatTabs(Unit unit) {
        Collection<Collection<VALUE>> list =
         new ArrayMaster<VALUE>().
          get2dListFrom3dArray(ValuePages.UNIT_INFO_PARAMS);

        return DTO_Master.getTabs(unit, list);
    }

    private static AttributesDTO getStats(Unit unit) {
        return new AttributesDTO(unit.getParam(PARAMS.STRENGTH), unit.getParam(PARAMS.VITALITY),
         unit.getParam(PARAMS.AGILITY), unit.getParam(PARAMS.DEXTERITY),
         unit.getParam(PARAMS.WILLPOWER), unit.getParam(PARAMS.SPELLPOWER),
         unit.getParam(PARAMS.INTELLIGENCE), unit.getParam(PARAMS.KNOWLEDGE),
         unit.getParam(PARAMS.WISDOM), unit.getParam(PARAMS.CHARISMA));
    }
}
