package logic.hero;

import logic.Unit;
import logic.lane.HeroPos;

import java.util.Map;

public class Hero extends Unit {

    private final HeroPos pos;

    public Hero(HeroPos pos, Map<String, Object> valueMap) {
        super(valueMap);
        this.pos = pos;
    }
}
