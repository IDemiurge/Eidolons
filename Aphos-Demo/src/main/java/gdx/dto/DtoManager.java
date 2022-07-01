package gdx.dto;

import logic.content.test.TestUnitContent;
import logic.lane.HeroPos;
import logic.lane.LanePos;

import java.util.Map;

public class DtoManager {
    /*
    could we do some yaml source construction?
     */
    public interface Dto {

    }
    public Dto getFromYaml(){
//mm, this would include creating Logical Entities!.. Unless we use IDs
        return null;
    }

    public enum TestScenario {
        cemetery(),
        ;

        String background;
        String focusArea;
        Map<Integer, String> gates;
        Map<LanePos, TestUnitContent.TestUnit> units;
        Map<HeroPos, TestUnitContent.TestHero> heroes;


    }
    static {

    }
}
