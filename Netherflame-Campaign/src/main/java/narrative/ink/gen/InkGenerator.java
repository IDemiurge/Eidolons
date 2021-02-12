package narrative.ink.gen;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActor;
import narrative.ink.InkEnums.INK_DIALOGUE_TEMPLATE;

/**
 * Created by JustMe on 11/20/2018.
 *
 * location + time => npc type+npc mood =>quest type+quest details => quest text

 main factor is location, time will be just kind of sorting/filtering
 same with npc type/mood => quest type

 */
public class InkGenerator {
    public void generateRootTemplate(){
        //all the std diverts... my library of responses, multiplied by mood and other

    }
        public void generateTemplates(){
            for (INK_DIALOGUE_TEMPLATE template : INK_DIALOGUE_TEMPLATE.values()) {
                //from base txt
            }
        /*
        INCLUDE
        VAR
         */
    }

    public void generate(DialogueActor actor){
        //just copy a template with prefixes?
        //refuse_surly refuse_genial refuse_dignified

//    genInk(actor, )
//        new ProcessBuilder(inklecate, inkPath)

    }
}
