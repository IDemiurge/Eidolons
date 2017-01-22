package main.libgdx.bf.controls.radial;

import main.entity.obj.DC_HeroObj;

import java.util.List;

/**
 * Created by JustMe on 1/21/2017.
 */
public interface RADIAL_ITEM {

    List<RADIAL_ITEM> getItems(DC_HeroObj heroObj);

    Object getContents();

    String getTexturePath();

}
