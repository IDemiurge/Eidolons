package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.entity.obj.BattleFieldObject;
import main.game.bf.Coordinates;

import java.util.List;
import java.util.function.Predicate;

public interface GdxSpeechActions {
    boolean getNoCommentsCondition();

    Predicate<Float> getNoAnimsCondition();

    void doShake(String value, List<String> vars);


    void doCamera(String value, List<String> vars, SpeechScript.SCRIPT speechAction) ;


    void doGridObj(SpeechScript.SCRIPT speechAction, BattleFieldObject unit, Coordinates c, Boolean under);

    void doSpriteAnim(boolean bool, String value, Runnable onDone, Coordinates c, Coordinates dest, Boolean sequential);

    void doZoom(String value, List<String> vars);
}
