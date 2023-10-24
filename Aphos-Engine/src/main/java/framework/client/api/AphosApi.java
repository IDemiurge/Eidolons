package framework.client.api;

import logic.execution.event.user.UserEvent;

/**
 * Created by Alexander on 10/22/2023
 *
 * Logic does not know about graphics
 * LibGdx class will implement this
 *
 * all methods are void
 */
public interface AphosApi {
    void receiveEvent(UserEvent event);

}
