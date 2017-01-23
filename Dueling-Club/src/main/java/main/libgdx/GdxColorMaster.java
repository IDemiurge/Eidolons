package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;

/**
 * Created by JustMe on 1/23/2017.
 */
public class GdxColorMaster {
    public static final  Color ENDURANCE =  getColor(210, 100, 110,1f);
    public static final  Color TOUGHNESS = getColor(65, 35, 15,1f);
    public static final  Color STAMINA = getColor(180, 150, 45,1f);
    public static final  Color ESSENCE = getColor(80, 30, 225,1f);
    public static final  Color FOCUS = getColor(10, 175, 200,1f);
    public static final  Color MORALE = getColor(150, 60, 180,1f);

    public static Color getColor(int r, int b, int g, float a) {
       return  new  Color(r/100, b/100,g/100, a);
    }
        public static Color getParamColor(PARAMETER param) {
            if (param instanceof PARAMS) {
                switch (((PARAMS) param)) {
                    case C_MORALE:
                        return MORALE;
                    case C_FOCUS:
                        return FOCUS;
                    case C_ENDURANCE:
                        return ENDURANCE;
                    case C_STAMINA:
                        return STAMINA;
                    case C_ESSENCE:
                        return ESSENCE;
                    case C_TOUGHNESS:
                        return TOUGHNESS;
                    case C_INITIATIVE:
                    case C_ENERGY:
                    case C_N_OF_ACTIONS:
                }
            }

            return Color.WHITE ;}
}
