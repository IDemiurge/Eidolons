package main.test.debug;

import main.swing.generic.components.panels.G_ButtonPanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;
import main.test.debug.DebugMaster.HIDDEN_DEBUG_FUNCTIONS;

import java.awt.event.ActionEvent;
import java.util.List;

public class FunctionPanel extends G_ButtonPanel {

    private DebugMaster master;

    // two button panels!
    // logPanel
    // full info panel
    public FunctionPanel(DebugMaster master) {
        super(getFunctions());
        this.master = master;

    }

    public static List<String> getFunctions() {
        List<String> functions = ListMaster.toStringList(DEBUG_FUNCTIONS.values());
        // hidden... =)
        // functions.addAll(ListMaster.toStringList(HIDDEN_DEBUG_FUNCTIONS.values()));
        return functions;
    }

    @Override
    protected void setInts() {
        horizontal = false;
        columns = 4;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        // cache
        new Thread(new Runnable() {
            @Override
            public void run() {
                DEBUG_FUNCTIONS func = new EnumMaster<DEBUG_FUNCTIONS>().retrieveEnumConst(
                        DEBUG_FUNCTIONS.class, e.getActionCommand());
                if (func == null) {
                    HIDDEN_DEBUG_FUNCTIONS hfunc = new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>()
                            .retrieveEnumConst(HIDDEN_DEBUG_FUNCTIONS.class, e.getActionCommand());

                    master.executeHiddenDebugFunction(hfunc);
                    return;
                }
                master.executeDebugFunction(func);
            }
        }, "function " + e.getActionCommand()).start();
        // Game.game.getManager().reset();
    }

}
