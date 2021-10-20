package eidolons.game.battlecraft.logic.meta.testenv;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import main.content.DC_TYPE;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.util.DialogMaster;

public class TestMetaSetupper extends MetaInitializer<TestMeta> {


    private static final String STD_CHAIN = "Base Chain";

    public TestMetaSetupper(MetaGameMaster master) {
        super(master);
    }

    @Override
    public TestMeta initMetaGame(String data) {
        TestMeta meta= new TestMeta(master);
        switch (data){
            case "PRESET":
                //stuff from in-code consts
                break;
            case "CUSTOM":
                // meta.dungeonName = initDungeon();
                // meta.encounter = initEncounter();
                meta.chain = initChain();
                meta.trueForm = initChain();


        }
        return meta;
    }

    private String initChain() {
        String picked = null;
        picked = ListChooser.chooseType(DC_TYPE.PARTY, "Chain");
        if (picked==null){
            picked = ListChooser.chooseTypes(DC_TYPE.CHARS, "", "");
        }
        if (picked==null){
            picked = STD_CHAIN;
        }
        return picked;
    }
}
