package eidolons.game.core.master;

import eidolons.content.PROPS;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.system.test.TestMasterContent;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.SpellEnums.SPELL_POOL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
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
    private static final PROPERTY DIVINED = PROPS.DIVINED_SPELLS;
    Map<MicroObj, Map<ObjType, Spell>> spellCache = new HashMap<>();
    static Map<ObjType, Spell> globalSpellCache = new HashMap<>();

    public SpellMaster(DC_Game game) {
        super(game);
    }

    public static List<Spell> getPotentialSpellsForHero(Unit entity) {
        List<Spell> list = new ArrayList<>();

        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            PARAMETER mastery = ContentValsManager.getPARAM(type.getProperty("SPELL_GROUP") + " Mastery");
//check not custom? etc?!
//            if (!CoreEngine.isContentTestMode())
//                if (!HqMaster.isContentDisplayable(type)) {
//                    continue;
//                }

            if (SkillMaster.isMasteryUnlocked(entity, mastery)) {
                Spell spell = globalSpellCache.get(type);
                if (spell == null) {
                    spell = (Spell) entity.getGame().createSpell(type, Player.NEUTRAL, new Ref(entity.getGame()));
                    globalSpellCache.put(type, spell);
                }
                spell.getRef().setSource(entity.getId());
                list.add(spell);
            }
        }

        return list;
    }

    public Spell createSpell(ObjType type, Player player, Ref ref) {
        Spell spell = new Spell(type, player, getGame(), ref);
        return spell;
    }


    public List<Spell> initSpellpool(MicroObj obj, PROPERTY PROP) {
        List<Spell> spells = new ArrayList<>();
        String spellList = obj.getProperty(PROP);
        List<String> spellpool;

        spellpool = ContainerUtils.openContainer(spellList);

        for (String typeName : spellpool) {
            Ref ref = Ref.getCopy(obj.getRef());
            ObjType type = DataManager.getType(typeName, DC_TYPE.SPELLS);
            if (type == null) {
                continue;
            }
            Map<ObjType, Spell> cache = spellCache.get(obj);
            if (cache == null) {
                cache = new HashMap<>();
                spellCache.put(obj, cache);
            }
            Spell spell = cache.get(type);
            if (spell == null) {
                spell = (Spell) getGame().createSpell(type, obj, ref);
                cache.put(type, spell);
            }

            SPELL_POOL spellPool = new EnumMaster<SPELL_POOL>().retrieveEnumConst(SPELL_POOL.class,
                    PROP.getName());
            if (spellPool != null) {
                spell.setSpellPool(spellPool);
                spell.setProperty(G_PROPS.SPELL_POOL, spellPool.toString());
            } else {
                spell.setSpellPool(null);
                spell.setProperty(G_PROPS.SPELL_POOL, "");
            }

            spells.add(spell);
        }
        return spells;
    }

    public List<Spell> getSpells(Unit obj, boolean reset) {
        if (obj == null) {
            return new ArrayList<>();
        }
        List<Spell> spells = obj.getSpells();
        if (!TestMasterContent.addAllSpells)
            if (spells != null && !reset) {
                if (!spells.isEmpty()) {
                    return spells;
                }
            }

        spells = new ArrayList<>(initSpellpool(obj, VERBATIM));
//        spells.addAll(initSpellpool(obj, DIVINED)); TODO how is it added??
        spells.addAll(initSpellpool(obj, MEMORIZED));
        return spells;
    }

    public void activateMySpell(int index) {
        LogMaster.log(1, "spell hotkey pressed " + index);
        getMySpells().get(index).invokeClicked();
    }

    public List<Spell> getSpells(Unit obj) {
        return getSpells(obj, false);
    }


    private List<Spell> getMySpells() {
        return ((Unit)
                getGame().getPlayer(true).getHeroObj()).getSpells();
    }
}
