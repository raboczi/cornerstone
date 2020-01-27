package au.id.raboczi.cornerstone.test_service.zk;

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.test_service.TestService;
import au.id.raboczi.cornerstone.zk.Reference;
import au.id.raboczi.cornerstone.zk.SCRSelectorComposer;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.observers.DefaultObserver;
import java.util.Hashtable;
import org.osgi.service.event.Event;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

/**
 * Controller for the macro <code>test.zul</code>.
 */
public final class TestController extends SCRSelectorComposer<Component> implements EventListener<ConsumerEvent<String>> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(TestController.class);

    /** The name of the ZK queue. */
    private static final String QUEUE = "consumer-queue-name";

    /** The model for this controller. */
    @Reference
    private TestService testService;

    /** The view for this controller. */
    @Wire("#label")
    private Label label;

    @Override
    public void doAfterCompose(final Component comp) throws Exception {
        super.doAfterCompose(comp);

        label.setValue(testService.getValue());

        // ZK events
        EventQueue eventQueue = EventQueues.lookup(QUEUE, comp.getDesktop().getSession(), true);
        LOGGER.info("Do after compose, window " + comp + ", desktop " + getSelf().getDesktop() + ", session " + getSelf().getDesktop().getSession() + ", queue " + eventQueue);
        assert eventQueue != null;
        eventQueue.subscribe(this);

        // OSGi events
        connect(TestService.EVENT_TOPIC, eventQueue, (Consumer<String>) s -> label.setValue(s));

        // RxJava events
        Caller caller = null;
        connect(testService.getObservableValue(caller), eventQueue, s -> label.setValue(s));
    }

    /**
     * Forwards RxJava ObservableSource as ZK events.
     *
     * @param <T>  the type of stream
     * @param observableSource  the source
     * @param eventQueue  the channel
     * @param consumer  the destination
     */
    private <T> void connect(final ObservableSource<T> observableSource, final EventQueue eventQueue, final Consumer<T> consumer) {
        observableSource.subscribe(new DefaultObserver<T>() {
            @Override public void onStart() {
                LOGGER.info("Start!");
            }

            @Override public void onNext(final T t) {
                LOGGER.info("Handle RxJava change: " + t);
                eventQueue.publish(new ConsumerEvent(t, consumer));
            }

            @Override public void onError(final Throwable t) {
                LOGGER.error("Test service exception", t);
            }
            @Override public void onComplete() {
                LOGGER.info("Done!");
            }
        });
    }

    /**
     * Forwards OSGi EventAdmin traffic as ZK events.
     *
     * @param <T>  the type of stream
     * @param topic  the source, the name of an OSGi EventAdmin queue
     * @param eventQueue  the channel
     * @param consumer  the destination
     */
    private <T> void connect(final String topic, final EventQueue eventQueue, final Consumer<T> consumer) {

        Hashtable ht = new Hashtable();
        ht.put(EVENT_TOPIC, new String[] {topic});

        getBundleContext().registerService(EventHandler.class.getName(), new EventHandler() {
            @Override
            public void handleEvent(final Event event) {
                LOGGER.info("Handle OSGi event: " + event);

                switch (event.getTopic()) {
                case TestService.EVENT_TOPIC:
                    eventQueue.publish(new ConsumerEvent<T>((T) event.getProperty("value"), consumer));
                    break;

                default:
                    LOGGER.warn("Unsupported topic: " + event.getTopic());
                    break;
                }
            }
        }, ht);
    }

    /** @param mouseEvent  button click */
    @Listen("onClick = button#button1; onClick = button#button2")
    public void onClickButton(final MouseEvent mouseEvent) {
        testService.setValue(((Button) mouseEvent.getTarget()).getLabel());
    }


    // Implementation of ZK EventListener<ConsumerEvent<String>>

    @Override
    public void onEvent(final ConsumerEvent<String> consumerEvent) throws Exception {
        LOGGER.info("Handle ZK event " + consumerEvent);
        consumerEvent.run();
    }
}
