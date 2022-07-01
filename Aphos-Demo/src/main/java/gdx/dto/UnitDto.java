package gdx.dto;

import logic.entity.Unit;

import java.util.Objects;

public class UnitDto implements DtoManager.Dto {
//    private String imagePath;
    private final Unit unit; //what use is the dto pattern if it only wraps unit?

    public UnitDto(Unit unit) {
        this.unit = unit;
//        imagePath = unit.getValueMap().get(AUnitEnums.UnitVal.image.toString()).toString();
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitDto unitDto = (UnitDto) o;
        return unit.equals(unitDto.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit);
    }
}
