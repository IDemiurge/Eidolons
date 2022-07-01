package gdx.dto;

import java.util.List;

public class LaneDto implements DtoManager.Dto {

    List<UnitDto> units;

    public LaneDto(List<UnitDto> units) {
        this.units = units;
    }

    public List<UnitDto> getUnits() {
        return units;
    }
}
