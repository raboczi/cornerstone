package au.id.raboczi.cornerstone.test_service.zk;

import io.reactivex.rxjava3.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;

/**
 * Custom ZK event corresponding to the testService's observable transitions.
 *
 * @param <T>  type of object to be consumed
 */
public class ConsumerEvent<T> extends Event {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerEvent.class);

    /** An object to be consumed. */
    private T t;

    /** A consumer. */
    private Consumer<T> consumer;

    /**
     * @param newT  an object to be consumed
     * @param newConsumer  a consumer
     */
    public ConsumerEvent(final T newT, final Consumer<T> newConsumer) {
        super("consumer-event-name");
        this.t = newT;
        this.consumer = newConsumer;
    }

    /** Consume the object. */
    void run() {
        try {
            consumer.accept(t);

        } catch (Throwable throwable) {
            LOGGER.error("Failed consumer event", throwable);
        }
    }
}
