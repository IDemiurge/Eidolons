package libgdx.map.path;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.Path;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import libgdx.map.layers.AlphaMap;
import main.data.XLinkedMap;
import main.system.SortMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 3/15/2018.
 */
public class PixmapPathBuilder {

    static Map<AlphaMap.ALPHA_MAP, List<Vector2>> pointsMap = new XLinkedMap<>();
    private static final AlphaMap.ALPHA_MAP[] impassable = new AlphaMap.ALPHA_MAP[]{
     AlphaMap.ALPHA_MAP.OCEAN, AlphaMap.ALPHA_MAP.INLAND_WATER, AlphaMap.ALPHA_MAP.IMPASSABLE
    };
    private static Vector2 lastPoint;
    AlphaMap.ALPHA_MAP[] std_preferredMapType = new AlphaMap.ALPHA_MAP[]{
     AlphaMap.ALPHA_MAP.ROADS, AlphaMap.ALPHA_MAP.PATHS, null, AlphaMap.ALPHA_MAP.WILDERNESS,
    };


    public static void writePathFile(Pixmap pixmap, String outputPath) {

        int sideSplit = 12;
        int regionWidth = pixmap.getWidth() / sideSplit;
        int regionHeight = pixmap.getHeight() / sideSplit;
        List<List<Vector2>> regions = new ArrayList<>();
        for (int i = 0; i < sideSplit; i++) {
            for (int j = 0; j < sideSplit; j++) {
                List<Vector2> pixelPoints = new ArrayList<>();
                for (int x = regionWidth * i; x < regionWidth * (i + 1); x++) {
                    for (int y = regionWidth * j; y < regionHeight * (j + 1); y++) {

                        if (new Color(pixmap.getPixel(x, y)).a != 0) {
                            pixelPoints.add(new Vector2(x, pixmap.getHeight() - y));
                        }

                    }
                }
                regions.add(pixelPoints);
            }
        }

        int minDist = 4;
        for (List<Vector2> pixelPoints : regions) {
            for (Vector2 sub : new ArrayList<>(pixelPoints)) {
                if (!pixelPoints.contains(sub))
                    continue;
                for (Vector2 sub1 : new ArrayList<>(pixelPoints)) {
                    if (sub != sub1 &&
                     sub.dst(sub1) <= minDist) {
                        pixelPoints.remove(sub1);
//                        continue madness;
                    }
                }
            }
        }

        StringBuilder outputBuilder = new StringBuilder();
        for (List<Vector2> pixelPoints : regions) {
            for (Vector2 sub : pixelPoints) {
                outputBuilder.append((int) sub.x).append("-").append((int) sub.y).append(";");
            }
        }
        String output = outputBuilder.toString();
        main.system.auxiliary.log.LogMaster.log(1, " " + output);
        FileManager.write(output, outputPath);
    }

    public static Sequence<SteeringBehavior<Vector2>> buildPathSequence(SteeringAgent agent, Vector2 orig,
                                                                        Vector2 dest, AlphaMap.ALPHA_MAP map) {


        //to road, on road, from road
        boolean road = false;
        float distance = orig.dst(dest);
        if (map == AlphaMap.ALPHA_MAP.ROADS || map == AlphaMap.ALPHA_MAP.PATHS) {
            float maxDistToRoad = 100 + distance / 10;
            float dist = getDistanceToMap(orig, maxDistToRoad, AlphaMap.ALPHA_MAP.ROADS, AlphaMap.ALPHA_MAP.PATHS);
            if (dist < maxDistToRoad) {
                dist = getDistanceToMap(dest, maxDistToRoad, AlphaMap.ALPHA_MAP.ROADS, AlphaMap.ALPHA_MAP.PATHS);
                road = dist < maxDistToRoad;
            }
        }

        return road ? getRoadSequence(agent, orig, dest) : buildMapPathSequence(agent, orig, dest, null);
    }

    static Task<SteeringBehavior<Vector2>> getTask(SteeringAgent agent, SteeringBehavior<Vector2> behavior) {
        return new MoveTask(agent, behavior);
    }

    static Sequence<SteeringBehavior<Vector2>> buildMapPathSequence(SteeringAgent agent, Vector2 orig,
                                                                    Vector2 dest, AlphaMap.ALPHA_MAP map) {
        Sequence<SteeringBehavior<Vector2>> sequence = new Sequence<>();
        Path<Vector2, LinePathParam> path = buildPath(map, orig, dest, getImpassable());
        SteeringBehavior<Vector2> behavior = getFollowPath(path, agent);
        sequence.addChild(getTask(agent, behavior));
        return sequence;
    }

    static SteeringBehavior<Vector2> getFollowPath(Path<Vector2, LinePathParam> path, SteeringAgent agent) {
        FollowPath behavior = new FollowPath(agent, path) {
            @Override
            protected SteeringAcceleration arrive(SteeringAcceleration steering,
                                                  Vector targetPosition) {
                steering.setZero();
                setEnabled(false);
                return super.arrive(steering, targetPosition);
            }

            @Override
            protected SteeringAcceleration calculateRealSteering(SteeringAcceleration
                                                                  steering) {
                main.system.auxiliary.log.LogMaster.log(1," " );
                return super.calculateRealSteering(steering);
            }
        };

        behavior.setArriveEnabled(true);
        behavior.setEnabled(true);
        behavior.setArrivalTolerance(25);
        behavior.setTimeToTarget(2);
        behavior.setPredictionTime(1f);
        behavior.setPathOffset(-1f);
        behavior.setDecelerationRadius(25f);
        return behavior;
    }

