package logic.core;

import gdx.dto.FrontFieldDto;
import gdx.dto.FrontLineDto;
import gdx.dto.LaneDto;
import gdx.dto.LaneFieldDto;
import logic.content.test.TestUnitContent;
import logic.entity.Entity;
import logic.lane.HeroPos;

import java.util.ArrayList;
import java.util.List;

import static logic.content.test.TestUnitContent.TestUnit.*;

public class LaunchData {

    public static final String HERO = TestUnitContent.TestHero.Valen.toString();

    public static final String[][] LANEDATA = {
            {Golem.name, Archer.name, "", "",}, //1st is frontline
            {"", Fiend.name, Golem.name, "",},
            {Rogue.name, "", "", Haunter.name,},

            {Fiend.name, "", Haunter.name, "",},
            {"", Rogue.name, "", "",},
            {Fiend.name, Archer.name, "", "",},
    };
    private static final String TXT = "";
    private static final String TXT2 = "";
    private static final String BG = "bg/shrine.png";

    public String hero;
    public String[][] frontData;
    public String[][] laneData;
    private final String txt;
    private final String txt2;

    public static LaunchData createDefaultData(){
        return new LaunchData(HERO, null, LANEDATA, TXT, TXT2);
    }

    public LaunchData(String hero, String[][] frontData, String[][] laneData, String txt, String txt2) {
        this.hero = hero;
        this.laneData = laneData;
        this.txt = txt;
        this.txt2 = txt2;
        this.frontData = frontData;
        if (frontData == null) {
            this.frontData = new String[][]{
                    {"", "", "", hero, "", "", "",},
                    {"", "", "", "", "", "", "",},
            };
        }
    }

    public LaneFieldDto initLfDto(Game game) {
        List<LaneDto> lanes = new ArrayList<>();
        for (int i = 0; i < laneData.length; i++) {
            lanes.add(new LaneDto(game.createUnitsOnLane(i, laneData)));
        }
        String img = BG;
        return new LaneFieldDto(img, lanes);
    }

    public FrontLineDto initHeroDto(Game game) {
        List<Entity> side=     new ArrayList<>() ;
        List<Entity> side2=     new ArrayList<>() ;
            int i=0;
        HeroPos pos=null;
        for (String s : frontData[0]) {
            if (!s.isEmpty()) {
                pos = new HeroPos(i, true);
                side.add(game.createHeroOrObject(s, pos));
            }
            i++;
        }
         i=0;
        for (String s : frontData[1]) {
            if (!s.isEmpty()) {
                pos = new HeroPos(i, false);
                side2.add(game.createHeroOrObject(s, pos));
            }
            i++;
        }
        return new FrontLineDto(side, side2);
    }

    public FrontFieldDto initFFDto(Game game) {
//        hero, fortifs, chain, vault data
        int n = frontData[0].length / 2;
        return new FrontFieldDto(n, txt, txt2);
    }

}
