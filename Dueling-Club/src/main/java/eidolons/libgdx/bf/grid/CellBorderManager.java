package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static main.system.GuiEventType.*;

public class CellBorderManager extends Group {
    public static final String teamcolorPath = Images.COLORLESS_BORDER;
    public static final String targetPath = Images.TARGET_BORDER;

    private static TextureRegion teamcolorTexture;
    private static   TextureRegion targetTexture;
    public TextureRegion singleBorderImageBackup = null;
    private Borderable unitBorderOwner = null;
    private Map<Borderable, Runnable> teamColorBorderOwners = new HashMap<>();


    public CellBorderManager() {

        teamcolorTexture = TextureCache.getOrCreateR(teamcolorPath);
        targetTexture = TextureCache.getOrCreateR(targetPath);
        bindEvents();
    }

    public boolean isteamColorBorderActive() {
        return teamColorBorderOwners.size() > 0;
    }

    private void clearTeamColorBorder(boolean restoreLastBorder) {
        teamColorBorderOwners.entrySet().forEach(entity -> {
            entity.getKey().setBorder(null);
        });
        teamColorBorderOwners = new HashMap<>();
        if (restoreLastBorder){
            if (singleBorderImageBackup != null) {
                showBorder(singleBorderImageBackup, unitBorderOwner);
                singleBorderImageBackup = null;
            }
        }
    }

    public static TextureRegion getTeamcolorTexture() {
        return teamcolorTexture;
    }

    public static TextureRegion getTargetTexture() {
        return targetTexture;
    }

    private void bindEvents() {


        GuiEventManager.bind(SHOW_TEAM_COLOR_BORDER, obj -> {
            showBorder(teamcolorTexture, (Borderable) obj.get());
        });

        GuiEventManager.bind(SHOW_TARGET_BORDERS, obj -> {
            Map<Borderable, Runnable> map = (Map<Borderable, Runnable>) obj.get();
            clearTeamColorBorder(false);
            if (map != null) {
                map.entrySet().forEach((Entry<Borderable, Runnable> entry) -> {
                    setTargetBorder(entry.getKey());

                });

                teamColorBorderOwners = map;
            }
        });

        GuiEventManager.bind(TARGET_SELECTION, obj -> {
            final Borderable borderable = (Borderable) obj.get();
            if (borderable != null) {
                Runnable entity = teamColorBorderOwners.get(borderable);
                if (entity != null) {
                    entity.run();
                    EUtils.playSound(STD_SOUNDS.NEW__CLICK_UP2);
                } else {
                    //TODO replace this quick-fix:
                    // click on non-blue-border cell must still do cell.invokeClicked() (run()) somehow

                    if (Eidolons.game.getManager().isSelecting())
                    {
                        Eidolons.game.getManager().selectingStopped(true);
                        EUtils.playSound(STD_SOUNDS.NEW__CLICK_DISABLED);
                    }
                }
                clearTeamColorBorder(true);


            } else {
                EUtils.playSound(STD_SOUNDS.NEW__CLICK_UP2);
                Eidolons.game.getManager().selectingStopped(true);
                clearTeamColorBorder(true);
            }

        });
    }

    private void setTargetBorder(Borderable entry) {
        if (unitBorderOwner == entry) {
            singleBorderImageBackup = unitBorderOwner.getBorder();
            unitBorderOwner.setBorder(null);// TODO: 12.12.2016 make better
            entry.setTeamColorBorder(false);
        }
        if (entry == null) {
            return;
        }
        entry.setBorder(targetTexture);
        entry.setTeamColorBorder(true);
    }

    private void showBorder(TextureRegion border, Borderable owner) {
        owner.setBorder(border);

        if (unitBorderOwner != null && unitBorderOwner != owner) {
            unitBorderOwner.setBorder(null);
//            unitBorderOwner.setTeamColorBorder(false);
        }
        owner.setTeamColorBorder(true);

        unitBorderOwner = owner;
    }
}
