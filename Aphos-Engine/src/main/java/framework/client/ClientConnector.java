package framework.client;

import framework.client.user.UserEventBuilder;
import framework.client.user.UserEventHandler;
import framework.entity.field.FieldEntity;
import logic.execution.event.LogicEvent;
import logic.execution.event.user.UserEvent;
import logic.execution.event.user.UserEventType;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/22/2023
 * <p>
 * So it can also work the same with some basic LibGdx?!
 */
public class ClientConnector {
    static UserEventHandler handler;
    private static ClientConnector connector= new ClientConnector();
    // SocketManager socketManager;

    public void fireEvent(String input) {
        UserEvent event = UserEventBuilder.createEvent(input);
        handler.handle(event);
    }

    public static ClientConnector client() {
        return connector;
    }

    public void receivedEvent(UserEventType type, Object arg) {
        if (type == UserEventType.Selection) {
            List<FieldEntity> list = new ArrayList<>();
            list.add(combat().getUnitById((Integer) arg));
            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.SELECT_BF_OBJ, list);
        }
    }


    public void sendEvent(LogicEvent event) {
        //this has to happen sequentially, but we don't want to block engine on send operation - in any case, continue
        // String toSend = UserEventBuilder.parseLogicEvent(event);
        // socketManager.send(toSend);
        //ERROR HANDLING?!
        //how to make sure that everything has arrived?!
    }

}
