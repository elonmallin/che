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
package org.eclipse.che.spi.system.server.model.impl;

import org.eclipse.che.api.system.shared.SystemProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Anton Korneta
 */
@Entity(name = "SystemProperty")
@Table(name = "che_system_property")
public class SystemPropertyImpl implements SystemProperty {

    @Id
    @Column(name = "property_name", nullable = false)
    private String name;

    @Column(name = "property_value", nullable = false)
    private String value;

    @Column(name = "property_modified_date", nullable = false)
    private Long modifiedDate;

    public SystemPropertyImpl() {}

    public SystemPropertyImpl(SystemProperty property) {
        this(property.getName(),
             property.getValue(),
             property.getModifiedDate());
    }

    public SystemPropertyImpl(String name,
                              String value,
                              Long modifiedDate) {
        this.name = name;
        this.value = value;
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SystemPropertyImpl)) {
            return false;
        }
        final SystemPropertyImpl that = (SystemPropertyImpl)obj;
        return Objects.equals(name, that.name)
               && Objects.equals(value, that.value)
               && Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(name);
        hash = 31 * hash + Objects.hashCode(value);
        hash = 31 * hash + Objects.hashCode(modifiedDate);
        return hash;
    }

    @Override
    public String toString() {
        return "SystemPropertyImpl{" +
               "name='" + name + '\'' +
               ", value='" + value + '\'' +
               ", modifiedDate=" + modifiedDate +
               '}';
    }

}
