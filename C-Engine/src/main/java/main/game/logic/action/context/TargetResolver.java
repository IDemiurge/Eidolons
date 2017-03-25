package main.game.logic.action.context;

import main.entity.obj.Obj;
import main.game.logic.action.context.Context.IdKey;

import java.util.Stack;

/**
 * Created by JustMe on 3/23/2017.
 */
public class TargetResolver {
/*
Event



 */
    public Obj resolveTarget(Stack<IdKey> queue, Obj obj){
        if (queue.isEmpty())
            return obj;
        IdKey key = queue.pop();
        obj = getObj(key, obj);
        return resolveTarget(queue, obj);

    }

    private Obj getObj(IdKey key, Obj obj) {
        switch(key){

        }
        return obj;
    }
}
