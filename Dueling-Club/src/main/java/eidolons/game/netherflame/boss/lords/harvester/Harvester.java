package eidolons.game.netherflame.boss.lords.harvester;

import eidolons.game.netherflame.boss.BOSS_PART;
import eidolons.game.netherflame.boss.BossModel;
import eidolons.game.netherflame.boss.logic.BOSS_ACTION;
import main.system.auxiliary.StringMaster;

public class Harvester extends BossModel {



    public BOSS_PART[] getParts(){
        return HARVESTER_PARTS.values();
    }
    public BOSS_ACTION[] getActions(){
        return BOSS_ACTION_REAPER.values();
    }

    public enum HARVESTER_PARTS implements  BOSS_PART{
        head,
//        back, better join them for space... or will we support custom anims on it?
        core,
        hand_1,
        hand_2,
        ;

        @Override
        public String[] getAnims() {
            return new String[0];
        }
    }


    public enum BOSS_ACTION_REAPER implements BOSS_ACTION {
        SEVER,
        SHATTER,
        DEATH_RAZOR__PURIFY{
            @Override
            public String getName() {
                return "Death Razor - Purify";
            }
        },
        PURIFIER,
        SOUL_RIP,
        TOUCH_OF_TORMENT,
        DEATH_WHIRL, //THROW SCYTHE!

        //SPELL
        NETHER_CALL,
        NETHER_BURST,
        NETHER_WAVE,
        MORTAL_SCREAM,

        VOID_RIFT,
        SUNDER,
        BLACK_MIRROR,
        NETHER_TUNNEL //SWAP
        ;

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }
}
