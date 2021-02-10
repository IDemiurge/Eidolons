package libgdx.gui.panels.dc.unitinfo.datasource;

import main.content.values.parameters.PARAMETER;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ResistSource {
    List<Pair<PARAMETER, String>> getMagicResistList();

    List<Pair<PARAMETER, String>> getArmorResists();

    List<Pair<PARAMETER, String>> getDurabilityResists();
}
