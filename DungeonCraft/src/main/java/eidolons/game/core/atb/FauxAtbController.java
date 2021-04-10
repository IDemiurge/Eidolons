package eidolons.game.core.atb;

/**
 * Created by JustMe on 3/26/2018.
 */
public class FauxAtbController extends AtbController {
    public FauxAtbController(AtbController original, AtbPrecalculator calculator) {
        super(original, calculator);
    }

    @Override
    protected boolean isPrecalc() {
        return true;
    }
}
