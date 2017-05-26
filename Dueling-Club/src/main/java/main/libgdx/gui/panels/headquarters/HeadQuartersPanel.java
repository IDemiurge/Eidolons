package main.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.libgdx.gui.SimpleClickListener;
import main.libgdx.gui.panels.dc.TablePanel;

import static main.libgdx.StyleHolder.getMainMenuButton;

public class HeadQuartersPanel extends TablePanel {
    private final TextButton tavernButton;
    private final TextButton shopButton;
    private final TextButton mapButton;
    private final TextButton createHero;
    private final TextButton menuButton;

    public HeadQuartersPanel() {
        left().bottom();

        createHero = getMainMenuButton("createHero");
        add(createHero);
        row();

        tavernButton = getMainMenuButton("tavernButton");
        add(tavernButton);
        row();
        shopButton = getMainMenuButton("shopButton");
        add(shopButton);
        row();
        mapButton = getMainMenuButton("mapButton");
        add(mapButton);
        row();

        menuButton = getMainMenuButton("menuButton");
        add(menuButton);
        row();
    }

    public HeadQuartersPanel setCreateHeroButtonCallback(Runnable callback) {
        if (createHero != null) {
            createHero.addListener(new SimpleClickListener(callback));
        }
        return this;
    }

    public HeadQuartersPanel setTavernButtonCallback(Runnable callback) {
        if (tavernButton != null) {
            tavernButton.addListener(new SimpleClickListener(callback));
        }
        return this;
    }

    public HeadQuartersPanel setShopButtonCallback(Runnable callback) {
        if (shopButton != null) {
            shopButton.addListener(new SimpleClickListener(callback));
        }
        return this;
    }

    public HeadQuartersPanel setMapButtonCallback(Runnable callback) {
        if (mapButton != null) {
            mapButton.addListener(new SimpleClickListener(callback));
        }
        return this;
    }

    public HeadQuartersPanel setMenuButtonCallback(Runnable callback) {
        if (menuButton != null) {
            menuButton.addListener(new SimpleClickListener(callback));
        }
        return this;
    }

    @Override
    public void updateAct(float delta) {
        //here set images and other shit
    }
}
