package main.libgdx.screens.map.path;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by JustMe on 3/17/2018.
 */
public   class MoveTask extends Task<SteeringBehavior<Vector2>> {
    private final SteeringAgent agent;
    SteeringBehavior<Vector2> behavior;
    boolean started;


    public MoveTask(SteeringAgent agent, SteeringBehavior<Vector2> behavior) {
        this.agent = agent;
        this.behavior = behavior;
        tree = new BehaviorTree<>();
    }

    @Override
    public SteeringBehavior<Vector2> getObject() {
        return behavior ;
    }

    @Override
    protected int addChildToTask(Task<SteeringBehavior<Vector2>> child) {
        return 0;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Task<SteeringBehavior<Vector2>> getChild(int i) {
        return null;
    }

    public Status execute() {
        if (!started) {
            agent.setSteeringBehavior(getObject());
            started = true;
        }
        if (agent.getSteeringBehavior() != getObject())
            return Status.FAILED;
        if (getObject().isEnabled())
            return Status.RUNNING;
        else
            return Status.SUCCEEDED;
    }
    @Override
    public final void run () {
        Status result = execute();
        if (result == null) throw new IllegalStateException("Invalid status 'null' returned by the execute method");
        switch (result) {
            case SUCCEEDED:
                return;
            case FAILED:
                return;
            case RUNNING:
                return;
            default:
                throw new IllegalStateException("Invalid status '" + result.name() + "' returned by the execute method");
        }
    }

    @Override
    public void childSuccess(Task<SteeringBehavior<Vector2>> task) {

    }

    @Override
    public void childFail(Task<SteeringBehavior<Vector2>> task) {

    }

    @Override
    public void childRunning(Task<SteeringBehavior<Vector2>> runningTask, Task<SteeringBehavior<Vector2>> reporter) {

    }

    @Override
    protected Task<SteeringBehavior<Vector2>> copyTo(Task<SteeringBehavior<Vector2>> task) {
        return null;
    }

}
