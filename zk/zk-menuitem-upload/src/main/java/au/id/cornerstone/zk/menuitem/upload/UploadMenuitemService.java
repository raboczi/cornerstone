package au.id.raboczi.cornerstone.zk.menuitem.theme;

/*-
 * #%L
 * Cornerstone :: ZK menu item :: Upload
 * %%
 * Copyright (C) 2019 - 2020 Simon Raboczi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import au.id.raboczi.cornerstone.zk.Users;
import au.id.raboczi.cornerstone.zk.MenuitemService;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Menuitem;

/**
 * Menu item for file upload.
 */
@Component(service = MenuitemService.class,
           property = {"menubar=main"})
public final class UploadMenuitemService implements MenuitemService {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadMenuitemService.class);

    /** Menu path. */
    private static final String[] PATH = {"file"};

    /** Authorization lookup service. */
    @Reference
    private @Nullable UserAdmin userAdmin;

    @Override
    public String[] getPath() {
        return PATH;
    }

    @Override
    @SuppressWarnings("MagicNumber")
    public Menuitem newMenuitem() {
        ResourceBundle labels = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent());
        Menuitem menuitem = new Menuitem(labels.getString("menuitem.label"));
        menuitem.addEventListener("onClick", event -> {
            Map<String, Object> params = new HashMap<>();
            Fileupload.get(params, labels.getString("fileupload.message"), labels.getString("fileupload.title"),
                "*",      // accept content type
                1000,     // maximum simultaneous uploads
                1000000,  // maximum upload size (kilobytes)
                false,    // don't override content type to always be binary
                uploadEvent -> {
                    LOGGER.info("Uploaded {}", uploadEvent);
                    Media[] medias = uploadEvent.getMedias();
                    if (medias == null) {
                        LOGGER.info("User uploaded zero files");
                        return;
                    }
                    for (Media media: medias) {
                        LOGGER.info("User uploaded {}", media);
                    }
                }
            );
        });

        // Initialize the enabled/disabled status
        assert userAdmin != null : "@AssumeAssertion(nullness)";
        boolean notManager = !Users.getCaller(userAdmin).authorization().hasRole("manager");
        menuitem.setDisabled(notManager);

        // Reactively maintain the enabled/disabled status
        Users.observable().subscribe(user -> {
            assert userAdmin != null : "@AssumeAssertion(nullness)";
            boolean notManager2 = !Users.getCaller(userAdmin).authorization().hasRole("manager");
            menuitem.setDisabled(notManager2);
        });

        return menuitem;
    }
}
