package eidolons.netherflame.eidolon.heromake;

/**
 * For TestEnv: Dynamic pool of heroes, easily customizable via copy/saveAs > To test out Feats etc A pool of Standard
 * heroes - slowly updated to balance them out
 * <p>
 * creating these: First Std, then expand same xml-data? swing-dialog on start to pick?
 * <p>
 * Std heroes creation string-form => AV / HQ I can create the base this way, then Level Up ! besides, Level Up is SURE
 * to be part of the EA exp!
 * <p>
 * why generate types for backgrounds and races if we can do without easily? eventually, it might be easier and more
 * Mod-friendly to modify this via AV , but not now!
 * <p>
 * just a sizable constructor with all necessary stuff, maybe with string of same format even!
 */

public class NF_HeroMaker {

    public enum HERO_RACES {
        STONESHIELD_DWARF(),
        ;
        String name;
        String attributes;
        String resistances; //just grades? to modify from default of 25...

    }

    public enum HERO_BACKGROUND {
/*
+ Mastery, AND attrs?
if these are combinable, we should consider overlapping cases
TRICK - the attr val we got after all base constructs are the BASE
 */

    }
}
