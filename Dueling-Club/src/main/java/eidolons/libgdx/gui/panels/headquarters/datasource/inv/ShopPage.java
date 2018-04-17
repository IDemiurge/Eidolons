package eidolons.libgdx.gui.panels.headquarters.datasource.inv;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ShopPage extends Actor {
    private List<ShopValueContainerList> lists;

    public ShopPage(List<ShopValueContainerList> groupLists) {
        this.lists = groupLists;
    }
}
