package gdx.dto;

import logic.entity.Entity;

import java.util.List;

public class FrontLineDto implements DtoManager.Dto {
    List<Entity> side;
    List<Entity> side2;

    public FrontLineDto(List<Entity> side, List<Entity> side2) {
        this.side = side;
        this.side2 = side2;
    }
    //separate for fortifications!

    public List<Entity> getSide() {
        return side;
    }

    public List<Entity> getSide2() {
        return side2;
    }
}
