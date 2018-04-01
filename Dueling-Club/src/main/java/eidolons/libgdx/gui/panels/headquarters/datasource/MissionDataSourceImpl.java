package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.mission.Mission;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 6/2/2017.
 */
public class MissionDataSourceImpl implements MissionDataSource {

    Mission mission;

    public MissionDataSourceImpl(Mission mission) {
        this.mission = mission;
    }

    @Override
    public ValueContainer getPosition() {
        Coordinates c = new Coordinates(true,
         mission.getPlace().getProperty(PROPS.PLACE_COORDINATES));
        return new ValueContainer("" + c.x, "" + c.y);
    }

    @Override
    public ValueContainer getName() {

        return new ValueContainer(mission.getName(), "");
    }

    @Override
    public ValueContainer getTooltip() {
        return new ValueContainer(mission.getToolTip(), "");
    }

    @Override
    public ValueContainer getMapIcon() {
        return new ValueContainer(TextureCache.getOrCreateR(mission.getPlace().getImagePath()));
    }

    @Override
    public ValueContainer getTooltipIcon() {
        return new ValueContainer(TextureCache.getOrCreateR(mission.getImagePath()));

    }
}