    static Sequence<SteeringBehavior<Vector2>> getRoadSequence(SteeringAgent agent, Vector2 orig,
                                                               Vector2 dest) {
        Sequence<SteeringBehavior<Vector2>> sequence = new Sequence<>();

        Vector2 roadPoint = lastPoint;
        SteeringBehavior<Vector2> toRoad = getFollowPath(
         buildPath(orig, roadPoint, getImpassable()), agent);
        sequence.addChild(getTask(agent, toRoad));

        Vector2 roadEndPoint = getClosestMapPoint(roadPoint, getPoints(AlphaMap.ALPHA_MAP.ROADS), dest);
        LinePath<Vector2> roadPath = buildPath(AlphaMap.ALPHA_MAP.ROADS, roadPoint, roadEndPoint, getImpassable());
        SteeringBehavior<Vector2> road = getFollowPath(roadPath, agent);
        sequence.addChild(getTask(agent, road));

        LinePath<Vector2> fromRoadPath = buildPath(roadEndPoint, dest, getImpassable());
        SteeringBehavior<Vector2> fromRoad = getFollowPath(fromRoadPath, agent);
        sequence.addChild(getTask(agent, fromRoad));

        return sequence;
    }

    private static Vector2 getClosestMapPoint(Vector2 roadPoint, List<Vector2> points,
                                              Vector2 dest) {
        //sort?
//        int i = points.indexOf(roadPoint) + 1;
//        for (; i < points.size(); i++) {
//
//        }
        //crop by rectangle?
        Rectangle rect = new Rectangle(Math.min(dest.x, roadPoint.x), Math.min(dest.y, roadPoint.y), Math.abs(roadPoint.x - dest.x),
         Math.abs(roadPoint.y - dest.y));

        float minDist = roadPoint.dst(dest);
        Vector2 closest = roadPoint;
        for (Vector2 sub : points) {
            if (!rect.contains(sub))
                continue;
            float dist = sub.dst(dest);
            if (dist < minDist) {
                minDist = dist;
                closest = sub;
            }
        }
        return closest;
    }

    private static LinePath<Vector2> buildPath(Vector2 orig, Vector2 roadPoint, AlphaMap.ALPHA_MAP... impassable) {
        return buildPath(null, orig, roadPoint, impassable);
    }

    private static float getDistanceToMap(Vector2 v, float maxDist, AlphaMap.ALPHA_MAP... maps) {
//        FreeTravelMaster.getInstance().check(map, x, y);
        float minDist = Float.MAX_VALUE;
        for (AlphaMap.ALPHA_MAP sub : maps) {
            List<Vector2> points = getPoints(sub);
            //suppose these are sorted then we can kind of skip some chunks? performance...
            for (Vector2 vector : points) {
                float dist = v.dst(vector);
                if (dist <= maxDist)
                    return dist;
                if (dist < minDist) {
                    minDist = dist; //TODO return map type! and the point too...
                    lastPoint = vector;
                }
            }
            //segmented map?
            //regions?
        }
        return minDist;
    }

    public static LinePath<Vector2> buildPath(AlphaMap.ALPHA_MAP map, Vector2 orig,
                                              Vector2 dest, AlphaMap.ALPHA_MAP... impassable) {

        List<Vector2> list = new ArrayList<>();
        Vector2 last;
        if (map != null) {
            Rectangle rect = new Rectangle(Math.min(orig.x, dest.x), Math.min(orig.y, dest.y),
             Math.abs(orig.x - dest.x),
             Math.abs(orig.y - dest.y));

            for (Vector2 v : getPoints(map)) {
                if (rect.contains(v))
                    list.add(v);
            }

            SortMaster.sortByExpression(true, list, (p) -> Math.round(orig.dst((Vector2) p)));
//TODO if I had more points, I would have to crop those far from the path!
//            float minDist = 8;
//            for (Vector2 sub : new ArrayList<>(list)) {
//                if (last.dst(sub) > minDist) {
//                    list.remove(sub);
//                }
//            else if (checkSegmentCrosses(sub, last, impassable))
//                list.remove(sub);
            //ok so we remove point if it cannot be reached directly... isn't it better to
            // try and bend around it?
//                else
//                    last = sub;
//            }
        } else {
            list = GdxGeomentry.getPointsBetween(orig, dest, 10);
        }
        Array<Vector2> points = new Array<>();
        last = orig;
        for (Vector2 sub : new ArrayList<>(list)) {
            if (checkSegmentCrosses(sub, last, impassable)) {
                break;
            }
            last = sub;
            points.add(sub);
        }
        try {
            return new LinePath<>(points,true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return null;
    }

    private static boolean checkSegmentCrosses(Vector2 v, Vector2 v2,
                                               AlphaMap.ALPHA_MAP... mapTypes) {
        int n = 4;
        for (int i = 0; i < n; i++) {
            Vector2 checkPoint = new Vector2(v).lerp(v2, 1f / n * (i));
//            for (ALPHA_MAP map : mapTypes)
//                if (FreeTravelMaster.getInstance().check(map, checkPoint.x, checkPoint.y))
//                    return true;
        }
        return false;
    }

    private static List<Vector2> getPoints(AlphaMap.ALPHA_MAP map) {
        List<Vector2> list = pointsMap.get(map);
        if (list == null) {
            list = new ArrayList<>();
            String string = FileManager.readFile(AlphaMap.getPointsPath(map));
            for (String substring : ContainerUtils.openContainer(string)) {
                String[] parts = substring.split("-");
                Float x = Float.valueOf(parts[0]);
                Float y = Float.valueOf(parts[1]);
                list.add(new Vector2(x, y));
            }
            pointsMap.put(map, list);
        }
        return list;
    }

    public static AlphaMap.ALPHA_MAP[] getImpassable() {
        return impassable;
    }

}
