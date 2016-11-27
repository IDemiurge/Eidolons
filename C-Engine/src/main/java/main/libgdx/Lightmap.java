package main.libgdx;

import main.content.parameters.PARAMETER;
import main.content.parameters.Param;
import main.entity.obj.MicroObj;
import main.system.datatypes.DequeImpl;

/**
 * Created by PC on 19.11.2016.
 */
public class Lightmap {

    private DequeImpl<MicroObj> units;
    public Lightmap(DequeImpl<MicroObj> un) {
    this.units = un;
        System.out.println("created");
        for (int i = 0;i < un.size();i++){
            System.out.println(un.get(i).getName());
            System.out.println(un.get(i).getType());
            System.out.println("====================================================================================================");
            System.out.println("====================================================================================================");
            System.out.println("====================================================================================================");
        }
    }
    public void moveBody(MicroObj obj,float x,float y){

    }

}
