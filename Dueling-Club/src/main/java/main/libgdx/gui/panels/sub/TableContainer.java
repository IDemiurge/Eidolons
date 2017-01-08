package main.libgdx.gui.panels.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by JustMe on 1/7/2017.
 */
public class TableContainer extends Group {
    public TableContainer() {

    }

    public TableContainer(int rows, int columns) {


        Table table = new Table();

//        table.add(nameLabel);
//        table.add(nameText).width(100);
//        table.row();
//        table.add(addressLabel);
//        table.add(addressText).width(100);
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }
}
