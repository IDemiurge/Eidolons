package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.entity.obj.DC_HeroObj;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.system.GuiEventManager;
import main.system.graphics.MigMaster;

import static main.system.GraphicEvent.DIALOG_CLOSED;
import static main.system.GraphicEvent.SHOW_INFO_DIALOG;

/**
 * Created by JustMe on 1/8/2017.
 */
public class DialogDisplay extends Group {

    private static final int HEIGHT_OFFSET = 65;
    private static   DialogDisplay instance;
    Dialog dialog;

    public DialogDisplay() {
        GuiEventManager.bind(SHOW_INFO_DIALOG, obj -> {
            DC_HeroObj unit = (DC_HeroObj) obj.get();
            clearChildren();
            try {
                dialog = new InfoDialog(unit);
            } catch (Exception e) {
                e.printStackTrace();
                closedDialog();
                return;
            }
            dialog.update();
            addActor(dialog);

            int w = Math.max((int) dialog.getWidth(), (int) GameScreen.getInstance().getBackground().getWidth());
            int h = Math.max((int) dialog.getHeight(), (int) GameScreen.getInstance().
             getBackground().getHeight());
            setPosition(MigMaster.getCenteredPosition(w, (int) dialog.getWidth()),
             HEIGHT_OFFSET+
             MigMaster.getCenteredPosition(h, (int) dialog.getHeight()));
//            GameScreen.getInstance().
            setVisible(true);
            LightingManager.darkening = 0.5f;
        });

        GuiEventManager.bind(DIALOG_CLOSED, obj -> {
            closedDialog();
        });
        instance=this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void closedDialog() {
        dialog = null;
        setVisible(false);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public static boolean isDisplaying() {
        return instance.getDialog()!=null ;
    }

    public static DialogDisplay getInstance() {
        return instance;
    }
}
