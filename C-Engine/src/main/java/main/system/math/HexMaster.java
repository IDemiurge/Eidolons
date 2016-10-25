package main.system.math;

public class HexMaster {

    public static int getHexFromDecimal(int dec) {
        main.system.auxiliary.LogMaster.log(dec + " decimal to hex...");
        int hex = 0;
        int buffer = dec;
        int div = 1;
        while (buffer > 0) {
            buffer = buffer / div;
            div *= 16;
        }
        div /= 16;
        int factor = 1;
        main.system.auxiliary.LogMaster.log(div + " divider...");
        while (div > 1) {
            hex += dec % div * factor;

            main.system.auxiliary.LogMaster.log(dec % div * factor
                    + " quote...");
            div /= 16;
            factor *= 10;
            main.system.auxiliary.LogMaster.log(hex + " => hex...");
        }

        main.system.auxiliary.LogMaster.log(dec + " decimal to hex = " + hex);
        return hex;
    }

}
