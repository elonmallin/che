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
package org.eclipse.che.spi.system.server.tck;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.commons.test.tck.TckListener;
import org.eclipse.che.commons.test.tck.repository.TckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepositoryException;
import org.eclipse.che.spi.system.server.SystemPropertyDao;
import org.eclipse.che.spi.system.server.model.impl.SystemPropertyImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;
import static org.testng.Assert.assertEquals;

/**
 * Tests {@link SystemPropertyDao} contract.
 *
 * @author Anton Korneta
 */
@Listeners(TckListener.class)
@Test(suiteName = SystemPropertyDaoTest.SUITE_NAME)
public class SystemPropertyDaoTest {

    public static final String SUITE_NAME = "SystemDaoTck";

    private static final int COUNT_OF_PROPERTIES = 5;

    @Inject
    private TckRepository<SystemPropertyImpl> systemRepo;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    private SystemPropertyImpl[] systemProperties;

    @AfterMethod
    public void removeEntities() throws TckRepositoryException {
        systemRepo.removeAll();
    }

    @BeforeMethod
    public void createEntities() throws TckRepositoryException {
        systemProperties = new SystemPropertyImpl[COUNT_OF_PROPERTIES];
        for (int i = 0; i < COUNT_OF_PROPERTIES; i++) {
            systemProperties[i] = createProperty("sys.property-" + i, "value-" + i);
        }
        systemRepo.createAll(Arrays.asList(systemProperties));
    }

    private SystemPropertyImpl createProperty(String name, String value) {
        return new SystemPropertyImpl(name, value, currentTimeMillis());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeWhenSystemPropertyToSaveIsNull() throws Exception {
        systemPropertyDao.save(null);
    }

    @Test
    public void savesNewSystemProperty() throws Exception {
        systemPropertyDao.save(new SystemPropertyImpl("test", "test", 3L));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeWhenSystemPropertyNameToRemoveIsNull() throws Exception {
        systemPropertyDao.remove(null);
    }

    @Test(dependsOnMethods = "getsSystemProperty", expectedExceptions = NotFoundException.class)
    public void removesSystemProperty() throws Exception {
        final String name = systemProperties[0].getName();

        systemPropertyDao.remove(name);
        systemPropertyDao.get(name);
    }

    @Test(dependsOnMethods = "getsSystemProperty")
    public void updatesSystemPropertyWhenPropertyWithGivenNameAlreadyExists() throws Exception {
        systemPropertyDao.save(new SystemPropertyImpl(systemProperties[0].getName(), "test", 3L));
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void throwsNpeWhenGettingSystemPropertyByNullName() throws Exception {
        systemPropertyDao.get(null);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void throwsNotFoundExceptionWhenGettingNonExistingProperty() throws Exception {
        systemPropertyDao.get("non-existing");
    }

    @Test
    public void getsSystemProperty() throws Exception {
        final SystemPropertyImpl systemProperty = systemProperties[0];

        assertEquals(systemProperty, systemPropertyDao.get(systemProperties[0].getName()));
    }

}
