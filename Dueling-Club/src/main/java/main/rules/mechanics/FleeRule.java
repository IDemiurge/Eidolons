package main.rules.mechanics;

import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.DC_Game;

import java.util.LinkedList;
import java.util.List;

public class FleeRule {
    /*
	 * check facing
	 * 
	 * don't add to slain
	 * 
	 * for hero party - instead of betrayal... and manual - to avoid complete
	 * distaster!
	 */

    private List<DC_HeroObj> fledUnits = new LinkedList<>();
    private DC_Game game;

    public FleeRule(DC_Game game) {
        this.game = game;
    }

    public static boolean isFleeAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void flee(Ref ref, boolean permanent) {
        flee(ref.getSourceObj(), permanent);
    }

    // public void comeBack(){ }
    public void flee(Obj obj, boolean permanent) {
		/*
		 * should it take some time? ;)
		 *
		 * Yes it should...
		 *
		 * Perhaps a kind of channeling?
		 *
		 * the action itself could be like that
		 */
        game.getBattleField().remove(obj);
//        fledUnits.added(obj);
		/*
		 * is it permanent? perhaps some effects should allow a 'come back' ;)
		 *
		 * some effects may actually feign flight - sky strike, atral
		 * banishment, so the unit cannot be remove from the game... and I also
		 * don't want to remove anything from it, it will be just invisible ;)
		 */
        // if (permanent)
        // ref.getSourceObj().kill(ref.getSourceObj(), false, true);

    }

}
