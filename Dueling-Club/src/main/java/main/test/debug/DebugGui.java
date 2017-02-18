package main.test.debug;

import main.game.core.game.DC_Game;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;

import java.awt.*;

public class DebugGui extends G_Panel {

    CustomButton toggleButton;
    CustomButton valueButton;
    CustomButton functionButton;
    private PagedDebugPanel pages;

    public DebugGui() {
        init();
        /*
		 * paged panel?
		 * custom controls? 
		 * function lists ? 
		 * split in two parts or made up of 2 panels? 
		 */
    }

    public void init() {
        pages = new PagedDebugPanel();
        valueButton = new CustomButton(VISUALS.HAMMER) {
            public void handleClick() {
                editValue();
            }
        };
        functionButton = new CustomButton(VISUALS.GEARS) {
            public void handleClick() {
                enterFunction();
            }
        };
        pages.refresh(); // initial index?
        add(pages, "pos 0 0");
        int x = DebugGuiPage.BUTTON_VISUALS.getWidth() * 2 + DebugGuiPage.gapX;
        int y = pages.getArrowHeight() + VISUALS.MENU_BUTTON.getHeight();
        add(valueButton, "id value,pos " + x + " " + y);
        add(functionButton, " pos " + x + " value.y2" // +
                // valueButton.getVisuals().getHeight()
        );

        setComponentZOrder(functionButton, 0);
        setComponentZOrder(valueButton, 1);
        setComponentZOrder(pages, 2);
        panelSize = new Dimension(732, 132);
    }

    protected void enterFunction() {

        DC_Game.game.getDebugMaster().promptFunctionToExecute();

    }

    protected void editValue() {
        DC_Game.game.getValueHelper().promptSetValue();
    }

    @Override
    public void refresh() {
        pages.refresh();
    }

}
