package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;

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
    protected TextureRegion blueBorderTexture;
    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> blueBorderOwners = new HashMap<>();

    public CellBorderManager() {
        greenBorder = new Image(TextureCache.getOrCreateR(cyanPath));
        greenBorder.setBounds(2, 2, 4, 4);

        redBorder = new Image(TextureCache.getOrCreateR(redPath));

        blueBorderTexture = TextureCache.getOrCreateR(bluePath);

        bindEvents();
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

    private void bindEvents() {

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
                    entry.getKey().setBorder(new Image(blueBorderTexture));
                });

                blueBorderOwners = map;
            }
        });
    }

    public boolean hitAndCall(Borderable borderable) {
        Runnable entity = blueBorderOwners.get(borderable);
        if (entity != null) {
            entity.run();
        }
        clearBlueBorder();

        if (singleBorderImageBackup != null) {
            showBorder(singleBorderImageBackup, unitBorderOwner);
            singleBorderImageBackup = null;
        }
        return (entity != null);
    }

    private void showBorder(Image border, Borderable owner) {
        owner.setBorder(border);

        if (unitBorderOwner != null && unitBorderOwner != owner) {
            unitBorderOwner.setBorder(null);
        }
        unitBorderOwner = owner;
    }

    public void updateBorderSize() {
        if (unitBorderOwner != null && unitBorderOwner.getBorder() != null) {
            unitBorderOwner.updateBorderSize();
        }
    }
}
