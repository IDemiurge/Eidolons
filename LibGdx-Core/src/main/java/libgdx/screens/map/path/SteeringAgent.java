package libgdx.screens.map.path;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by JustMe on 3/15/2018.
 */
public class SteeringAgent extends SteerableAdapter<Vector2> {

    Vector2 position;
    float orientation;
    float maxSpeed;
    boolean independentFacing;
    SteeringBehavior<Vector2> steeringBehavior;
    private SteeringAcceleration<Vector2> steeringOutput;
    private Vector2 linearVelocity;
    private float angularVelocity;

	/* Here you should implement missing methods inherited from Steerable */

    public static float calculateOrientationFromLinearVelocity(Steerable<Vector2> character) {
        // If we haven't got any velocity, then we can do nothing.
        if (character.getLinearVelocity().isZero(character.getZeroLinearSpeedThreshold()))
            return character.getOrientation();

        return character.vectorToAngle(character.getLinearVelocity());
    }

    // Actual implementation depends on your coordinate system.
    // Here we assume the y-axis is pointing upwards.
    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    // Actual implementation depends on your coordinate system.
    // Here we assume the y-axis is pointing upwards.
    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return steeringOutput.linear;
    }

    public void update(float delta) {
        if (steeringBehavior != null) {
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringOutput);

			/*
             * Here you might want to add a motor control layer filtering steering accelerations.
			 *
			 * For instance, a car in a driving game has physical constraints on its movement:
			 * - it cannot turn while stationary
			 * - the faster it moves, the slower it can turn (without going into a skid)
			 * - it can brake much more quickly than it can accelerate
			 * - it only moves in the direction it is facing (ignoring power slides)
			 */

            // Apply steering acceleration to move this agent
            applySteering(steeringOutput, delta);
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steering, float time) {
        // Update position and linear velocity. Velocity is trimmed to maximum speed
        if (linearVelocity == null)
            linearVelocity = new Vector2(0, 0);
        this.position.mulAdd(linearVelocity, time);
        this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());

        // Update orientation and angular velocity
        if (independentFacing) {
            this.orientation += angularVelocity * time;
            this.angularVelocity += steering.angular * time;
        } else {
            // For non-independent facing we have to align orientation to linear velocity
            float newOrientation = calculateOrientationFromLinearVelocity(this);
            if (newOrientation != this.orientation) {
                this.angularVelocity = (newOrientation - this.orientation) * time;
                this.orientation = newOrientation;
            }
        }
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        super.setMaxAngularSpeed(maxAngularSpeed);
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxSpeed = maxLinearSpeed;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return super.getZeroLinearSpeedThreshold();
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        super.setZeroLinearSpeedThreshold(value);
    }

    @Override
    public float getMaxLinearAcceleration() {
        return 15;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        super.setMaxLinearAcceleration(maxLinearAcceleration);
    }

    @Override
    public float getMaxAngularAcceleration() {
        return 1;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        super.setMaxAngularAcceleration(maxAngularAcceleration);
    }

    @Override
    public float getOrientation() {
        return super.getOrientation();
    }

    @Override
    public void setOrientation(float orientation) {
        super.setOrientation(orientation);
    }

    @Override
    public float getAngularVelocity() {
        return steeringOutput.angular;
    }

    @Override
    public float getBoundingRadius() {
        return super.getBoundingRadius();
    }

    @Override
    public boolean isTagged() {
        return super.isTagged();
    }

    @Override
    public void setTagged(boolean tagged) {
        super.setTagged(tagged);
    }

    @Override
    public Location<Vector2> newLocation() {
        return super.newLocation();
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        steeringOutput =
         new SteeringAcceleration<>(new Vector2(0, 0));
        linearVelocity = new Vector2(0, 0);
        angularVelocity = 1;

        this.steeringBehavior = steeringBehavior;
    }
}