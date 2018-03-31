package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static main.system.GuiEventType.*;

public class CellBorderManager extends Group {
    public static final String cyanPath = "UI\\Borders\\neo\\color flag\\cyan 132.png";
    public static final String bluePath = "UI\\Borders\\neo\\color flag\\blue 132.png";
    public static final String teamcolorPath = "UI\\Borders\\neo\\color flag\\white 132.png";
    public static final String redPath = "UI\\Borders\\neo\\color flag\\red 132.png";
    private final TextureRegion teamcolorTexture;
    public TextureRegion singleBorderImageBackup = null;
    protected TextureRegion blueTexture;
    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> teamColorBorderOwners = new HashMap<>();
    private TextureRegion redTexture;
    private TextureRegion greenTexture;

    public CellBorderManager() {
        greenTexture = TextureCache.getOrCreateR(cyanPath);
        redTexture = TextureCache.getOrCreateR(redPath);
        blueTexture = TextureCache.getOrCreateR(bluePath);
        teamcolorTexture = TextureCache.getOrCreateR(teamcolorPath);
        bindEvents();
    }

    public boolean isteamColorBorderActive() {
        return teamColorBorderOwners.size() > 0;
    }

    private void clearteamColorBorder() {
        teamColorBorderOwners.entrySet().forEach(entity -> {
            entity.getKey().setBorder(null);
        });
        teamColorBorderOwners = new HashMap<>();
    }

    private void bindEvents() {

//        GuiEventManager.bind(SHOW_GREEN_BORDER, obj -> {
//            if (obj != null) {
//                Borderable b = (Borderable) obj.get();
//                showBorder(greenTexture, b);
//            }
//        });
//
//        GuiEventManager.bind(SHOW_RED_BORDER, obj -> {
//            if (obj != null) {
//                Borderable b = (Borderable) obj.get();
//                showBorder(redTexture, b);
//            }
//        });
        GuiEventManager.bind(SHOW_TEAM_COLOR_BORDER, obj -> {
            showBorder(teamcolorTexture, (Borderable) obj.get());
        });

        GuiEventManager.bind(SHOW_BLUE_BORDERS, obj -> {
            Map<Borderable, Runnable> map = (Map<Borderable, Runnable>) obj.get();
            clearteamColorBorder();
            if (map != null) {
                map.entrySet().forEach((Entry<Borderable, Runnable> entry) -> {
                    setTeamColorBorder(entry.getKey());

                });

                teamColorBorderOwners = map;
            }
        });

        GuiEventManager.bind(CALL_BLUE_BORDER_ACTION, obj -> {
            final Borderable borderable = (Borderable) obj.get();
            if (borderable != null) {
                Runnable entity = teamColorBorderOwners.get(borderable);
                if (entity != null) {
                    entity.run();
                } else {
                    //TODO replace this quick-fix:
                    // click on non-blue-border cell must still do cell.invokeClicked() (run()) somehow

                    if (Eidolons.game.getManager().isSelecting())
                        Eidolons.game.getManager().selectingStopped(true);
                }
                clearteamColorBorder();

                if (singleBorderImageBackup != null) {
                    showBorder(singleBorderImageBackup, unitBorderOwner);
                    singleBorderImageBackup = null;
                }
            }
        });
    }

    private void setTeamColorBorder(Borderable entry) {
        if (unitBorderOwner == entry) {
            singleBorderImageBackup = unitBorderOwner.getBorder();
            unitBorderOwner.setBorder(null);// TODO: 12.12.2016 make better
            entry.setTeamColorBorder(false);
        }
        if (entry == null) {
            return;
        }
        entry.setBorder(teamcolorTexture);
        entry.setTeamColorBorder(true);
    }

    private void showBorder(TextureRegion border, Borderable owner) {
        owner.setBorder(border);

        if (unitBorderOwner != null && unitBorderOwner != owner) {
            unitBorderOwner.setBorder(null);
            unitBorderOwner.setTeamColorBorder(false);
        }
        owner.setTeamColorBorder(true);

        unitBorderOwner = owner;
    }
}
