package main.level_editor.backend.functions.mapping;

import com.google.inject.internal.util.ImmutableSet;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import org.junit.Test;

import java.awt.*;
import java.util.List;
import java.util.*;

import static main.system.auxiliary.log.LogMaster.log;

public class ModuleGridMapper {

    private static final Module M1 = new Module(null, 30, 90, "1");
    private static final Module M2 = new Module(null, 30, 90, "2");
    private static final Module M3 = new Module(null, 40, 90, "3");
    private static final Module M4 = new Module(null, 110, 40, "4");
    private static final Module M5 = new Module(null, 110, 20, "5");

    public static int maxHeight = 45;
    public static int maxWidth = 45;
    private static int height;
    private static int width;

    private Set<Module> getModules() {
        return ImmutableSet.of(M1, M2, M3, M4, M5);
    }

    @Test
    public void test() {
        getOptimalGrid(getModules());
    }

    public LinkedHashMap<Point, Module> getOptimalGrid(Set<Module> modules) {
        log(LOG_CHANNEL.BUILDING, "Making Optimal Grid for: " + modules);
        int n = modules.size();// getModuleHandler().modules.size();
        int min_size = Integer.MAX_VALUE;

        int[] cols = n % 2 == 0
                ? new int[]{0, n / 2, n}
                : new int[]{0, n / 2, n / 2 + 1, n};
        int[] rows = n % 2 == 0
                ? new int[]{0, n / 2, n}
                : new int[]{0, n / 2, n / 2 + 1, n};

        int[][] combos = new int[n % 2 == 0 ? 3 : 4][2];
        if (n % 2 == 0) {
            combos[0] = new int[]{cols[1], rows[1]};
            combos[1] = new int[]{cols[0], rows[2]};
            combos[2] = new int[]{cols[2], rows[0]};
        } else {
            combos[0] = new int[]{cols[1], rows[2]};
            combos[1] = new int[]{cols[2], rows[1]};
            combos[2] = new int[]{cols[0], rows[3]};
            combos[3] = new int[]{cols[3], rows[0]};
        }


        LinkedHashMap<Point, Module> moduleGrid;
        LinkedList<Module> pool = new LinkedList<>(modules);
        java.util.List<java.util.List<Module>> orderVariants =
                new ListMaster<Module>().generatePerm(pool);

        log(LOG_CHANNEL.BUILDING, "Grid params: " +
                "orderVariants=" + orderVariants +
                "\ncombos=" + Arrays.deepToString(combos));

        LinkedHashMap<Point, Module> pick = null;
        for (int[] combo : combos) {
            int w = combo[0];
            int h = combo[1];
            for (List<Module> orderVariant : orderVariants) {
                moduleGrid = new LinkedHashMap<>();
                Iterator<Module> iterator = orderVariant.iterator();
                for (int i = 0; i <= w; i++) {
                    for (int j = 0; j <= h; j++) {
                        if (!iterator.hasNext()) {
                            break;
                        }
                        moduleGrid.put(new Point(i, j), iterator.next());
                    }
                }
                int size =
                        calculateTotalSquareSize(moduleGrid);
                if (size < min_size) {
                    pick = moduleGrid;
                    min_size = size;
                    maxHeight = height;
                    maxWidth = width;
                }
            }


        }

        log(LOG_CHANNEL.BUILDING, "Grid params: " +
                "Final pick=" + pick +
                "\nFinal Width=" + maxWidth +
                "\nFinal Height=" + maxHeight);

        return pick;
    }

    private static int calculateTotalSquareSize(Map<Point, Module> moduleGrid) {
        height = 0;
        width = 0;
        Map<Integer, Integer> colMap = new LinkedHashMap<>();
        Map<Integer, Integer> rowMap = new LinkedHashMap<>();

        for (Point point : moduleGrid.keySet()) {
            Module module = moduleGrid.get(point);
//            Coordinates at= getModulePlacement(module);
            int h = module.getEffectiveHeight(true);
            MapMaster.addToIntegerMap(colMap, point.x, h);
            int w = module.getEffectiveWidth(true);
            MapMaster.addToIntegerMap(rowMap, point.y, w);
            //cols and rows!
        }
        for (Integer value : colMap.values()) {
            if (value >= height) {
                height = value;
            }
        }
        for (Integer value : rowMap.values()) {
            if (value >= width) {
                width = value;
            }
        }

        return height * width + Math.round(height * height * 0.1f);
    }
}
