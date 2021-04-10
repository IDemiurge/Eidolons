package eidolons.entity.hero.deity;

import main.system.data.DataUnit;

public class DeityData extends DataUnit {

    public DeityData(String text) {
        super(text);
    }

    public enum deity_value{
        worldview,
        patron,
        nemesis,

    }

}
