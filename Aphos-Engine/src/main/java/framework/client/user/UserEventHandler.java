package framework.client.user;

import framework.client.generic.EventCallback;
import framework.client.generic.EventCallbackParam;
import logic.execution.event.user.UserEvent;
import logic.execution.event.user.UserEventType;

/**
 * Created by Alexander on 8/22/2023
 *
 * How to balance code between 'switch panel style' and callbacks spread all over the codebase?
 */
public class UserEventHandler {

    public void handle(UserEvent event){
        // event.get
        new EventCallbackParam();
    }
    public static void bind(UserEventType type, EventCallback callback){

    }
}
