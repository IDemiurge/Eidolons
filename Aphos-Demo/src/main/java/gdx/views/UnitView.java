package gdx.views;

import gdx.controls.AUnitClicker;
import gdx.dto.UnitDto;
import libgdx.gui.generic.GroupX;

public class UnitView extends GroupX {

    private final UnitDto dto;

    public UnitView(UnitDto dto) {
        this.dto = dto;
        addListener(new AUnitClicker(dto.getUnit()));
    }
}
