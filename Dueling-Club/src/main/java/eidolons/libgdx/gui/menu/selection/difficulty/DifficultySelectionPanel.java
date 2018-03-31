package eidolons.libgdx.gui.menu.selection.difficulty;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
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

    public static final main.system.threading.WaitMaster.WAIT_OPERATIONS WAIT_OPERATION = WAIT_OPERATIONS.CUSTOM_SELECT;

    @Override
    public WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATION;
    }

    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new DifficultyInfoPanel(getItemFromDiff(getDefaultDifficulty()));
    }

    private DIFFICULTY getDefaultDifficulty() {
//        DIFFICULTY.NOVICE
        return new EnumMaster<DIFFICULTY>().retrieveEnumConst(DIFFICULTY.class,
         OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.GAME_DIFFICULTY));
    }

    @Override
    protected List<SelectableItemData> createListData() {
        return getSupplier(DIFFICULTY.values()).get();
    }

    private Supplier<List<SelectableItemData>> getSupplier(DIFFICULTY[] values) {
        return () -> Arrays.stream(values).map(dif ->
         getItemFromDiff(dif)).collect(Collectors.toList());
    }

    private SelectableItemData getItemFromDiff(DIFFICULTY dif) {
//        TextParser.parse()
        String text = TextMaster.readResource
         ("info", "difficulty", dif.name() + ".txt");
        text = VariableManager.substitute(text,
         dif.getPowerPercentage(),
         dif.getRoundsToFightMod(),
         dif.getHealthPercentageEnemy(),
         dif.getHealthPercentageAlly(),
         dif.getHealthPercentageMainHero()
        );

        return new SelectableItemData(dif.name(),
         text, null, null);
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new DifficultyListPanel(this);
    }
}
