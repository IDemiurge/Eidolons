package framework.client;

import logic.execution.event.LogicEvent;
import logic.execution.event.user.UserEvent;

/**
 * Created by Alexander on 8/22/2023
 *
 * So it can also work the same with some basic LibGdx?!
 */
public class ClientConnector {
    static UserEventHandler handler;
    // SocketManager socketManager;

    public void fireEvent(String input){
        UserEvent event = UserEventBuilder.createEvent(input);
        handler.handle(event);
    }

    public void sendEvent(LogicEvent event){
        //this has to happen sequentially, but we don't want to block engine on send operation - in any case, continue
        // String toSend = UserEventBuilder.parseLogicEvent(event);
        // socketManager.send(toSend);
        //ERROR HANDLING?!
        //how to make sure that everything has arrived?!
    }
}
