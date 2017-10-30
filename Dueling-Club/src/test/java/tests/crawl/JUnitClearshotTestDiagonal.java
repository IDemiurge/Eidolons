package tests.crawl;

import main.game.bf.Coordinates;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 10/30/2017.
 */
public class JUnitClearshotTestDiagonal extends  JUnitClearshotTest {
    @Override
    public String getDungeonPath() {
        return "test\\clearshot test diagonal.xml";
    }

    @Override
    protected List<Coordinates> createCoordinatesList( ) {
        List<Coordinates> list = new LinkedList<>();
        int yGap = game.getBF_Height()/2-(getInnerHeight()-1)/2;
//        int xGap = game.getBF_Width()/2-(getInnerWidth()-1)/2;
        int i = 0;
        for (int y =  yGap;
             y < game.getBF_Height() -y ; y++) {
            i++;
            for (int x = game.getBF_Width()/2-i;
                 x< game.getBF_Width()/2+i; x++) {
                list.add(new Coordinates(x, y));
            }
        }
       return list;
    }

    public int getInnerWidth() {
        return 7;
    }

    public int getInnerHeight() {
        return 7;
    }
}
