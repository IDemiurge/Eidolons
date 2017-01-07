package main.libgdx.gui.layout;

import main.libgdx.gui.panels.sub.Comp;
import main.libgdx.gui.panels.sub.Container;
import main.system.auxiliary.StringMaster;
import main.system.graphics.MigMaster;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 1/7/2017.
 */
public class LayoutParser {
    public static final String X2 = "x2";
    public static final String Y2 = "y2";
    private static final String DELIMITER = " ";
    private   Container c;
    Map<Comp, Point> map;

    public LayoutParser(Container c) {
this.c=c;
    }

    public void add(  String s, Comp comp) {
//map.put(comp, p);
    }
        public Map<Comp, Point> parse(Container c, String s, Comp... comps) {
//        dynamically called - if resized etc ??
        //get ints
        int i = 0;
        for (String substring : StringMaster.openContainer(s)) {
            Comp comp = comps[i];
            i++;
            substring = process(c, substring, comp);
            int x = StringMaster.getInteger(
             substring.split(DELIMITER)[0]);
            int y = StringMaster.getInteger(
             substring.split(DELIMITER)[1]);

            comp.setX(x);
            comp.setY(y);
        }
        return map;
    }

    public static String process(Container c, String constraints, Comp comp) {
        int height = (int) comp.getHeight();
        int width = (int) comp.getWidth();
        int container_height = (int) c.getHeight();
        int container_width = (int) c.getWidth();
        return MigMaster.process(constraints,
         height, width, container_height, container_width);
    }
}
