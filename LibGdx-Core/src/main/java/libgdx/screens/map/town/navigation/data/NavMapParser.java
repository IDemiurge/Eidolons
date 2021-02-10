package libgdx.screens.map.town.navigation.data;

import eidolons.macro.entity.MacroRef;
import eidolons.macro.map.Place;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

import java.util.Stack;

import static eidolons.macro.MacroGame.game;

/**
 * Created by JustMe on 11/23/2018.
 *
 * |  ValueContainer
 |  |  ImageContainer
 |  |  |  Image


 */
public class NavMapParser {
    public static final String testData =
     "Greyrock\n" +
     "-Inner Harbor \n" +
     "-Cliffs\n" +
     "DNG: Greyrock Caverns\n" +
     "--Outer walls \n" +
     "---Sewage Pipes \n" +
     "DNG: Sewers \n";
    /**
     * Data example:
     * -Inner Harbor
     * -Cliffs
     * DNG: Greyrock Caverns
     * --Outer walls
     * ---Sewage Pipes
     * DNG: Sewers
     * ---Northern Ledge
     * NPC: Silent Woman (baronesse gone mad?)
     * ---Guard Quarters
     * NPC: Captain of the Guard
     * Houseguard
     * ----Inner Walls
     * -----Walkway
     * OBJ: Strange Statue
     * -----Courtyard
     * ------Ironhorn Inn
     * NPC: Sullen Innkeep
     * NPC: Zak Selmy
     * <p>
     * <p>
     * SYNC with INK?
     *
     * @param data
     */

    public static final String NPC = "NPC";
    public static final String OBJECT = "NPC";
    public static final String DUNGEON = "DNG";
    public static final String DEPTH_METER = "-";
    private static final java.lang.String PREFIX_SEPARATOR = ":";
    private static Nested tip;
    private static Stack<NestedLeaf> path;

    public static void parseTestMap() {
        parseNavigationMap( game.getPlayerParty().getCurrentLocation(), testData);
    }
        public static void parseNavigationMap(Nested root, String data) {
        //same as Packer? aye, the general alg

        //output is a nested structure of objects... so i can at least set them as objects

        tip = root;
        path = new Stack<>();

        String treeData = formatResTree(data);
        String[] lines = StringMaster.splitLines(treeData);
        int result = 0;
            StrPathBuilder pathBuilder = new StrPathBuilder(PathFinder.getResPath());
        while (result >= 0) {
            result = crawlTreeLines(result, lines);
        }
    }

    private static int crawlTreeLines(int index, String[] lines) {
        //runs until the tree goes up

        int depth = 0;
        for (int i = index; i < lines.length; i++) {
            String line = lines[i];
            String name = getNameFromLine(line);
            String prefix = getPrefixFromLine(line);
            //NPC/...
            NestedLeaf child = getOrCreateObj(name, prefix);

            if (child instanceof Nested) {
                tip = (Nested) child;
            }

            int newDepth = getDepth(line);
            if (newDepth > 0 || depth > 0)
                if (newDepth <= depth) {
                    //don't add!
                    //going up again
                    int goBack = depth - newDepth + 2;
                    while (goBack-- > 0) {
                        if (path.isEmpty()){
                            throw new RuntimeException();
                        }
                        path.pop();
                    }
                    return i; //resume crawl from this index
                }
            path.push(child);
            tip.add(child);
            depth = newDepth; //we went down
        }
        return -1; //crawl is finished
    }

    private static String getPrefixFromLine(String line) {
        return line.split(PREFIX_SEPARATOR)[0];
    }

    private static NestedLeaf getOrCreateObj(String name, String prefix) {
        //cache
        switch (prefix) {
            case DUNGEON:

//            case NPC:
//                return new NPC(new ObjType(name, MACRO_OBJ_TYPES.NPC));
        }
        //non-map places?
        return new Place(game, DataManager.getType(name, MACRO_OBJ_TYPES.PLACE), new MacroRef());
    }


    private static int getDepth(String line) {
       int depth = 0;
        for (char c : line.toCharArray()) {
            if (DEPTH_METER.equalsIgnoreCase(""+c))
                break;
            depth++;
        }
        return depth ;
    }

    private static String getNameFromLine(String line) {
        String[] parts = line.split("  ");
        return parts[1];
    }


    private static String formatResTree(String resTree) {
        return resTree.replace("__", "").trim();
    }
}
