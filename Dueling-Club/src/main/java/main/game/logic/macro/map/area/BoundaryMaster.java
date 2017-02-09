package main.game.logic.macro.map.area;

import main.game.logic.macro.map.Area;
import main.game.logic.macro.map.Region;
import main.system.auxiliary.RandomWizard;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class BoundaryMaster {

	/*
     * 1) generate lines
	 * 2) random vector
	 * 3) prune irrelevant lines (outside spectrum)
	 * 4) find first line crossed by vector
	 * 5) solve up/down -> getOrCreate 1 or 2nd
	 * 
	 * ++ re-use points - 
	 */

    static List<AreaLine> lines = new LinkedList<>();

    public static Area getAreaForPoint(Point p) {
        float vectorSlope = (RandomWizard.getRandomInt(10) + 1) / 10;
        int length = 100; // random? any? maximum?
        AreaLine vectorLine = new AreaLine(new BoundaryPoint(p.x, p.y),
                new BoundaryPoint((int) (p.x * vectorSlope),
                        (int) (p.y * (1 - vectorSlope))), null);
        List<AreaLine> relevantLines = new LinkedList<>();
        for (AreaLine line : lines) {
            boolean side = isToTheLeft(vectorLine, line.p1);
            if (side == isToTheLeft(vectorLine, line.p2)) {
                relevantLines.add(line);
            }
        }
        List<AreaLine> crossed = new LinkedList<>();
        while (crossed.size() != 1) {
            crossed = getCrossedLines(relevantLines, vectorSlope, length);
            length--;
        }
        AreaLine line = crossed.get(0);

        return isToTheLeft(line, p) ? line.area1 : line.area2;

    }

    private static List<AreaLine> getCrossedLines(List<AreaLine> relevantLines,
                                                  float vectorSlope, int length) {
        // TODO or was there some better way to getOrCreate the first crossed line?
        // perhaps just by distance from p to p1/p2
        return null;
    }

    public static Boolean isToTheLeft(AreaLine line, Point p) {
        if (line.slope == null) {
            line.slope = new Float(line.p1.x - line.p2.x)
                    / new Float(line.p1.y - line.p2.y);
        }
        float xToY = new Float(p.x) / new Float(p.y);
        if (line.slope == xToY) {
            return null;
        }
        return line.slope > xToY;
    }

    public static void generateLines(Region region) {
        BoundaryPoint lastCoordinate;
        for (Area area : region.getAreas()) {
//            for (BoundaryPoint p : area.getBoundaries()) { // WE should have
//                // BoundaryViewMode
//                // for Regions
//                lastCoordinate = p;
//                if (lastCoordinate == null)
//                    continue;
//                lines.add(new AreaLine(lastCoordinate, p, area));
//            }
        }
        for (AreaLine line : lines) {
            // init 2nd area!
        }
    }

}
