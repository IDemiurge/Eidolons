package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.BattleFieldObject;
import main.system.auxiliary.RandomWizard;

public class DiceMaster {

    public static int d20(BattleFieldObject source, int dice) {
        int result = 0;
        for (int i = 0; i < dice; i++) {
            result += (RandomWizard.getRandomInt(19) + 1);
        }
        return result;  //TODO luck!
    }
}
