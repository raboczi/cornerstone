package au.id.raboczi.cornerstone.test_service.impl;

/*-
 * #%L
 * Cornerstone :: Test service
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

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.test_service.RxTestService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import static java.util.concurrent.TimeUnit.SECONDS;
import org.osgi.service.component.annotations.Component;

/**
 * Demonstrates RxJava event handling.
 */
@Component(service = {RxTestService.class})
public final class RxTestServiceImpl implements RxTestService {

    /** Timer frequency in seconds. */
    private static final long PERIOD = 3;

    /** A counter that increments every {@link #PERIOD} seconds. */
    private ObservableSource<String> observableValue = Observable.interval(PERIOD, SECONDS).map(n -> n.toString());

    @Override
    public ObservableSource<String> getObservableValue(final Caller caller) {
        return observableValue;
    }
}
