package macro;

import eidolons.macro.global.World;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 10/13/2018.
 */
public class FauxMacroGame extends MacroGame {
    private static FauxMacroGame instance; //why singleton?

    public FauxMacroGame() {
    }

    public static FauxMacroGame getInstance() {
        if (instance == null) {
            instance = new FauxMacroGame();
        }
        return instance;
    }


    @Override
    public void start(boolean host) {

    }

    protected World generateWorld() {
        ObjType type= DataManager.getType("Test World", MACRO_OBJ_TYPES.WORLD);

        return new World(this, type, ref);
    }
    @Override
    public void init() {
        super.init();
    }
}
