/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.thxsoft.vds.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResLoadUserProfiles implements org.apache.thrift.TBase<ResLoadUserProfiles, ResLoadUserProfiles._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResLoadUserProfiles");

  private static final org.apache.thrift.protocol.TField ERROR_FIELD_DESC = new org.apache.thrift.protocol.TField("error", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField USER_PROFILES_FIELD_DESC = new org.apache.thrift.protocol.TField("userProfiles", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ResLoadUserProfilesStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ResLoadUserProfilesTupleSchemeFactory());
  }

  public ErrorDesc error; // required
  public List<UserProfile> userProfiles; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ERROR((short)1, "error"),
    USER_PROFILES((short)2, "userProfiles");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ERROR
          return ERROR;
        case 2: // USER_PROFILES
          return USER_PROFILES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ERROR, new org.apache.thrift.meta_data.FieldMetaData("error", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ErrorDesc.class)));
    tmpMap.put(_Fields.USER_PROFILES, new org.apache.thrift.meta_data.FieldMetaData("userProfiles", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, UserProfile.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResLoadUserProfiles.class, metaDataMap);
  }

  public ResLoadUserProfiles() {
  }

  public ResLoadUserProfiles(
    ErrorDesc error,
    List<UserProfile> userProfiles)
  {
    this();
    this.error = error;
    this.userProfiles = userProfiles;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResLoadUserProfiles(ResLoadUserProfiles other) {
    if (other.isSetError()) {
      this.error = new ErrorDesc(other.error);
    }
    if (other.isSetUserProfiles()) {
      List<UserProfile> __this__userProfiles = new ArrayList<UserProfile>();
      for (UserProfile other_element : other.userProfiles) {
        __this__userProfiles.add(new UserProfile(other_element));
      }
      this.userProfiles = __this__userProfiles;
    }
  }

  public ResLoadUserProfiles deepCopy() {
    return new ResLoadUserProfiles(this);
  }

  @Override
  public void clear() {
    this.error = null;
    this.userProfiles = null;
  }

  public ErrorDesc getError() {
    return this.error;
  }

  public ResLoadUserProfiles setError(ErrorDesc error) {
    this.error = error;
    return this;
  }

  public void unsetError() {
    this.error = null;
  }

  /** Returns true if field error is set (has been assigned a value) and false otherwise */
  public boolean isSetError() {
    return this.error != null;
  }

  public void setErrorIsSet(boolean value) {
    if (!value) {
      this.error = null;
    }
  }

  public int getUserProfilesSize() {
    return (this.userProfiles == null) ? 0 : this.userProfiles.size();
  }

  public java.util.Iterator<UserProfile> getUserProfilesIterator() {
    return (this.userProfiles == null) ? null : this.userProfiles.iterator();
  }

  public void addToUserProfiles(UserProfile elem) {
    if (this.userProfiles == null) {
      this.userProfiles = new ArrayList<UserProfile>();
    }
    this.userProfiles.add(elem);
  }

  public List<UserProfile> getUserProfiles() {
    return this.userProfiles;
  }

  public ResLoadUserProfiles setUserProfiles(List<UserProfile> userProfiles) {
    this.userProfiles = userProfiles;
    return this;
  }

  public void unsetUserProfiles() {
    this.userProfiles = null;
  }

  /** Returns true if field userProfiles is set (has been assigned a value) and false otherwise */
  public boolean isSetUserProfiles() {
    return this.userProfiles != null;
  }

  public void setUserProfilesIsSet(boolean value) {
    if (!value) {
      this.userProfiles = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ERROR:
      if (value == null) {
        unsetError();
      } else {
        setError((ErrorDesc)value);
      }
      break;

    case USER_PROFILES:
      if (value == null) {
        unsetUserProfiles();
      } else {
        setUserProfiles((List<UserProfile>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ERROR:
      return getError();

    case USER_PROFILES:
      return getUserProfiles();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ERROR:
      return isSetError();
    case USER_PROFILES:
      return isSetUserProfiles();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResLoadUserProfiles)
      return this.equals((ResLoadUserProfiles)that);
    return false;
  }

  public boolean equals(ResLoadUserProfiles that) {
    if (that == null)
      return false;

    boolean this_present_error = true && this.isSetError();
    boolean that_present_error = true && that.isSetError();
    if (this_present_error || that_present_error) {
      if (!(this_present_error && that_present_error))
        return false;
      if (!this.error.equals(that.error))
        return false;
    }

    boolean this_present_userProfiles = true && this.isSetUserProfiles();
    boolean that_present_userProfiles = true && that.isSetUserProfiles();
    if (this_present_userProfiles || that_present_userProfiles) {
      if (!(this_present_userProfiles && that_present_userProfiles))
        return false;
      if (!this.userProfiles.equals(that.userProfiles))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(ResLoadUserProfiles other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ResLoadUserProfiles typedOther = (ResLoadUserProfiles)other;

    lastComparison = Boolean.valueOf(isSetError()).compareTo(typedOther.isSetError());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetError()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.error, typedOther.error);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserProfiles()).compareTo(typedOther.isSetUserProfiles());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserProfiles()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userProfiles, typedOther.userProfiles);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ResLoadUserProfiles(");
    boolean first = true;

    sb.append("error:");
    if (this.error == null) {
      sb.append("null");
    } else {
      sb.append(this.error);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("userProfiles:");
    if (this.userProfiles == null) {
      sb.append("null");
    } else {
      sb.append(this.userProfiles);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (error != null) {
      error.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ResLoadUserProfilesStandardSchemeFactory implements SchemeFactory {
    public ResLoadUserProfilesStandardScheme getScheme() {
      return new ResLoadUserProfilesStandardScheme();
    }
  }

  private static class ResLoadUserProfilesStandardScheme extends StandardScheme<ResLoadUserProfiles> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ResLoadUserProfiles struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ERROR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.error = new ErrorDesc();
              struct.error.read(iprot);
              struct.setErrorIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // USER_PROFILES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list16 = iprot.readListBegin();
                struct.userProfiles = new ArrayList<UserProfile>(_list16.size);
                for (int _i17 = 0; _i17 < _list16.size; ++_i17)
                {
                  UserProfile _elem18; // required
                  _elem18 = new UserProfile();
                  _elem18.read(iprot);
                  struct.userProfiles.add(_elem18);
                }
                iprot.readListEnd();
              }
              struct.setUserProfilesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ResLoadUserProfiles struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.error != null) {
        oprot.writeFieldBegin(ERROR_FIELD_DESC);
        struct.error.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.userProfiles != null) {
        oprot.writeFieldBegin(USER_PROFILES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.userProfiles.size()));
          for (UserProfile _iter19 : struct.userProfiles)
          {
            _iter19.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ResLoadUserProfilesTupleSchemeFactory implements SchemeFactory {
    public ResLoadUserProfilesTupleScheme getScheme() {
      return new ResLoadUserProfilesTupleScheme();
    }
  }

  private static class ResLoadUserProfilesTupleScheme extends TupleScheme<ResLoadUserProfiles> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ResLoadUserProfiles struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetError()) {
        optionals.set(0);
      }
      if (struct.isSetUserProfiles()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetError()) {
        struct.error.write(oprot);
      }
      if (struct.isSetUserProfiles()) {
        {
          oprot.writeI32(struct.userProfiles.size());
          for (UserProfile _iter20 : struct.userProfiles)
          {
            _iter20.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ResLoadUserProfiles struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.error = new ErrorDesc();
        struct.error.read(iprot);
        struct.setErrorIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list21 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.userProfiles = new ArrayList<UserProfile>(_list21.size);
          for (int _i22 = 0; _i22 < _list21.size; ++_i22)
          {
            UserProfile _elem23; // required
            _elem23 = new UserProfile();
            _elem23.read(iprot);
            struct.userProfiles.add(_elem23);
          }
        }
        struct.setUserProfilesIsSet(true);
      }
    }
  }

}

