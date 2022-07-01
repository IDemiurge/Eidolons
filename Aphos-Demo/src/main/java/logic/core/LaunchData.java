package logic.core;

import gdx.dto.FrontFieldDto;
import gdx.dto.FrontLineDto;
import gdx.dto.LaneDto;
import gdx.dto.LaneFieldDto;
import logic.content.test.TestUnitContent;
import logic.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class LaunchData {

    public static final String HERO = TestUnitContent.TestHero.Elberen.toString();

    public static final String[][] LANEDATA = {
            {"", "", "", "",},
            {"", "", "", "",},
            {"", "", "", "",},

            {"", "", "", "",},
            {"", "", "", "",},
            {"", "", "", "",},
    };
    private static final String TXT = "";
    private static final String TXT2 = "";

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
        String img = "gate";
        return new LaneFieldDto(img, lanes);
    }

    public FrontLineDto initHeroDto(Game game) {
        List<Entity> side=     new ArrayList<>() ;
        List<Entity> side2=     new ArrayList<>() ;
        for (String s : frontData[0]) {
            if (!s.isEmpty()) {
                side.add(game.createHeroOrObject(s));
            }
        }
        for (String s : frontData[0]) {
            if (!s.isEmpty()) {
                side2.add(game.createHeroOrObject(s));
            }
        }
        return new FrontLineDto(side, side2);
    }

    public FrontFieldDto initFFDto(Game game) {
//        hero, fortifs, chain, vault data
        int n = frontData[0].length / 2;
        return new FrontFieldDto(n, txt, txt2);
    }

}
