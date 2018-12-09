package eidolons.macro.entity.library;

import eidolons.entity.active.Spell;
import eidolons.entity.obj.unit.Unit;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by JustMe on 12/5/2018.
 *
 * set main to last?
 * increase cost for non-last
 */
public class GrimoireManager {

    GrimoireData main;
    GrimoireData alt;
    GrimoireData shift;
public static final int MAX_PER_GRIMOIRE =6;

    public void initData(Unit unit) {
        //auto-allocate V and M spells to G's

        int i =0;
        Set<Set<Spell>> dataSets = new LinkedHashSet<>();
        Set<Spell> set;
        dataSets.add(set = new LinkedHashSet<>());
        for (Spell spell : unit.getSpells()) {
            set.add(spell);
            if (i++ >=MAX_PER_GRIMOIRE){
                i=0;
                dataSets.add(set = new LinkedHashSet<>());
            }
        }
    }
}
