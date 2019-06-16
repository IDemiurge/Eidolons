package eidolons.libgdx.screens.map.town;

import eidolons.libgdx.screens.ScreenWithLoaderAndUI;

/**
 * Created by JustMe on 3/14/2018.
 * <p>
 * what other ui does it need?
 * <p>
 * navigation icons
 * <p>
 * party info
 * <p>
 * town info
 */
public class TownScreen extends ScreenWithLoaderAndUI {

    TownDataSource dataSource;

    @Override
    protected void afterLoad() {

    }

    @Override
    protected boolean isWaitForInput() {
        return false;
    }


}
