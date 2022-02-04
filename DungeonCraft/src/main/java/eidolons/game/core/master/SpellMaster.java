package eidolons.game.core.master;

import eidolons.entity.feat.active.Spell;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.netherflame.eidolon.heromake.passives.SkillMaster;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/16/2017.
 */
public class SpellMaster extends Master {

    Map<MicroObj, Map<ObjType, Spell>> spellCache = new HashMap<>();
    static Map<ObjType, Spell> globalSpellCache = new HashMap<>();

    public SpellMaster(DC_Game game) {
        super(game);
    }

    public Spell createSpell(ObjType type, Player player, Ref ref) {
        return new Spell(type, player, getGame(), ref);
    }

    public static List<Spell> getPotentialSpellsForHero(Unit entity) {
        List<Spell> list = new ArrayList<>();
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            PARAMETER mastery = ContentValsManager.getPARAM(type.getProperty("SPELL_GROUP") + " Mastery");
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


}
