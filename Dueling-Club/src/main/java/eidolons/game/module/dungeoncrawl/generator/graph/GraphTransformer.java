package eidolons.game.module.dungeoncrawl.generator.graph;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.GRAPH_RULE;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GraphTransformer {

    public void applyRule(GRAPH_RULE rule, LevelGraph graph, Object... args) {
        switch (rule) {
            case EXTEND:
                //main path?
                GraphPath path = (GraphPath) args[0];
                break;
            case LOCK:
            case TWIN_PATH:
                break;
            case SHORTCUT:
                //link to exit from the middle of a main path
                break;
            case DUPLICATE:
                //another set of graph edges
                break;
            case BLOCK:
                  path = (GraphPath) args[0];
                break;
            case CIRCLE_BACK:
                //make a non-main path go back to start, lock the final link with a key
                //and place it in the final room of the original path
                break;
        }
    }
}
