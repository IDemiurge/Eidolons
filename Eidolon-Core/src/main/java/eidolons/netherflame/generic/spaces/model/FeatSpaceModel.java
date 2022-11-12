package eidolons.netherflame.generic.spaces.model;

import eidolons.entity.feat.spaces.FeatSpaceData;

/**
 * Created by Alexander on 2/1/2022
 * for std feats that are ALWAYS made up of 3/3 active/passive
 */
public class FeatSpaceModel {
    FeatSpaceData data; //only meta data

    FeatModel active_1;
    FeatModel active_2;
    FeatModel active_3;

    FeatModel passive_1;
    FeatModel passive_2;
    FeatModel passive_3;

}
