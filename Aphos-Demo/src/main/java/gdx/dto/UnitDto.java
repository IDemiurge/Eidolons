package gdx.dto;

import logic.entity.Unit;

import java.util.Objects;

public class UnitDto extends EntityDto<Unit> {
    public UnitDto(Unit entity) {
        super(entity);
    }
//    private String imagePath;

    public Unit getUnit() {
        return getEntity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitDto unitDto = (UnitDto) o;
        return entity.equals(unitDto.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }
}
