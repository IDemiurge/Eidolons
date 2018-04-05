package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

/**
 * Created by JustMe on 4/1/2018.
 */
public class ClearshotMapper extends ObjVisionMapper<Boolean> {

    @Override
    protected boolean isClearRequired() {
        return true;
    }
}
