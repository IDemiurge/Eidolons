package main.libgdx.gui.radial;

import main.system.auxiliary.StringMaster;
import main.test.debug.DebugRadialManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/29/2016.
 */
public class RadialManager {


        public List< RadialMenu.CreatorNode> getDebugNodes(){
        List< RadialMenu.CreatorNode> list = new LinkedList<>();
        new LinkedList<DebugRadialManager.DEBUG_CONTROL>(
             Arrays.asList(DebugRadialManager.DEBUG_CONTROL.values())).forEach(c->{
            RadialMenu.CreatorNode node = new RadialMenu.CreatorNode();
            node.texture = null ;
            node.name =  StringMaster.getWellFormattedString(c.name());
            node.action = new Runnable() {
                @Override
                public void run() {
                    DebugRadialManager.clicked(c);
                }
            };

            list.add(node);
        });
       return list;
    }
}
