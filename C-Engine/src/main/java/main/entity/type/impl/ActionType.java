package main.entity.type.impl;


import main.entity.type.ObjType;

public class ActionType extends ObjType {
    public ActionType(ObjType type) {
        super(type);
    }

    // TODO used by Communicator?

    public ActionType() {
        // TODO Auto-generated constructor stub
    }

    // public void invokeClicked() {
    // if (!game.getState().isMyTurn())
    // return; // "hollow sound"?
    // if (game.getManager().isSelecting() || !game.getState().isMyTurn())
    // return;
    // new Thread(this).start();
    // }

    // @Override
    // public void clicked() {
    //
    // setOwnerObj(game.getManager().getActiveObj());
    //
    // Ref ref = Ref.getCopy(getOwnerUnit().getRef());
    //
    // activate(ref);
    // }
    //
    // @Override
    // public boolean canBeActivated(Ref ref) {
    // if (costs.isVariable())
    // return true;
    // return costs.canBePaid(ref);
    // }

    // @Override
    // public boolean activate(Ref ref) {
    // if (!canBeActivated(ref))
    // return false;
    // if (game == null)
    // game = ref.getGame();
    // Active action = ref.getGame().getActionManager()
    // .newAction(this, ref, ownerObj.getOwner(), game);
    //
    // if (game.getState().isMyTurn() && !game.isOffline()) {
    // int index = game.getManager().getUnitActions(ownerObj)
    // .indexOf(this);
    // game.getCommunicator().transmitCreateActionCommand(
    // getOwnerUnit().getId(), index);
    // }
    // return action.activate();
    // }

}
