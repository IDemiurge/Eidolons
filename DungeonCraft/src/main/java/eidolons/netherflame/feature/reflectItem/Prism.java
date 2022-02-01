package eidolons.netherflame.feature.reflectItem;

import eidolons.netherflame.feature.reflectItem.data.Inv2Enums;
import eidolons.netherflame.feature.reflectItem.data.MaterialData;

/**
 * Created by Alexander on 1/31/2022
 * Material - 3 tiers, 3 classes in multiple Groups (metal, ...)
 * Energy Grade (base)
 */
public class Prism {
    // PrismType type; //entity? That'd be easiest.
    private Inv2Enums.EnergyGrade energyGrade;
    private MaterialData materialData;
    private int qualityMod;
    private int magicMod;

    public Inv2Enums.EnergyGrade getEnergyGrade() {
        return energyGrade;
    }

    public MaterialData getMaterialData() {
        return materialData;
    }

    public int getQualityMod() {
        return qualityMod;
    }

    public int getMagicMod() {
        return magicMod;
    }




}
