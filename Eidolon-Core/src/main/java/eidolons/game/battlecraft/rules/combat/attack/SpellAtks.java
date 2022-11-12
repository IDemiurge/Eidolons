package eidolons.game.battlecraft.rules.combat.attack;

public class SpellAtks extends AttackTypes{

    /*
do we use equipped weapons in any way?
align this with Force natural-weapons
Invocation - special for each school or energy type?

sets - per energy? Or per school with various energy types?! E.g. Redemption - Fire+Holy...

so what else is new for dmg-spells?
Accuracy
All the same perks as Attacks - CaseFx, (counters?!) , Sneak, Armor/blockers,

How would spells go then? It's somewhat limiting to have just ... these types
+ we can't really specify costs here! These spellAtks must be packed into real spells, right?
1) "Apply double Incantation to [targets]
> raw damage bonus from spell (rare)
> effective spellpower/int/?

Review content!!!

     */
    public enum SpellAttackType {
        cantrip(125, 50, 20),
        spell(125, 50, 20),
        incantation(125, 50, 20),
        invocation(125, 50, 20),

        //unarmed? dual?
        ;
        int dmgMod;
        int atbMod;
        int atkMod; //usually a thing, eh?
        int focusCost, essenceCost; // in absolute value? Or % max?
        int spDmgMod, intDmgMod;

        SpellAttackType(int dmgMod, int atbMod, int toughnessCost) {
        }
    }

}
