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

public class ResRequestFriend implements org.apache.thrift.TBase<ResRequestFriend, ResRequestFriend._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResRequestFriend");

  private static final org.apache.thrift.protocol.TField ERROR_FIELD_DESC = new org.apache.thrift.protocol.TField("error", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField FRIEND_PROFILE_FIELD_DESC = new org.apache.thrift.protocol.TField("friendProfile", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ResRequestFriendStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ResRequestFriendTupleSchemeFactory());
  }

  public ErrorDesc error; // required
  public UserProfile friendProfile; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ERROR((short)1, "error"),
    FRIEND_PROFILE((short)2, "friendProfile");

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
        case 2: // FRIEND_PROFILE
          return FRIEND_PROFILE;
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
    tmpMap.put(_Fields.FRIEND_PROFILE, new org.apache.thrift.meta_data.FieldMetaData("friendProfile", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, UserProfile.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResRequestFriend.class, metaDataMap);
  }

  public ResRequestFriend() {
  }

  public ResRequestFriend(
    ErrorDesc error,
    UserProfile friendProfile)
  {
    this();
    this.error = error;
    this.friendProfile = friendProfile;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResRequestFriend(ResRequestFriend other) {
    if (other.isSetError()) {
      this.error = new ErrorDesc(other.error);
    }
    if (other.isSetFriendProfile()) {
      this.friendProfile = new UserProfile(other.friendProfile);
    }
  }

  public ResRequestFriend deepCopy() {
    return new ResRequestFriend(this);
  }

  @Override
  public void clear() {
    this.error = null;
    this.friendProfile = null;
  }

  public ErrorDesc getError() {
    return this.error;
  }

  public ResRequestFriend setError(ErrorDesc error) {
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

  public UserProfile getFriendProfile() {
    return this.friendProfile;
  }

  public ResRequestFriend setFriendProfile(UserProfile friendProfile) {
    this.friendProfile = friendProfile;
    return this;
  }

  public void unsetFriendProfile() {
    this.friendProfile = null;
  }

  /** Returns true if field friendProfile is set (has been assigned a value) and false otherwise */
  public boolean isSetFriendProfile() {
    return this.friendProfile != null;
  }

  public void setFriendProfileIsSet(boolean value) {
    if (!value) {
      this.friendProfile = null;
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

    case FRIEND_PROFILE:
      if (value == null) {
        unsetFriendProfile();
      } else {
        setFriendProfile((UserProfile)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ERROR:
      return getError();

    case FRIEND_PROFILE:
      return getFriendProfile();

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
    case FRIEND_PROFILE:
      return isSetFriendProfile();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResRequestFriend)
      return this.equals((ResRequestFriend)that);
    return false;
  }

  public boolean equals(ResRequestFriend that) {
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

    boolean this_present_friendProfile = true && this.isSetFriendProfile();
    boolean that_present_friendProfile = true && that.isSetFriendProfile();
    if (this_present_friendProfile || that_present_friendProfile) {
      if (!(this_present_friendProfile && that_present_friendProfile))
        return false;
      if (!this.friendProfile.equals(that.friendProfile))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(ResRequestFriend other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ResRequestFriend typedOther = (ResRequestFriend)other;

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
    lastComparison = Boolean.valueOf(isSetFriendProfile()).compareTo(typedOther.isSetFriendProfile());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFriendProfile()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.friendProfile, typedOther.friendProfile);
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
    StringBuilder sb = new StringBuilder("ResRequestFriend(");
    boolean first = true;

    sb.append("error:");
    if (this.error == null) {
      sb.append("null");
    } else {
      sb.append(this.error);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("friendProfile:");
    if (this.friendProfile == null) {
      sb.append("null");
    } else {
      sb.append(this.friendProfile);
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
    if (friendProfile != null) {
      friendProfile.validate();
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

  private static class ResRequestFriendStandardSchemeFactory implements SchemeFactory {
    public ResRequestFriendStandardScheme getScheme() {
      return new ResRequestFriendStandardScheme();
    }
  }

  private static class ResRequestFriendStandardScheme extends StandardScheme<ResRequestFriend> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ResRequestFriend struct) throws org.apache.thrift.TException {
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
          case 2: // FRIEND_PROFILE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.friendProfile = new UserProfile();
              struct.friendProfile.read(iprot);
              struct.setFriendProfileIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ResRequestFriend struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.error != null) {
        oprot.writeFieldBegin(ERROR_FIELD_DESC);
        struct.error.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.friendProfile != null) {
        oprot.writeFieldBegin(FRIEND_PROFILE_FIELD_DESC);
        struct.friendProfile.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ResRequestFriendTupleSchemeFactory implements SchemeFactory {
    public ResRequestFriendTupleScheme getScheme() {
      return new ResRequestFriendTupleScheme();
    }
  }

  private static class ResRequestFriendTupleScheme extends TupleScheme<ResRequestFriend> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ResRequestFriend struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetError()) {
        optionals.set(0);
      }
      if (struct.isSetFriendProfile()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetError()) {
        struct.error.write(oprot);
      }
      if (struct.isSetFriendProfile()) {
        struct.friendProfile.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ResRequestFriend struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.error = new ErrorDesc();
        struct.error.read(iprot);
        struct.setErrorIsSet(true);
      }
      if (incoming.get(1)) {
        struct.friendProfile = new UserProfile();
        struct.friendProfile.read(iprot);
        struct.setFriendProfileIsSet(true);
      }
    }
  }

}
