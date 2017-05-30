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

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.spi.system.server.SystemPropertyDao;
import org.eclipse.che.spi.system.server.model.impl.SystemPropertyImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * JPA based implementation of {@link SystemPropertyDao}.
 *
 * @author Anton Korneta
 */
@Singleton
public class JpaSystemPropertyDao implements SystemPropertyDao {

    @Inject
    private EventService            eventService;
    @Inject
    private Provider<EntityManager> managerProvider;

    @Override
    public void save(SystemPropertyImpl property) throws ServerException {
        requireNonNull(property);
        try {
            doCreate(property);
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void remove(String name) throws ServerException {
        requireNonNull(name);
        try {
            doRemove(name);
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    @Transactional
    public SystemPropertyImpl get(String name) throws NotFoundException, ServerException {
        requireNonNull(name);
        try {
            final EntityManager manager = managerProvider.get();
            final SystemPropertyImpl property = manager.find(SystemPropertyImpl.class, name);
            if (property == null) {
                throw new NotFoundException(format("System property with name '%s' doesn't exist", name));
            }
            return property;
        } catch (RuntimeException ex) {
            throw new ServerException(ex.getLocalizedMessage(), ex);
        }
    }

    @Transactional(rollbackOn = {RuntimeException.class, ServerException.class})
    protected void doRemove(String name) throws ServerException {
        final EntityManager manager = managerProvider.get();
        final SystemPropertyImpl property = manager.find(SystemPropertyImpl.class, name);
        if (property != null) {
            manager.remove(property);
            manager.flush();
        }
    }

    @Transactional
    protected void doCreate(SystemPropertyImpl property) throws ServerException {
        managerProvider.get().merge(property);
        managerProvider.get().flush();
    }

}
