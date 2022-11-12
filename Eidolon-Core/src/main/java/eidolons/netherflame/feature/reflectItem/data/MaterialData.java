package eidolons.netherflame.feature.reflectItem.data;

public class MaterialData {
    private Boolean liteHeavyBalanced;
    private int tier;

    public MaterialData(Boolean liteHeavyBalanced, int tier) {
        this.liteHeavyBalanced = liteHeavyBalanced;
        this.tier = tier;
    }

    public Boolean getLiteHeavyBalanced() {
        return liteHeavyBalanced;
    }

    public int getTier() {
        return tier;
    }

    // enough to determine concrete material for metal/bone/wood/...
    // power vs weight scales?


}
