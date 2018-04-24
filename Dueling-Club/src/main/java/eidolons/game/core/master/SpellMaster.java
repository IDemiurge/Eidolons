package eidolons.game.core.master;

import eidolons.content.PROPS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.system.test.TestMasterContent;
import main.content.DC_TYPE;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/16/2017.
 */
public class SpellMaster extends Master {

    private static final PROPERTY VERBATIM = PROPS.VERBATIM_SPELLS;
    private static final PROPERTY MEMORIZED = PROPS.MEMORIZED_SPELLS;
    HashMap<MicroObj, Map<ObjType, DC_SpellObj>> spellCache = new HashMap<>();

    public SpellMaster(DC_Game game) {
        super(game);
    }

    public DC_SpellObj createSpell(ObjType type, Player player, Ref ref) {
        DC_SpellObj spell = new DC_SpellObj(type, player, getGame(), ref);
        return spell;
    }


    public List<DC_SpellObj> initSpellpool(MicroObj obj, PROPERTY PROP) {
        List<DC_SpellObj> spells = new ArrayList<>();
        String spellList = obj.getProperty(PROP);
        List<String> spellpool;

        spellpool = StringMaster.openContainer(spellList);

        for (String typeName : spellpool) {
            Ref ref = Ref.getCopy(obj.getRef());
            ObjType type = DataManager.getType(typeName, DC_TYPE.SPELLS);
            if (type == null) {
                continue;
            }
            Map<ObjType, DC_SpellObj> cache = spellCache.get(obj);
            if (cache == null) {
                cache = new HashMap<>();
                spellCache.put(obj, cache);
            }
            DC_SpellObj spell = cache.get(type);
            if (spell == null) {
                spell = (DC_SpellObj) getGame().createSpell(type, obj, ref);
                cache.put(type, spell);
            }

            SPELL_POOL spellPool = new EnumMaster<SPELL_POOL>().retrieveEnumConst(SPELL_POOL.class,
             PROP.getName());
            if (spellPool != null) {
                spell.setSpellPool(spellPool);
                spell.setProperty(G_PROPS.SPELL_POOL, spellPool.toString());
            } else {
                spell.setSpellPool(null  );
                spell.setProperty(G_PROPS.SPELL_POOL, "");
            }

            spells.add(spell);
        }
        return spells;
    }

    public List<DC_SpellObj> getSpells(Unit obj, boolean reset) {
        if (obj == null) {
            return new ArrayList<>();
        }
        List<DC_SpellObj> spells = obj.getSpells();
        if (!TestMasterContent.addAllSpells)
            if (spells != null && !reset) {
                if (!spells.isEmpty()) {
                    return spells;
                }
            }

        spells = new ArrayList<>(initSpellpool(obj, VERBATIM));
        spells.addAll(initSpellpool(obj, MEMORIZED));
        return spells;
    }

    public void activateMySpell(int index) {
        LogMaster.log(1, "spell hotkey pressed " + index);
        getMySpells().get(index).invokeClicked();
    }

    public List<DC_SpellObj> getSpells(Unit obj) {
        return getSpells(obj, false);
    }


    private List<DC_SpellObj> getMySpells() {
        return ((Unit)
         getGame().getPlayer(true).getHeroObj()).getSpells();
    }
}
