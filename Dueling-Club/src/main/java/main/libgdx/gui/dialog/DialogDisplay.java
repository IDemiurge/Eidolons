package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.entity.obj.DC_HeroObj;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.system.GuiEventManager;
import main.system.graphics.MigMaster;

import static main.system.GuiEventType.DIALOG_CLOSED;
import static main.system.GuiEventType.SHOW_INFO_DIALOG;

/**
 * Created by JustMe on 1/8/2017.
 */
public class DialogDisplay extends Group {

    Dialog dialog;

    public void init() {
        GuiEventManager.bind(SHOW_INFO_DIALOG, obj -> {
            DC_HeroObj unit = (DC_HeroObj) obj.get();
            clearChildren();
            dialog = new InfoDialog(unit);
            addActor(dialog);
            setPosition(MigMaster.getCenteredWidth((int) dialog.getWidth()),
                    MigMaster.getCenteredHeight((int) dialog.getHeight()));
//            GameScreen.getInstance().
            setVisible(true);
            LightingManager.darkening = 0.5f;
        });

        GuiEventManager.bind(DIALOG_CLOSED, obj -> {
            closedDialog();
        });
    }

    public void closedDialog() {
        dialog = null;
        setVisible(false);
    }

    public Dialog getDialog() {
        return dialog;
    }
}
