package au.id.raboczi.cornerstone.test_war;

import au.id.raboczi.cornerstone.test_service.TestService;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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

    /** @param event  button click */
    @Listen("onClick = #button")
    public void onClickButton(final MouseEvent mouseEvent) throws org.osgi.framework.InvalidSyntaxException {
        LOGGER.info("Pressed button");
        TestService testService2 = findService(TestService.class);
        label.setValue(testService2.test("Changed value."));
    }
}
