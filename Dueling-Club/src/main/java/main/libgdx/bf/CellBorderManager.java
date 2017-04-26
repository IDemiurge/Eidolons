package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.game.core.Eidolons;
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
    public TextureRegion singleBorderImageBackup = null;
    protected TextureRegion blueTexture;
    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> blueBorderOwners = new HashMap<>();
    private TextureRegion redTexture;
    private TextureRegion greenTexture;

    public CellBorderManager() {
        greenTexture = TextureCache.getOrCreateR(cyanPath);
        redTexture = TextureCache.getOrCreateR(redPath);
        blueTexture = TextureCache.getOrCreateR(bluePath);

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
                showBorder(greenTexture, b);
            }
        });

        GuiEventManager.bind(SHOW_RED_BORDER, obj -> {
            if (obj != null) {
                Borderable b = (Borderable) obj.get();
                showBorder(redTexture, b);
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
                    entry.getKey().setBorder(blueTexture);
                });

                blueBorderOwners = map;
            }
        });

        GuiEventManager.bind(CALL_BLUE_BORDER_ACTION, obj -> {
            final Borderable borderable = (Borderable) obj.get();
            if (borderable != null) {
                Runnable entity = blueBorderOwners.get(borderable);
                if (entity != null) {
                    entity.run();
                } else {
                    //TODO replace this quick-fix:
                    // click on non-blue-border cell must still do cell.invokeClicked() (run()) somehow

                    if (Eidolons.game.getManager().isSelecting())
                    Eidolons.game.getManager().selectingStopped(true);
                }
                clearBlueBorder();

                if (singleBorderImageBackup != null) {
                    showBorder(singleBorderImageBackup, unitBorderOwner);
                    singleBorderImageBackup = null;
                }
            }
        });
    }

    private void showBorder(TextureRegion border, Borderable owner) {
        owner.setBorder(border);

        if (unitBorderOwner != null && unitBorderOwner != owner) {
            unitBorderOwner.setBorder(null);
        }
        unitBorderOwner = owner;
    }
}
