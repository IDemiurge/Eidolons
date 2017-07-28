package main.system;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class GuiEventManager {
    private static EventBus instance;
    private static Lock initLock = new ReentrantLock();
    private static List<Runnable> callbacks = new ArrayList<>(20);

    public static void bind(GuiEventType type, final EventCallback event) {
        final MessageConsumer<EventCallbackParam> consumer = getInstance().localConsumer(type.name());
        consumer.handler(objectMessage ->
                callbacks.add(() ->
                        event.call(objectMessage.body())));
    }

    public static void trigger(final GuiEventType type) {
        trigger(type, null);
    }

    public static void trigger(final GuiEventType type, Object obj) {
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(50000);
        options.setCodecName("default-codec");
        EventCallbackParam callbackParam;
        if (!(obj instanceof EventCallbackParam)) {
            callbackParam = new EventCallbackParam(obj);
        } else {
            callbackParam = (EventCallbackParam) obj;
        }
        getInstance().publish(type.name(), callbackParam, options);
    }

    public static void processEvents() {
        if (callbacks.size() > 0) {
            List<Runnable> list = callbacks;
            callbacks = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                final Runnable runnable = list.get(i);
                if (runnable != null) {
                    runnable.run();
                } else {

                }
            }
        }
    }

    private static EventBus getInstance() {
        if (instance == null) {
            try {
                initLock.lock();
                if (instance == null) {
                    instance = VetrxHolder.getInstance().eventBus();
                    instance.registerDefaultCodec(EventCallbackParam.class, new EventCodec());
                }
            } catch (IllegalStateException iie) {
                //throw then codec allready registred
            } finally {
                initLock.unlock();
            }
        }
        return instance;
    }

    public static void clear() {
        getInstance().close(null);
    }


    private static class EventCodec implements MessageCodec<EventCallbackParam, EventCallbackParam> {
        @Override
        public void encodeToWire(Buffer buffer, EventCallbackParam o) {

        }

        @Override
        public EventCallbackParam decodeFromWire(int i, Buffer buffer) {
            return null;
        }

        @Override
        public EventCallbackParam transform(EventCallbackParam o) {
            return o;
        }

        @Override
        public String name() {
            return "default-codec";
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }
}
