package gdx.dto;

import logic.Unit;

public class UnitDto {
    String imagePath;
    private final Unit unit;

    public UnitDto(Unit unit) {
        this.unit = unit;
        imagePath = unit.toString()
    }

    public Unit getUnit() {
        return unit;
    }

}
