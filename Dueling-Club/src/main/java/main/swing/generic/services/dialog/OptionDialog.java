package main.swing.generic.services.dialog;

import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.util.Arrays;

public class OptionDialog extends G_Dialog {
    private String title;
    private boolean vertical;
    private int wrapN;
    private int n = 0;
    private Object[] options;
    private G_Panel panel;
    private boolean integerCode;
    private VISUALS V;

    public OptionDialog(String string, boolean vertical, boolean integerCode, VISUALS v, int wrapN,
                        Object... options) {
        super();
        this.title = string;
        this.wrapN = wrapN;
        this.options = options;
        this.integerCode = integerCode;
        V = v;
        init();
    }

    public OptionDialog(String string, boolean vertical, VISUALS v, int wrapN, Object... options) {
        this(string, vertical, false, v, wrapN, options);
    }

    protected VISUALS getVisuals() {
        // VISUALS V = null;
        // int max = (vertical) ? options.length / wrapN : wrapN;
        // getButtonWidth();
        // visuals.panel_frame_x_y;
        // new EnumMaster<ENUM>().retrieveEnumConst(ENUM.class, string )
        return V;
    }

    @Override
    public Component createComponent() {
        panel = new G_Panel(getVisuals());
        if (vertical)
            panel.setLayout(new MigLayout("fillx, flowy"));
        else
            panel.setLayout(new MigLayout("fillx"));
        // panel.add(new TextComp(null, title), "");
        for (Object o : options)
            addButton(o);
        WrappedTextComp comp = new WrappedTextComp(null, true) {
            protected int getDefaultFontSize() {
                return 16;
            }

            public void wrapTextLines() {
                super.wrapTextLines();
            }

            protected Dimension initDefaultSize() {
                return getPanelSize();
            }

            public Dimension getPanelSize() {
                return new Dimension(OptionDialog.this.getVisuals().getWidth() - 42,
                        OptionDialog.this.getVisuals().getHeight() - VISUALS.BUTTON.getHeight()
                                - 32);
            }
        };
        comp.setPanelSize(new Dimension(getVisuals().getWidth() - 42, getVisuals().getHeight()
                - VISUALS.BUTTON.getHeight() - 32));
        comp.setText(title);
        comp.refresh();
        panel.add(comp, "@pos center_x 75"
                // + "button1.y2"
        );
        return panel;
    }

    protected void addButton(Object o) {
        boolean wrap = false;
        if (n >= wrapN) {
            n = 0;
            wrap = true;
        } else
            n++;
        String constraints =
                // "grow" +
                ((wrap) ? ", wrap" : ""); // , id button" + n

        // int i=n;
        // // if (n == 0)
        // // constraints = constraints + ", x 20";
        // if (n % 2 == 0)
        // i = -n / 2;
        // constraints = "@pos center_x-" +
        // (CustomButton.BUTTON_VISUALS.getWidth()
        // *i);
        panel.add(getButton(o, getTextForOption(o)), constraints);

    }

    private String getTextForOption(Object o) {
        return o.toString();
    }

    protected void handleClick(Object result) {
        WaitMaster.receiveInput(WAIT_OPERATIONS.OPTION_DIALOG, isIntegerCode() ? Arrays.asList(
                options).indexOf(result) : result);
        close();
    }

    protected boolean isIntegerCode() {
        return integerCode;
    }

    protected Component getButton(final Object result, String text) {
        return new CustomButton(text) {
            @Override
            public void handleClick() {
                playClickSound();
                OptionDialog.this.handleClick(result);
            }

        };
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    @Override
    public Dimension getSize() {
        return panel.getPanelSize();
    }

    @Override
    public String getTitle() {
        return title;
    }
}
