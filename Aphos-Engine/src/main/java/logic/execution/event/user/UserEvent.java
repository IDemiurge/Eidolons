package logic.execution.event.user;

/**
 * Created by Alexander on 8/22/2023
 * Main way of communication from CLIENT
 */
public class UserEvent {
    public final UserEventType type;
    public final Object[] args;

    public UserEvent(UserEventType type, Object[] args) {
        this.type = type;
        this.args = args;
    }
}
