package main.game.module.adventure.gui.party;

import main.content.PARAMS;
import main.content.VALUE;
import main.content.values.parameters.MACRO_PARAMS;
import main.game.module.adventure.entity.MacroParty;

import java.util.Arrays;

public class PartyInfoPanel extends Header {
    /*
     * 1000 pixel height?
	 */

    public final static VALUE[] party_info_values = {MACRO_PARAMS.PROVISIONS,
     PARAMS.GOLD, PARAMS.GOLD, PARAMS.GOLD, MACRO_PARAMS.PROVISIONS,
     MACRO_PARAMS.PROVISIONS,

    };

    public PartyInfoPanel(MacroParty party) {
        super(Arrays.asList(party_info_values), party);
    }
}
