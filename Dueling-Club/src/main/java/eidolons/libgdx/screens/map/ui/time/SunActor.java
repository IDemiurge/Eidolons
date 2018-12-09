package eidolons.libgdx.screens.map.ui.time;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.macro.MacroGame;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.PathUtils;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 3/4/2018.
 */
public class SunActor extends GroupX {

    boolean undersun;
    ImageContainer underlay;
    ImageContainer overlay;
    ImageContainer overlay2;
    private float lastNextMapAlphaPercentage;
    private DAY_TIME time;

    public SunActor(boolean undersun) {
        this.undersun = undersun;
        addActor(underlay = new ImageContainer(getPath() + getNameRoot() + " underlay.png"));
        addActor(overlay = new ImageContainer(getDefaultOverlayPath()));
        addActor(overlay2 = new ImageContainer(getDefaultOverlayPath()));
        overlay.setAlphaTemplate(Fluctuating.ALPHA_TEMPLATE.SUN);
        overlay2.setAlphaTemplate(Fluctuating.ALPHA_TEMPLATE.SUN);
        if (undersun) {
            setX(-90);
            setY(0);
            setScale(0.6f);
            setRotation(270);
//            debug();
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
        overlay.getContent().setOrigin(undersun ? Align.top : Align.center);
        setOrigin(undersun ? Align.top : Align.center);
    }

    private void update(DAY_TIME time) {
        this.time = time;
        if (undersun) {
            if (!time.isUndersunVisible()) {
                setVisible(false);
                return;
            }
        } else {
            if (!time.isSunVisible()) {
                setVisible(false);
                return;
            }
        }
        float r = overlay2.getRotation();//preserve rotation
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

        Color color = overlay2.getContent().getColor();
        float percentage =
         MacroGame.getGame().getLoop().getTimeMaster().getTimer() / 240;
//         0.25f * (TimeMaster.getDate().getHour() % 4) +
//         +MacroGame.getGame().getLoop().getTimeMaster().getMinuteCounter() / 240;

        if (percentage < lastNextMapAlphaPercentage) //no going back in time...
        {
            percentage = lastNextMapAlphaPercentage;
//            lastNextMapAlphaPercentage=0;
        }
//        if (percentage == lastNextMapAlphaPercentage) {
//            percentage+=delta;
//        }
        if (percentage > 1) //no going back in time...
        {
            return;
        }
        lastNextMapAlphaPercentage = percentage;
        color.a = color.a * percentage;

        float rotation = getRotation(percentage);
        if (undersun) {
            //rotate the whole thing!
            setRotation(rotation);
//            ActorMaster.addRotateByAction(this, );
        } else {
            overlay.getContent().
             setRotation(rotation);
            overlay2.getContent().
             setRotation(rotation);
        }
    }

    private float getRotation(float percentageIntoNextDayTime) {
        if (undersun)
            switch (time) {
                case MIDNIGHT:
                    return getDefaultUndersunRotation()
                     - Math.max(0, percentageIntoNextDayTime - 0.75f) * 90;
                case NIGHTFALL:
                    return getDefaultUndersunRotation();
                case DAWN:
                    return getDefaultUndersunRotation()
                     - (percentageIntoNextDayTime + 0.25f) * 90;
                case DUSK:
                    return getDefaultUndersunRotation() + 140
                     - percentageIntoNextDayTime * 140;
                case MORNING:
                case MIDDAY:
                    return 0;
            }
        return 360 * percentageIntoNextDayTime;
    }

    private float getDefaultUndersunRotation() {
        return 310;
    }

    //360 degrees in 24*60 seconds
    private float getRotationSpeedOverlay() {
        if (undersun)
            switch (time) {
                case MIDNIGHT:
                case NIGHTFALL:
                    return 0.0f;
                case DAWN:
                case MIDDAY:
                    return 0.5f;
                case MORNING:
                case DUSK:
                    return 0.25f;
            }
        return 1;
    }

    private String getNameRoot() {
        return (undersun ? "undersun" : "sun");
    }

    private String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroUiPath()
         , "components", "time panel", "suns") + PathUtils.getPathSeparator()
         + getNameRoot() + PathUtils.getPathSeparator();

    }
}
