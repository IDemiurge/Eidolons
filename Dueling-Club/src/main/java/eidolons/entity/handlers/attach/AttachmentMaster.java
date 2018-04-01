package eidolons.entity.handlers.attach;

import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.handlers.*;

/**
 * Created by JustMe on 2/26/2017.
 */
public class AttachmentMaster extends EntityMaster<DC_HeroAttachedObj> {

    public AttachmentMaster(DC_HeroAttachedObj entity) {
        super(entity);
    }

    @Override
    protected EntityAnimator<DC_HeroAttachedObj> createEntityAnimator() {
        return null;
    }

    @Override
    protected EntityLogger<DC_HeroAttachedObj> createEntityLogger() {
        return null;
    }

    @Override
    protected EntityInitializer<DC_HeroAttachedObj> createInitializer() {
        return null;
    }

    @Override
    protected EntityChecker<DC_HeroAttachedObj> createEntityChecker() {
        return null;
    }

    @Override
    protected EntityResetter<DC_HeroAttachedObj> createResetter() {
        return null;
    }

    @Override
    protected EntityCalculator<DC_HeroAttachedObj> createCalculator() {
        return null;
    }

    @Override
    protected EntityHandler<DC_HeroAttachedObj> createHandler() {
        return null;
    }
}
