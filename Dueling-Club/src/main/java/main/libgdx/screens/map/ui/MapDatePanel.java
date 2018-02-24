package main.libgdx.screens.map.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.game.module.adventure.global.GameDate;
import main.game.module.adventure.global.TimeMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.gui.NinePatchFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 2/23/2018.
 */
public class MapDatePanel extends TablePanel {

    private static final float WIDTH = 320;
    private static final float HEIGHT = 60;
    private Label firstLabel;
    private Label secondLabel;
    private Label thirdLabel;
    private GameDate date;
    private boolean initialized;

    public MapDatePanel() {
        GuiEventManager.bind(MapEvent.DATE_CHANGED, p -> {
            updateRequired = true;
            this.date = (GameDate) p.get();
        });
        setSize(GdxMaster.adjustSize(WIDTH), GdxMaster.adjustSize(HEIGHT));
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        updateRequired = true;

        //tooltips?
    }

    private void init() {
        this.date = TimeMaster.getDate();
        LabelStyle style = StyleHolder.getSizedLabelStyle(FONT.AVQ, 18);
        firstLabel = new Label(getFirstLabelText(), style);
        secondLabel = new Label(getSecondLabelText(), style);
        thirdLabel = new Label(getThirdLabelText(), style);

        defaults().space(10).width(getWidth() / 3);

        add(firstLabel).center();
        row();
        add(secondLabel).center();
        add(thirdLabel).center();
        initialized = true;
    }

    private String getFirstLabelText() {
        return "The year " + date.getYear() + " of " +
         date.getEra();
    }

    private String getSecondLabelText() {
        return StringMaster.getOrdinal(date.getDay()) + " of " +
         date.getMonth().getName();
    }

    private String getThirdLabelText() {
        return DAY_TIME.HOUR_OF_WOLF.getText();
//        return (date.getDayTime().getText());
    }

    @Override
    public void updateAct(float delta) {
        if (!initialized)
            init();
        firstLabel.setText(getFirstLabelText());
        secondLabel.setText(getSecondLabelText());
        thirdLabel.setText(getThirdLabelText());
        pack();
    }
}
