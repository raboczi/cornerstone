package au.id.raboczi.cornerstone.test_war;

import au.id.raboczi.cornerstone.test_service.TestService;
import io.reactivex.rxjava3.core.*;
import java.util.Hashtable;
import org.osgi.service.event.Event;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * Controller for <code>index.zul</code>.
 */
public class IndexWindowController extends SCRSelectorComposer<Window> implements EventHandler, EventListener<IndexWindowController.WeirdEvent> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(IndexWindowController.class);

    @Reference
    private TestService testService;

    @Wire("#label")
    private Label label;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        label.setValue(testService.getValue());

        // OSGi events
        String[] topics = new String[] {TestService.EVENT_TOPIC};
        Hashtable ht = new Hashtable();
        ht.put(EventConstants.EVENT_TOPIC, topics);
        getBundleContext().registerService(EventHandler.class.getName(), this, ht);

        // ZK events
        EventQueue eventQueue = EventQueues.lookup("weird", getSelf().getDesktop().getSession(), true);
        eventQueue.subscribe(this);

        // RxJava events
        testService.getObservable().subscribe(s -> eventQueue.publish(new WeirdEvent(s)),
                                              e -> LOGGER.error("Test service exception", e));
    }

    /** @param event  button click */
    @Listen("onClick = button#button1; onClick = button#button2")
    public void onClickButton(final MouseEvent mouseEvent) {
        testService.setValue(((Button) mouseEvent.getTarget()).getLabel());
        label.setValue(testService.getValue());
    }


    // Implementation of OSGi EventHandler

    @Override
    public void handleEvent(final Event event) {
        LOGGER.info("Handle OSGi event " + event);
        //EventQueues.lookup("q", EventQueues.APPLICATION, true).publish(
        //    new org.zkoss.zk.ui.event.Event(event.getTopic()));
    }


    // Implementation of ZK EventListener<WeirdEvent>

    @Override
    public void onEvent(WeirdEvent weirdEvent) {
        label.setValue(weirdEvent.getName());
    }

    /** Custom ZK event corresponding to the testService's observable transitions. */
    class WeirdEvent extends org.zkoss.zk.ui.event.Event {
        WeirdEvent(String s) {
            super(s);
        }
    }
}
