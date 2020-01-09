package au.id.raboczi.cornerstone.test_war;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.theme.Themes;

/**
 * Populate a listbox with the available ZK themes.
 *
 * Selecting a list item will switch themes.
 */
public class ThemeListboxController extends SelectorComposer<Listbox> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ThemeListboxController.class);

    /** Key for theme name on {@link #Listitem}s. */
    private static final String THEME_ATTRIBUTE = "theme.name";

    /** {@inheritDoc} */
    @Override
    public void doFinally() {
        ListModelArray<String> model = new ListModelArray<>(Themes.getThemes());
        model.sort((a, b) ->
            Themes.getDisplayName(a).compareTo(Themes.getDisplayName(b)), true);
        model.addToSelection(Themes.getCurrentTheme());
        getSelf().setModel(model);
        getSelf().setItemRenderer(new ListitemRenderer<String>() {
            public void render(final Listitem listitem,
                               final String   theme,
                               final int      index) {

                listitem.setAttribute(THEME_ATTRIBUTE, theme);
                listitem.setLabel(Themes.getDisplayName(theme));
            }
        });
    }

    /** @param selectEvent  theme selected */
    @Listen("onSelect = #themeListbox")
    @SuppressWarnings("nullness")  // Themes.setTheme not annotated
    public void onSelectTheme(final SelectEvent<Listitem, String> selectEvent) {
        String newTheme = (String) selectEvent.getReference()
                                              .getAttribute(THEME_ATTRIBUTE);
        LOGGER.info(String.format("Changed ZK theme: %s", newTheme));
        Themes.setTheme(Executions.getCurrent(), newTheme);
        Executions.sendRedirect("");
    }
}
