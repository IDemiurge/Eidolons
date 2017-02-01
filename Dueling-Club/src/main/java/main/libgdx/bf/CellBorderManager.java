package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static main.system.GuiEventType.*;

public class CellBorderManager extends Group {
    private static final String cyanPath = "UI\\Borders\\neo\\color flag\\cyan 132.png";
    private static final String bluePath = "UI\\Borders\\neo\\color flag\\blue 132.png";
    private static final String orangePath = "UI\\Borders\\neo\\color flag\\orange 132.png";
    private static final String purplePath = "UI\\Borders\\neo\\color flag\\purple 132.png";
    private static final String redPath = "UI\\Borders\\neo\\color flag\\red 132.png";
    public Image singleBorderImageBackup = null;
    protected Image greenBorder;
    protected Image redBorder;
    protected Image orangeBorder;
    protected Texture blueBorderTexture;
    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> blueBorderOwners = new HashMap<>();

    public CellBorderManager() {
        greenBorder = new Image(TextureManager.getOrCreate(cyanPath));
        greenBorder.setBounds(2, 2, 4, 4);

        redBorder = new Image(TextureManager.getOrCreate(redPath));

        blueBorderTexture = TextureManager.getOrCreate(bluePath);

        initCallbacks();
    }

    public boolean isBlueBorderActive() {
        return blueBorderOwners.size() > 0;
    }

    private void clearBlueBorder() {
        blueBorderOwners.entrySet().forEach(entity -> {
            entity.getKey().setBorder(null);
        });
        blueBorderOwners = new HashMap<>();
    }

    private void initCallbacks() {

        GuiEventManager.bind(SHOW_GREEN_BORDER, obj -> {
            if (obj != null) {
                Borderable b = (Borderable) obj.get();
                showBorder(greenBorder, b);
            }
        });

        GuiEventManager.bind(SHOW_RED_BORDER, obj -> {
            if (obj != null) {
                Borderable b = (Borderable) obj.get();
                showBorder(redBorder, b);
            }
        });

        GuiEventManager.bind(SHOW_BLUE_BORDERS, obj -> {
            Map<Borderable, Runnable> map = (Map<Borderable, Runnable>) obj.get();
            clearBlueBorder();
            if (map != null) {
                map.entrySet().forEach((Entry<Borderable, Runnable> entry) -> {
                    if (unitBorderOwner == entry.getKey()) {
                        singleBorderImageBackup = unitBorderOwner.getBorder();
                        unitBorderOwner.setBorder(null);// TODO: 12.12.2016 make better
                    }
                    if (entry.getKey() == null) {
                        return;
                    }
                    Image i = new Image(blueBorderTexture);
                    entry.getKey().setBorder(new Image(blueBorderTexture));
                    i.setX(-4);
                    i.setY(-4);
                    i.setHeight(entry.getKey().getH() + 8);
                    i.setWidth(entry.getKey().getW() + 8);
                });

                blueBorderOwners = map;
            }
        });
    }

    public void hitAndCall(Borderable borderable) {
        blueBorderOwners.entrySet().forEach(entry -> {
            if (entry.getKey() == borderable) {
                entry.getValue().run();
                int id = 0; //TODO get that id!
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, id
                );
            }
        });

        clearBlueBorder();

        if (singleBorderImageBackup != null) {
            showBorder(singleBorderImageBackup, unitBorderOwner);
            singleBorderImageBackup = null;
        }

    }

    private void showBorder(Image border, Borderable owner) {
        border.setWidth(owner.getW() + 8);
        border.setHeight(owner.getH() + 8);
        border.setX(-4);
        border.setY(-4);
        owner.setBorder(border);

        if (unitBorderOwner != null && unitBorderOwner != owner) {
            unitBorderOwner.setBorder(null);
        }
        unitBorderOwner = owner;
    }

    public void updateBorderSize() {
        if (unitBorderOwner != null && unitBorderOwner.getBorder() != null) {
            unitBorderOwner.getBorder().setWidth(unitBorderOwner.getW() + 12);
            unitBorderOwner.getBorder().setHeight(unitBorderOwner.getH() + 12);
        }
    }
}
