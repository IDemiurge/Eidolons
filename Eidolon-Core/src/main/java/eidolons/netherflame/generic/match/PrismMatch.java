package eidolons.netherflame.generic.match;

import eidolons.netherflame.feature.reflectItem.ItemReflector;
import eidolons.netherflame.feature.reflectItem.Prism;
import eidolons.netherflame.feature.reflectItem.Sigil;
import eidolons.netherflame.feature.reflectItem.data.Inv2Enums;
import eidolons.netherflame.feature.reflectItem.data.MaterialData;
import eidolons.netherflame.feature.reflectItem.data.WeaponMasteryData;
import main.entity.type.ObjType;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Alexander on 1/31/2022
 */
public class PrismMatch implements Match<Sigil, Inv2Enums.PrismSlot, Prism> {

    Function<Inv2Enums.PrismSlot, ItemReflector.ArtifactSet> artifactProvider;
    Function<Inv2Enums.PrismSlot, Integer> artifactQualityProvider;
    Function<Inv2Enums.PrismSlot, Integer> artifactMagicProvider;
    Function<Inv2Enums.EnergyGrade, Inv2Enums.EnergyGrade> energyFilter;
    Function<Inv2Enums.EnergyGrade, Inv2Enums.EnergyGrade> energyGrade;
    WeaponMasteryData masteryData;

    public void process(MatchLogic.MatchResult<Sigil, Inv2Enums.PrismSlot, Prism> result){
        Sigil sigil = result.top;
        Inv2Enums.PrismSlot slot = result.center;
        Prism prism = result.bottom;

        MaterialData materialData = prism.getMaterialData();
        int quality = prism.getQualityMod() + artifactQualityProvider.apply(slot);
        int magic = prism.getMagicMod() + artifactQualityProvider.apply(slot);
        // if (magic<50) energy = null;
        int rarity = sigil.getRarity();
        Inv2Enums.EnergyGrade energy= prism.getEnergyGrade(); //depends on hero too
        energy = energyFilter.apply(energy); //TODO IDEA - do GRADE via Artifact power?
        energy = energyGrade.apply(energy);
        ItemReflector.ArtifactSet artifacts= artifactProvider.apply(slot);
        ObjType item = null;
        switch (slot) {
            case WeaponSet1:
            case WeaponSet2:
                 item = ItemReflector.createReflectedWeaponType(materialData, quality, magic, rarity, sigil.getType(), energy, masteryData, artifacts);
                break;
            case Armor:
                break;
            case Garments:
                break;
            case Jewelry:
                break;
            case Tokens:
                break;
        }
        //create simulated item? just so we can display some qualities
    }

}
