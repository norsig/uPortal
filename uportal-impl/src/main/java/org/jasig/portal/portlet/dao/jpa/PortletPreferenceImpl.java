/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.portlet.dao.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.pluto.descriptors.portlet.PortletPreferenceDD;
import org.apache.pluto.internal.InternalPortletPreference;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.jasig.portal.portlet.om.IPortletPreference;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@Entity
@Table(name = "UP_PORTLET_PREF")
@GenericGenerator(
        name = "UP_PORTLET_PREF_GEN", 
        strategy = "native", 
        parameters = {
            @Parameter(name = "sequence", value = "UP_PORTLET_PREF_SEQ"),
            @Parameter(name = "table", value = "UP_JPA_UNIQUE_KEY"),
            @Parameter(name = "column", value = "NEXT_UP_PORTLET_PREF_HI")
        }
    )
public class PortletPreferenceImpl implements IPortletPreference {
    @Id
    @GeneratedValue(generator = "UP_PORTLET_PREF_GEN")
    @Column(name = "PORTLET_PREF_ID")
    private final long portletPreferenceId;
    
    @Column(name = "NAME", nullable = false)
    @Type(type = "nullSafeText")
    private String name = null;
    
    @Column(name = "READ_ONLY", nullable = false)
    private boolean readOnly = false;
    
    @CollectionOfElements(fetch = FetchType.EAGER)
    @JoinTable(
        name = "UP_PORTLET_PREF_VALUES",
        joinColumns = @JoinColumn(name = "PORTLET_PREF_ID")
    )
    @IndexColumn(name = "VALUE_ORDER")
    @Type(type = "nullSafeText")
    @Column(name = "VALUE")
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN, org.hibernate.annotations.CascadeType.ALL })
    private List<String> values = null;
    
    
    public PortletPreferenceImpl() {
        this.portletPreferenceId = -1;
    }
    
    public PortletPreferenceImpl(InternalPortletPreference portletPreference) {
        this.portletPreferenceId = -1;
        this.name = portletPreference.getName();
        this.readOnly = portletPreference.isReadOnly();
        this.setValues(portletPreference.getValues());
    }
    
    public PortletPreferenceImpl(PortletPreferenceDD portletPreference) {
        this.portletPreferenceId = -1;
        this.name = portletPreference.getName();
        this.readOnly = portletPreference.isReadOnly();

        final List<String> values = portletPreference.getValues();
        if (values != null) {
            this.setValues(values.toArray(new String[values.size()]));
        }
    }
    
    public PortletPreferenceImpl(String name, boolean readOnly, String... values) {
        this.portletPreferenceId = -1;
        this.name = name;
        this.readOnly = readOnly;
        this.setValues(values);
    }

    
    /* (non-Javadoc)
     * @see org.jasig.portal.om.portlet.prefs.InternalPortletPreference#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.om.portlet.prefs.InternalPortletPreference#getValues()
     */
    public String[] getValues() {
        if (this.values == null) {
            return null;
        }

        return this.values.toArray(new String[this.values.size()]);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.om.portlet.prefs.InternalPortletPreference#isReadOnly()
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.om.IPortletPreference#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.om.portlet.prefs.InternalPortletPreference#setValues(java.lang.String[])
     */
    public void setValues(String[] values) {
        if (values == null) {
            this.values = null;
        }
        else if (this.values == null) {
            this.values = new ArrayList<String>(Arrays.asList(values));
        }
        else {
            this.values.clear();
            this.values.addAll(Arrays.asList(values));
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        return new PortletPreferenceImpl(this);
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof IPortletPreference)) {
            return false;
        }
        IPortletPreference rhs = (IPortletPreference) object;
        return new EqualsBuilder()
            .append(this.name, rhs.getName())
            .append(this.readOnly, rhs.isReadOnly())
            .append(this.getValues(), rhs.getValues())
            .isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(-1904185833, -1222355625)
            .append(this.name)
            .append(this.readOnly)
            .append(this.values)
            .toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("portletPreferenceId", this.portletPreferenceId)
            .append("name", this.name)
            .append("readOnly", this.readOnly)
            .append("values", this.values)
            .toString();
    }
}