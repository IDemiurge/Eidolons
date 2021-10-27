package libgdx.gui.dungeon.controls.radial;

import eidolons.entity.unit.Unit;

import java.util.List;

/**
 * Created by JustMe on 1/21/2017.
 */
public interface RADIAL_ITEM {

    List<RADIAL_ITEM> getItems(Unit heroObj);

    Object getContents();

    String getTexturePath();

}
