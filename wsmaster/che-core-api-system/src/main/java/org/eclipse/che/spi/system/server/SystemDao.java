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
package org.eclipse.che.spi.system.server;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.spi.system.server.model.impl.SystemPropertyImpl;

/**
 * Defines data access object contract for {@link SystemPropertyImpl}.
 *
 * @author Anton Korneta
 */
public interface SystemDao {

    /**
     * Saves system properties.
     * <p> Note that if property with given name already exists
     * then the new value will be set.
     *
     * @param property
     *         system property to set
     * @throws NullPointerException
     *         when property {@code name} is not specified
     * @throws ServerException
     *         when any other error occurs during property setup
     */
    void saveProperty(SystemPropertyImpl property) throws ServerException;

    /**
     * Removes system property by name.
     *
     * @param name
     *         system property name
     * @throws NullPointerException
     *         when property {@code name} is not specified
     * @throws ServerException
     *         when any other error occurs during property removing
     */
    void removeProperty(String name) throws ServerException;

    /**
     * Gets system property by name.
     *
     * @param name
     *         system property name
     * @return property with specified {@code name}
     * @throws NullPointerException
     *         when property {@code name} is not specified
     * @throws NotFoundException
     *         when property with specified {@code name} doesn't exist
     * @throws ServerException
     *         when any other error occurs during property fetching
     */
    SystemPropertyImpl getProperty(String name) throws NotFoundException, ServerException;

}
