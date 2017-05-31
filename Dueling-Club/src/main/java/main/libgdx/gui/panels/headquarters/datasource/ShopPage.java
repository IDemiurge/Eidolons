package main.libgdx.gui.panels.headquarters.datasource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.gui.panels.headquarters.ShopListPanel;

import java.util.List;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ShopPage extends Actor {
    private   List<ShopListPanel> lists;

    public ShopPage(List<ShopListPanel> groupLists) {
        this.lists = groupLists;
    }
}
