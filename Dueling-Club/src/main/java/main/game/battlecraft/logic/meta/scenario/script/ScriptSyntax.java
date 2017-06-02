package main.game.battlecraft.logic.meta.scenario.script;

/**
 * Created by JustMe on 5/18/2017.
 */
public class ScriptSyntax {
    /*
    Trigger Script syntax:
    Standard_Event_type>condition(args) AND/OR condition2(args)>function(args) | ...
     */
    public static final String SCRIPTS_SEPARATOR = "|";
    public static final String SCRIPTS_SEPARATOR_ALT = " & ";
    public static final String SCRIPT_ARGS_SEPARATOR = ",";
    public static final String PART_SEPARATOR = ">";
    public static final String COMMENT_OPEN = "***";
    public static final String COMMENT_CLOSE = "###";
    public static final String SPAWN_POINT ="point" ;
}
