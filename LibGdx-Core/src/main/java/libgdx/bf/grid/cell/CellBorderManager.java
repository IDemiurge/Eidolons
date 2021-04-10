package libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import libgdx.bf.Borderable;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.sound.AudioEnums;

import static main.system.GuiEventType.*;

public class CellBorderManager extends Group {
    public static final String teamcolorPath = Images.COLORLESS_BORDER;
    public static final String targetPath = Images.TARGET_BORDER;

    private static TextureRegion teamcolorTexture;
    private static TextureRegion targetTexture;
    public TextureRegion singleBorderImageBackup = null;
    private Borderable unitBorderOwner = null;
    private ObjectMap<Borderable, Runnable> teamColorBorderOwners = new ObjectMap<>();


    public CellBorderManager() {

        teamcolorTexture = TextureCache.getRegionUV(teamcolorPath);
        targetTexture = TextureCache.getRegionUV(targetPath);
        bindEvents();
    }

    // public boolean isteamColorBorderActive() {
    //     return teamColorBorderOwners.size > 0;
    // }

    private void clearTeamColorBorder(boolean restoreLastBorder) {
        teamColorBorderOwners.entries().forEach(entity -> {
            try {
                entity.key.setBorder(null);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        teamColorBorderOwners = new ObjectMap<>();
        if (restoreLastBorder) {
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
            ObjectMap<Borderable, Runnable> map = (ObjectMap<Borderable, Runnable>) obj.get();
            clearTeamColorBorder(false);
            if (map != null) {
                map.entries().forEach((ObjectMap.Entry<Borderable, Runnable> entry) -> {
                    setTargetBorder(entry.key);
                });

                teamColorBorderOwners = map;
            }
        });

        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            clearTeamColorBorder(true);
        });
        GuiEventManager.bind(TARGET_SELECTION, obj -> {
            final Borderable borderable = (Borderable) obj.get();
            if (borderable != null) {
                Runnable entity = teamColorBorderOwners.get(borderable);
                if (entity != null) {
                    entity.run();
                    EUtils.playSound(AudioEnums.STD_SOUNDS.NEW__CLICK_UP2);
                } else {
                    //TODO replace this quick-fix:
                    // click on non-blue-border cell must still do cell.invokeClicked() (run()) somehow

                    if (Eidolons.game.getManager().isSelecting()) {
                        Eidolons.game.getManager().selectingStopped(true);
                        EUtils.playSound(AudioEnums.STD_SOUNDS.NEW__CLICK_DISABLED);
                    }
                }


            } else {
                EUtils.playSound(AudioEnums.STD_SOUNDS.NEW__CLICK_UP2);
                Eidolons.game.getManager().selectingStopped(true);
            }
            clearTeamColorBorder(true);

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
