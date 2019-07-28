package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.tutorial.TutorialManager;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.system.text.DescriptionTooltips;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;

public class BridgeMaster {

    private static void cinematicOn() {
//        EidolonsGame.CINEMATIC= true;

    }

    public static String processMetaData(String metaData) {
        String output = "";
        for (String substring : ContainerUtils.openContainer(metaData)) {
            SCRIPT preset = new EnumMaster<SCRIPT>().retrieveEnumConst(SCRIPT.class, substring);
            if (preset == null) {
                output += substring + ";";
            } else
            {
//                DescriptionTooltips.getTutorialMap().get(preset)
                output += preset.text + ";";
            }
        }
        if (output.isEmpty()) {
            return "";
        }
        return output.substring(0, output.length()-1);
    }

    public enum SCRIPT {
        first_maze,
        art_mosaic,
        Waves_against_Gorr,
        Gorr_ignite,

        Screamer_script,

        First_Monster_sequence,
        Soul_Golems,
        Assemble_Mirror,
        ;
        String text;
    }

    String[] hero_sequence = {

    };

    public static void nextHero() {
        TutorialManager.nextHero();
    }

    public static void transitToHero(String hero) {
        cinematicOn();
        String spritePath = "sprites/heroes/" + hero + ".txt";
        AnimMaster.onCustomAnim(spritePath, () -> {

        });

    }


}
