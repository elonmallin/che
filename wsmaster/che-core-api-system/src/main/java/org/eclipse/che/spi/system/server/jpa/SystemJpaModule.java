package org.eclipse.che.spi.system.server.jpa;

import com.google.inject.AbstractModule;

import org.eclipse.che.spi.system.server.SystemDao;

/**
 * @author Anton Korneta
 */
public class SystemJpaModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SystemDao.class).to(JpaSystemDao.class);
    }
}
