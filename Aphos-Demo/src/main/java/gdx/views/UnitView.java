package gdx.views;

import gdx.controls.AUnitClicker;
import gdx.dto.UnitDto;

public class UnitView extends FieldView<UnitDto> {

    public UnitView(boolean side) {
        super(side);
    }

    @Override
    protected void update() {
        super.update();
        clearListeners();
        addListener(new AUnitClicker(dto.getUnit()));
    }
}
