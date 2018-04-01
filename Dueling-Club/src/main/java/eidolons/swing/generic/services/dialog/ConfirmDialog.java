package eidolons.swing.generic.services.dialog;

import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

public class ConfirmDialog extends OptionDialog {
    /*
     * extends option dialog - with any number...
     *
     * boolean horizontal;
     *
     * raw text could be an option!
     */
    public static final WAIT_OPERATIONS CONFIRM_DIALOG = WAIT_OPERATIONS.OPTION_DIALOG;
    private String TRUE;
    private String FALSE;
    private String NULL;

    public ConfirmDialog(String string, boolean wide, String TRUE,
                         String FALSE, String NULL) {
        super(string, false, VISUALS.INFO_PANEL_WIDE, 3, TRUE, FALSE, NULL);
        this.TRUE = TRUE;
        this.FALSE = FALSE;
        this.NULL = NULL;
    }

    // protected VISUALS getVisuals() {
    // return
    // // (wide) ?
    // VISUALS.OPTION_PANEL_3
    // // : VISUALS.PANEL_HC
    // ;
    // }

    // @Override
    // public Component createComponent() {
    // panel = new G_Panel(getVisuals());
    // if (vertical)
    // panel.setLayout(new MigLayout("flowy"));
    // if (TRUE != null)
    // addButton(TRUE);
    // if (FALSE != null)
    // panel.add(getButton(false, FALSE), (wide) ? "wrap" : "");
    // if (NULL != null)
    // panel.add(getButton(null, NULL), (wide) ? "wrap" : "");
    //
    // return panel;
    // }

    // protected void addButton(String TRUE) {
    // boolean wrap = false;
    // if (n >= wrapN) {
    // n = 0;
    // wrap = true;
    // } else
    // n++;
    // panel.add(getButton(true, TRUE), (wrap) ? "wrap" : "");
    // }

    protected void handleClick(Object result) {
        if (result.toString().equals(TRUE)) {
            result = true;
        } else if (result.toString().equals(FALSE)) {
            result = false;
        } else if (result.toString().equals(NULL)) {
            result = null;
        }
        WaitMaster.receiveInput(CONFIRM_DIALOG, result);
        close();
    }

    protected CustomButton getButton(final Object result, String text) {
        return new CustomButton(text) {
            @Override
            public void handleClick() {
                playClickSound();
                ConfirmDialog.this.handleClick(result);
            }

        };
    }

}
