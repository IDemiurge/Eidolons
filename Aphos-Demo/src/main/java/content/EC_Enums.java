package content;

public class EC_Enums {

    public enum SPELL_DAMAGE_MEASURE{
/*
Tiny – 6+1|2 * SP + 3|2 * Mstr, max = 50
Minor – 10+2|3 * SP + 5|3 * Mstr, max = 100
Medium – 10+4|6 * SP + 9|6 * Mstr, max = 160
Heavy– 10+4|6 * SP + 9|6 * Mstr, max = 250
Severe – 10+4|6 * SP + 9|6 * Mstr, max = 400
Massive – 10+4|6 * SP + 9|6 * Mstr, max = 650
Terminal – 10+4|6 * SP + 9|6 * Mstr, max = 1000
 */
        ;
        String name;
        int spCoef, msCoef, max;
    }
}
