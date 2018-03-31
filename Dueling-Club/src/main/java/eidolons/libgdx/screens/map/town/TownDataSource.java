package eidolons.libgdx.screens.map.town;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.module.adventure.town.Town;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/14/2018.
 */
public class TownDataSource {

    Town town;

    public TownDataSource(Town town) {
        this.town = town;
        init();
    }

    private void init() {
        String points = FileManager.readFile(getDataFilePath());
        for (String substring : StringMaster.openContainer(points)) {

        }
    }

    private String getDataFilePath() {
        return null;
    }

    public Image getTownBackground() {

        return null;
    }

    public List<TownPlaceActor> getTownPlaces() {
        List<TownPlaceActor> list = new ArrayList<>();
        return list;
    }

    public enum TOWN_TYPE {
        VILLAGE,
        SWAMP_VILLAGE,
        FOREST_VILLAGE,
        TOWN,

        RIVEREND,
        EVERMYST,
        GREENTORCH,
        TWILIGHT_MONASTERY,

        WITCHFORT,
        GREYROCK,
        ORODRIS,
        STONESHIELD_HALLS,
        IRONHELM_HALLS,

        BLACKLAKE,
        ELMGRAVE,


    }
}
