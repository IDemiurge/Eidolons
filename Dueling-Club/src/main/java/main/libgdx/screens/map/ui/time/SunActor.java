package main.libgdx.screens.map.ui.time;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.global.TimeMaster;
import main.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.GroupX;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/4/2018.
 */
public class SunActor extends GroupX {

    boolean undersun;
    ImageContainer underlay;
    ImageContainer overlay;
    ImageContainer overlay2;
    private float lastNextMapAlphaPercentage;

    public SunActor(boolean undersun) {
        this.undersun = undersun;
        addActor(underlay = new ImageContainer(getPath() + getNameRoot() + " underlay.png"));
        addActor(overlay = new ImageContainer(getDefaultOverlayPath()));
        addActor(overlay2 = new ImageContainer(getDefaultOverlayPath()));
        overlay.setAlphaTemplate(ALPHA_TEMPLATE.SUN);
        overlay2.setAlphaTemplate(ALPHA_TEMPLATE.SUN);
        if (undersun) {
            setX(-90);
            setY(0);
            setScale(0.6f);
            debug();
        }
updateOrigins();
        GuiEventManager.bind(MapEvent.TIME_CHANGED, p -> {
            update((DAY_TIME) p.get());
        });
    }

    private void updateOrigins() {
        overlay2.getContent().setOrigin(undersun ? Align.top : Align.center);
        underlay.getContent().setOrigin(undersun ? Align.top : Align.center);
        if (undersun) {
            setOriginY(getOriginY() - 40);
        }//offset
        overlay. getContent().setOrigin(undersun ? Align.top : Align.center);
        setOrigin(undersun ? Align.top : Align.center);
    }

    private void update(DAY_TIME time) {
        if (undersun) {
            if (!time.isUndersunVisible()) {
                setVisible(false);
                return;
            }
        } else {
            if (!time.isSunVisible()) {
                setVisible(false);
                return;
            } //preserve rotation
        }
        float r = overlay2.getRotation();
        overlay.setImage(getOverlayPath(time));
        boolean def = time.getNext().isNight() != undersun;
        if (!def) {
            overlay2.setImage(getOverlayPath(time.getNext()));
        } else {
            overlay2.setImage(getDefaultOverlayPath());
        }
        overlay.setRotation(r);
        overlay2.setRotation(r);
        updateOrigins();
        lastNextMapAlphaPercentage = 0;
    }

    private String getDefaultOverlayPath() {
        return getPath() + getNameRoot() + " default.png";
    }

    private String getOverlayPath(DAY_TIME time) {
        return getPath() + getNameRoot() + " " + time.name() + ".png";
    }

    @Override
    public void act(float delta) {
        super.act(delta);


        float dx = getRotationSpeedOverlay() * delta;
        if (undersun) {
            //rotate the whole thing!
            float r = getRotation(); //control sync with time!
            setRotation(r + dx);
        } else {
            float r = overlay.getContent().getRotation();
            overlay.getContent().setRotation(r + dx);
            overlay2.getContent().setRotation(r + dx);
        }

        Color color = overlay2.getContent().getColor();
        float percentage = 0.25f * (TimeMaster.getDate().getHour() % 4 +
         +MacroGame.getGame().getLoop().getTimeMaster().getMinuteCounter() / 60);
        if (percentage < lastNextMapAlphaPercentage) //no going back in time...
            return;
        lastNextMapAlphaPercentage = percentage;
        color.a = color.a * percentage;
    }

    private float getRotationSpeedOverlay() {
        if (undersun)
            return 0.5f;
        return 1;
    }

    private String getNameRoot() {
        return (undersun ? "undersun" : "sun");
    }

    private String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroUiPath()
         , "component", "time panel", "suns") + StringMaster.getPathSeparator()
         + getNameRoot() + StringMaster.getPathSeparator();

    }
}
