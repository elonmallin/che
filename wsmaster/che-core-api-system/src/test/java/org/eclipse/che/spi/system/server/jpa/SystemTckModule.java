/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.spi.system.server.jpa;

import com.google.inject.TypeLiteral;

import org.eclipse.che.commons.test.db.H2DBTestServer;
import org.eclipse.che.commons.test.db.H2JpaCleaner;
import org.eclipse.che.commons.test.db.PersistTestModuleBuilder;
import org.eclipse.che.commons.test.tck.TckModule;
import org.eclipse.che.commons.test.tck.TckResourcesCleaner;
import org.eclipse.che.commons.test.tck.repository.JpaTckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepository;
import org.eclipse.che.core.db.DBInitializer;
import org.eclipse.che.core.db.h2.jpa.eclipselink.H2ExceptionHandler;
import org.eclipse.che.core.db.schema.SchemaInitializer;
import org.eclipse.che.core.db.schema.impl.flyway.FlywaySchemaInitializer;
import org.eclipse.che.spi.system.server.SystemPropertyDao;
import org.eclipse.che.spi.system.server.model.impl.SystemPropertyImpl;
import org.h2.Driver;

/**
 * @author Anton Korneta
 */
public class SystemTckModule extends TckModule {

    @Override
    protected void configure() {
        final H2DBTestServer server = H2DBTestServer.startDefault();
        install(new PersistTestModuleBuilder().setDriver(Driver.class)
                                              .runningOn(server)
                                              .addEntityClasses(SystemPropertyImpl.class)
                                              .setExceptionHandler(H2ExceptionHandler.class)
                                              .build());
        bind(DBInitializer.class).asEagerSingleton();
        bind(SchemaInitializer.class).toInstance(new FlywaySchemaInitializer(server.getDataSource(), "che-schema"));
        bind(TckResourcesCleaner.class).toInstance(new H2JpaCleaner(server));

        bind(new TypeLiteral<TckRepository<SystemPropertyImpl>>() {})
                .toInstance(new JpaTckRepository<>(SystemPropertyImpl.class));

        bind(SystemPropertyDao.class).to(JpaSystemPropertyDao.class);
    }

}
