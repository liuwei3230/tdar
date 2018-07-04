package org.tdar.core.serialize.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.entity.AddressType;
import org.tdar.core.serialize.PAbstractPersistable;

/**
 * Represents a physical address for a person or institution.
 * 
 * @author abrin
 * 
 */
public class PAddress extends PAbstractPersistable implements Persistable {

    private String street1;
    private String street2;
    private String city;
    private String state;
    private String postal;
    private String country;

    private AddressType type;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    @Override
    public String toString() {
        return String.format("# %s : %s, %s [%s, %s, %s, %s] %s", getId(), street1, street2, city, state, postal, country, type);
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public PAddress() {
    }

    public PAddress(AddressType type, String street1, String city, String state, String postal, String country) {
        this.type = type;
        this.street1 = street1;
        this.state = state;
        this.city = city;
        this.postal = postal;
        this.country = country;
    }

    public String getAddressSingleLine() {
        StringBuilder sb = new StringBuilder(getStreet1());
        if ((sb.length() > 0) && StringUtils.isNotBlank(getStreet2())) {
            sb.append(" ").append(getStreet2());
        }
        if (sb.length() > 0) {
            sb.append(". ");
        }
        sb.append(getCity()).append(", ").append(getState()).append(" ").append(getPostal());
        if (StringUtils.isNotBlank(getCountry())) {
            sb.append(". ").append(getCountry());
        }
        sb.append("(").append(getType().getLabel()).append(")");
        return sb.toString();
    }

    public boolean isSameAs(PAddress address) {
        return getHashCodeForComparison() == address.getHashCodeForComparison();
    }

    private int getHashCodeForComparison() {
        HashCodeBuilder builder = new HashCodeBuilder(1, 7);
        builder.append(getStreet1()).append(getStreet2()).append(getCity()).append(getState()).append(getPostal()).append(getCountry());
        return builder.toHashCode();
    }
}
