package libgdx.gui.menu.selection.difficulty;

import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.SelectionPanel;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.TextMaster;
import main.content.enums.GenericEnums.DIFFICULTY;
import main.data.ability.construct.VariableManager;
import main.system.auxiliary.EnumMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/8/2018.
 * <p>
 * Just centered vertical list
 */
public class DifficultySelectionPanel extends SelectionPanel {

    public static final WAIT_OPERATIONS WAIT_OPERATION = WAIT_OPERATIONS.CUSTOM_SELECT;

    @Override
    public WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATION;
    }

    protected boolean isBackSupported() {
        return false;
    }
    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new DifficultyInfoPanel(getItemFromDiff(getDefaultDifficulty()));
    }
    protected String getTitle() {
        return "Select Difficulty";
    }
    private DIFFICULTY getDefaultDifficulty() {
//        DIFFICULTY.NOVICE
        return new EnumMaster<DIFFICULTY>().retrieveEnumConst(DIFFICULTY.class,
         OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
    }
    protected String getDoneText() {
        return "Play";
    }
    @Override
    protected List<SelectableItemData> createListData() {
        return getSupplier(DIFFICULTY.values()).get();
    }

    private Supplier<List<SelectableItemData>> getSupplier(DIFFICULTY[] values) {
        return () -> Arrays.stream(values).map(this::getItemFromDiff).collect(Collectors.toList());
    }

    private SelectableItemData getItemFromDiff(DIFFICULTY dif) {
//        TextParser.parse()
        String text = TextMaster.getDescription
         ("difficulty", dif.name());
        text = VariableManager.substitute(text,
         dif.getAttributePercentage(),
         dif.getMasteryPercentage()
        );

        return new SelectableItemData(dif.name(),
         text, null, null);
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new DifficultyListPanel(this);
    }
}
