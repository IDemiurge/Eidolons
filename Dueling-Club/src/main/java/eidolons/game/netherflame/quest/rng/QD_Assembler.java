package eidolons.game.netherflame.quest.rng;

import eidolons.game.netherflame.act.Act_I;
import eidolons.game.netherflame.quest.QD_Model;
import eidolons.game.netherflame.quest.subject.QuestSubject;

import java.util.ArrayList;
import java.util.List;

public class QD_Assembler {

    public QD_Model assemble(Act_I.QD_TYPE type, QuestSubject subject) {

        List<String> modulePaths = new ArrayList<>();
        // pre-gen? when do we transform? decide what layers are active?

        /*
        0. assign required module type(s) for subject(s)  (handmade)
        1. add required module type(s) for subject(s)
        2. add from pool of possible modules

        layer data - assign active/trig
        module header: layers = [default, subjectX, subjectY, inkConditionZ,



         */

        return null;
    }

}
