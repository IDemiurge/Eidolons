package main.game.core;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.game.logic.action.context.Context;

/**
 * Created by JustMe on 4/3/2017.
 */
public class ActionInput {
        private DC_ActiveObj action;
        private Context context;

        public ActionInput(DC_ActiveObj action, Context context) {
            this.action = action;
            this.context = context;
        }

        public ActionInput(DC_ActiveObj action, DC_Obj target) {
            this.action = action;
            context = new Context(action.getOwnerObj(), target);
        }

        public DC_ActiveObj getAction() {
            return action;
        }

        public Context getContext() {
            return context;
        }
    }
