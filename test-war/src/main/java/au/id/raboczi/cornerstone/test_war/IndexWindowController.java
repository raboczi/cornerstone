package au.id.raboczi.cornerstone.test_war;

import au.id.raboczi.cornerstone.test_service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * Controller for <code>index.zul</code>.
 */
public class IndexWindowController extends SCRSelectorComposer<Window> {

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
    }

    /** @param event  button click */
    @Listen("onClick = button#button1; onClick = button#button2")
    public void onClickButton(final MouseEvent mouseEvent) {
        testService.setValue(((Button) mouseEvent.getTarget()).getLabel());
        label.setValue(testService.getValue());
    }
}
