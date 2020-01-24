package au.id.raboczi.cornerstone.test_service.zk;

import io.reactivex.rxjava3.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;

/** Custom ZK event corresponding to the testService's observable transitions. */
public class ConsumerEvent<T> extends Event {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerEvent.class);

    private T t;

    private Consumer<T> consumer;

    public ConsumerEvent(T t, Consumer<T> consumer) {
        super("consumer-event-name");
        this.t = t;
        this.consumer = consumer;
    }

    void run() {
        try {
            consumer.accept(t);

        } catch (Throwable t) {
            LOGGER.error("Failed consumer event", t);
        }
    }
}
