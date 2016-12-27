package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.system.TempEventManager;

import java.util.HashMap;
import java.util.Map;

public class CellBorderManager extends Group {
    private int cellW;
    private int cellH;

    protected Image greenBorder;
    protected Image redBorder;
    protected Image orangeBorder;
    protected Texture blueBorderTexture;


    public boolean isBlueBorderActive() {
        return blueBorderOwners.size() > 0;
    }

    private static final String cyanPath = "UI\\Borders\\neo\\color flag\\cyan 132.png";
    private static final String bluePath = "UI\\Borders\\neo\\color flag\\blue 132.png";
    private static final String orangePath = "UI\\Borders\\neo\\color flag\\orange 132.png";
    private static final String purplePath = "UI\\Borders\\neo\\color flag\\purple 132.png";
    private static final String redPath = "UI\\Borders\\neo\\color flag\\red 132.png";

    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> blueBorderOwners = new HashMap<>();

    public Image singleBorderImageBackup = null;

    public CellBorderManager(int cellW, int cellH, TextureCache textureCache) {
        this.cellW = cellW;
        this.cellH = cellH;

        greenBorder = new Image(textureCache.getOrCreate(cyanPath));
        greenBorder.setBounds(2, 2, 4, 4);

        redBorder = new Image(textureCache.getOrCreate(redPath));

        blueBorderTexture = textureCache.getOrCreate(bluePath);

        initCallbacks();
    }

    private Texture getColoredBorderTexture(Color c) {
        Pixmap p = new Pixmap(cellW + 10, cellH + 10, Pixmap.Format.RGBA8888);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 3, 1, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p.drawRectangle(6, 6, p.getWidth() - 12, p.getHeight() - 12);
        p = BlurUtils.blur(p, 2, 2, true);
        p.setColor(c);
        p.drawRectangle(5, 5, p.getWidth() - 10, p.getHeight() - 10);
        p = BlurUtils.blur(p, 1, 1, true);
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }


    private void clearBlueBorder() {
        blueBorderOwners.entrySet().forEach(entity -> {
            entity.getKey().setBorder(null);
        });
        blueBorderOwners = new HashMap<>();
    }

    private void initCallbacks() {

        TempEventManager.bind("show-green-border", obj -> {
            if (obj != null) {
                Borderable b = (Borderable) obj.get();
                showBorder(greenBorder, b);
            }
        });

        TempEventManager.bind("show-red-border", obj -> {
            if (obj != null) {
                Borderable b = (Borderable) obj.get();
                showBorder(redBorder, b);
            }
        });

        TempEventManager.bind("show-blue-borders", obj -> {
            Map<Borderable, Runnable> map = (Map<Borderable, Runnable>) obj.get();
            clearBlueBorder();
            if (map != null) {
                map.entrySet().forEach(entry -> {
                    if (unitBorderOwner == entry.getKey()) {
                        singleBorderImageBackup = unitBorderOwner.getBorder();
                        unitBorderOwner.setBorder(null);// TODO: 12.12.2016 make better
                    }
                    Image i = new Image(blueBorderTexture);
                    entry.getKey().setBorder(new Image(blueBorderTexture));
                    i.setX(-6);
                    i.setY(-6);
                    i.setHeight(entry.getKey().getH() + 12);
                    i.setWidth(entry.getKey().getW() + 12);
                });

                blueBorderOwners = map;
            }
        });
    }

    public void hitAndCall(Borderable borderable) {
        blueBorderOwners.entrySet().forEach(entry -> {
            if (entry.getKey() == borderable) {
                entry.getValue().run();
            }
        });

        clearBlueBorder();

        if (singleBorderImageBackup != null) {
            showBorder(singleBorderImageBackup, unitBorderOwner);
            singleBorderImageBackup = null;
        }

    }

    private void showBorder(Image border, Borderable owner) {
        border.setWidth(owner.getW() + 12);
        border.setHeight(owner.getH() + 12);
        border.setX(-6);
        border.setY(-6);
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
