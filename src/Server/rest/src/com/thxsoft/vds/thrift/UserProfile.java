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

public class UserProfile implements org.apache.thrift.TBase<UserProfile, UserProfile._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("UserProfile");

  private static final org.apache.thrift.protocol.TField UID_FIELD_DESC = new org.apache.thrift.protocol.TField("uid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField EMAIL_FIELD_DESC = new org.apache.thrift.protocol.TField("email", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField ENCRYPTED_PASSWORD_FIELD_DESC = new org.apache.thrift.protocol.TField("encryptedPassword", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField STATUS_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("statusMessage", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField PHOTO_FIELD_DESC = new org.apache.thrift.protocol.TField("photo", org.apache.thrift.protocol.TType.STRING, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new UserProfileStandardSchemeFactory());
    schemes.put(TupleScheme.class, new UserProfileTupleSchemeFactory());
  }

  public String uid; // required
  public String email; // required
  public String encryptedPassword; // required
  public String name; // required
  public String statusMessage; // required
  public ByteBuffer photo; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    UID((short)1, "uid"),
    EMAIL((short)2, "email"),
    ENCRYPTED_PASSWORD((short)3, "encryptedPassword"),
    NAME((short)4, "name"),
    STATUS_MESSAGE((short)5, "statusMessage"),
    PHOTO((short)6, "photo");

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
        case 1: // UID
          return UID;
        case 2: // EMAIL
          return EMAIL;
        case 3: // ENCRYPTED_PASSWORD
          return ENCRYPTED_PASSWORD;
        case 4: // NAME
          return NAME;
        case 5: // STATUS_MESSAGE
          return STATUS_MESSAGE;
        case 6: // PHOTO
          return PHOTO;
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
    tmpMap.put(_Fields.UID, new org.apache.thrift.meta_data.FieldMetaData("uid", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "UID")));
    tmpMap.put(_Fields.EMAIL, new org.apache.thrift.meta_data.FieldMetaData("email", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ENCRYPTED_PASSWORD, new org.apache.thrift.meta_data.FieldMetaData("encryptedPassword", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STATUS_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("statusMessage", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PHOTO, new org.apache.thrift.meta_data.FieldMetaData("photo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(UserProfile.class, metaDataMap);
  }

  public UserProfile() {
  }

  public UserProfile(
    String uid,
    String email,
    String encryptedPassword,
    String name,
    String statusMessage,
    ByteBuffer photo)
  {
    this();
    this.uid = uid;
    this.email = email;
    this.encryptedPassword = encryptedPassword;
    this.name = name;
    this.statusMessage = statusMessage;
    this.photo = photo;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UserProfile(UserProfile other) {
    if (other.isSetUid()) {
      this.uid = other.uid;
    }
    if (other.isSetEmail()) {
      this.email = other.email;
    }
    if (other.isSetEncryptedPassword()) {
      this.encryptedPassword = other.encryptedPassword;
    }
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetStatusMessage()) {
      this.statusMessage = other.statusMessage;
    }
    if (other.isSetPhoto()) {
      this.photo = org.apache.thrift.TBaseHelper.copyBinary(other.photo);
;
    }
  }

  public UserProfile deepCopy() {
    return new UserProfile(this);
  }

  @Override
  public void clear() {
    this.uid = null;
    this.email = null;
    this.encryptedPassword = null;
    this.name = null;
    this.statusMessage = null;
    this.photo = null;
  }

  public String getUid() {
    return this.uid;
  }

  public UserProfile setUid(String uid) {
    this.uid = uid;
    return this;
  }

  public void unsetUid() {
    this.uid = null;
  }

  /** Returns true if field uid is set (has been assigned a value) and false otherwise */
  public boolean isSetUid() {
    return this.uid != null;
  }

  public void setUidIsSet(boolean value) {
    if (!value) {
      this.uid = null;
    }
  }

  public String getEmail() {
    return this.email;
  }

  public UserProfile setEmail(String email) {
    this.email = email;
    return this;
  }

  public void unsetEmail() {
    this.email = null;
  }

  /** Returns true if field email is set (has been assigned a value) and false otherwise */
  public boolean isSetEmail() {
    return this.email != null;
  }

  public void setEmailIsSet(boolean value) {
    if (!value) {
      this.email = null;
    }
  }

  public String getEncryptedPassword() {
    return this.encryptedPassword;
  }

  public UserProfile setEncryptedPassword(String encryptedPassword) {
    this.encryptedPassword = encryptedPassword;
    return this;
  }

  public void unsetEncryptedPassword() {
    this.encryptedPassword = null;
  }

  /** Returns true if field encryptedPassword is set (has been assigned a value) and false otherwise */
  public boolean isSetEncryptedPassword() {
    return this.encryptedPassword != null;
  }

  public void setEncryptedPasswordIsSet(boolean value) {
    if (!value) {
      this.encryptedPassword = null;
    }
  }

  public String getName() {
    return this.name;
  }

  public UserProfile setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public String getStatusMessage() {
    return this.statusMessage;
  }

  public UserProfile setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

  public void unsetStatusMessage() {
    this.statusMessage = null;
  }

  /** Returns true if field statusMessage is set (has been assigned a value) and false otherwise */
  public boolean isSetStatusMessage() {
    return this.statusMessage != null;
  }

  public void setStatusMessageIsSet(boolean value) {
    if (!value) {
      this.statusMessage = null;
    }
  }

  public byte[] getPhoto() {
    setPhoto(org.apache.thrift.TBaseHelper.rightSize(photo));
    return photo == null ? null : photo.array();
  }

  public ByteBuffer bufferForPhoto() {
    return photo;
  }

  public UserProfile setPhoto(byte[] photo) {
    setPhoto(photo == null ? (ByteBuffer)null : ByteBuffer.wrap(photo));
    return this;
  }

  public UserProfile setPhoto(ByteBuffer photo) {
    this.photo = photo;
    return this;
  }

  public void unsetPhoto() {
    this.photo = null;
  }

  /** Returns true if field photo is set (has been assigned a value) and false otherwise */
  public boolean isSetPhoto() {
    return this.photo != null;
  }

  public void setPhotoIsSet(boolean value) {
    if (!value) {
      this.photo = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case UID:
      if (value == null) {
        unsetUid();
      } else {
        setUid((String)value);
      }
      break;

    case EMAIL:
      if (value == null) {
        unsetEmail();
      } else {
        setEmail((String)value);
      }
      break;

    case ENCRYPTED_PASSWORD:
      if (value == null) {
        unsetEncryptedPassword();
      } else {
        setEncryptedPassword((String)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case STATUS_MESSAGE:
      if (value == null) {
        unsetStatusMessage();
      } else {
        setStatusMessage((String)value);
      }
      break;

    case PHOTO:
      if (value == null) {
        unsetPhoto();
      } else {
        setPhoto((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case UID:
      return getUid();

    case EMAIL:
      return getEmail();

    case ENCRYPTED_PASSWORD:
      return getEncryptedPassword();

    case NAME:
      return getName();

    case STATUS_MESSAGE:
      return getStatusMessage();

    case PHOTO:
      return getPhoto();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case UID:
      return isSetUid();
    case EMAIL:
      return isSetEmail();
    case ENCRYPTED_PASSWORD:
      return isSetEncryptedPassword();
    case NAME:
      return isSetName();
    case STATUS_MESSAGE:
      return isSetStatusMessage();
    case PHOTO:
      return isSetPhoto();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UserProfile)
      return this.equals((UserProfile)that);
    return false;
  }

  public boolean equals(UserProfile that) {
    if (that == null)
      return false;

    boolean this_present_uid = true && this.isSetUid();
    boolean that_present_uid = true && that.isSetUid();
    if (this_present_uid || that_present_uid) {
      if (!(this_present_uid && that_present_uid))
        return false;
      if (!this.uid.equals(that.uid))
        return false;
    }

    boolean this_present_email = true && this.isSetEmail();
    boolean that_present_email = true && that.isSetEmail();
    if (this_present_email || that_present_email) {
      if (!(this_present_email && that_present_email))
        return false;
      if (!this.email.equals(that.email))
        return false;
    }

    boolean this_present_encryptedPassword = true && this.isSetEncryptedPassword();
    boolean that_present_encryptedPassword = true && that.isSetEncryptedPassword();
    if (this_present_encryptedPassword || that_present_encryptedPassword) {
      if (!(this_present_encryptedPassword && that_present_encryptedPassword))
        return false;
      if (!this.encryptedPassword.equals(that.encryptedPassword))
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_statusMessage = true && this.isSetStatusMessage();
    boolean that_present_statusMessage = true && that.isSetStatusMessage();
    if (this_present_statusMessage || that_present_statusMessage) {
      if (!(this_present_statusMessage && that_present_statusMessage))
        return false;
      if (!this.statusMessage.equals(that.statusMessage))
        return false;
    }

    boolean this_present_photo = true && this.isSetPhoto();
    boolean that_present_photo = true && that.isSetPhoto();
    if (this_present_photo || that_present_photo) {
      if (!(this_present_photo && that_present_photo))
        return false;
      if (!this.photo.equals(that.photo))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(UserProfile other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    UserProfile typedOther = (UserProfile)other;

    lastComparison = Boolean.valueOf(isSetUid()).compareTo(typedOther.isSetUid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uid, typedOther.uid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEmail()).compareTo(typedOther.isSetEmail());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEmail()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.email, typedOther.email);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEncryptedPassword()).compareTo(typedOther.isSetEncryptedPassword());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEncryptedPassword()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.encryptedPassword, typedOther.encryptedPassword);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStatusMessage()).compareTo(typedOther.isSetStatusMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatusMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statusMessage, typedOther.statusMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPhoto()).compareTo(typedOther.isSetPhoto());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPhoto()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.photo, typedOther.photo);
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
    StringBuilder sb = new StringBuilder("UserProfile(");
    boolean first = true;

    sb.append("uid:");
    if (this.uid == null) {
      sb.append("null");
    } else {
      sb.append(this.uid);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("email:");
    if (this.email == null) {
      sb.append("null");
    } else {
      sb.append(this.email);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("encryptedPassword:");
    if (this.encryptedPassword == null) {
      sb.append("null");
    } else {
      sb.append(this.encryptedPassword);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("statusMessage:");
    if (this.statusMessage == null) {
      sb.append("null");
    } else {
      sb.append(this.statusMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("photo:");
    if (this.photo == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.photo, sb);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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

  private static class UserProfileStandardSchemeFactory implements SchemeFactory {
    public UserProfileStandardScheme getScheme() {
      return new UserProfileStandardScheme();
    }
  }

  private static class UserProfileStandardScheme extends StandardScheme<UserProfile> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, UserProfile struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // UID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.uid = iprot.readString();
              struct.setUidIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // EMAIL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.email = iprot.readString();
              struct.setEmailIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ENCRYPTED_PASSWORD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.encryptedPassword = iprot.readString();
              struct.setEncryptedPasswordIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // STATUS_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.statusMessage = iprot.readString();
              struct.setStatusMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // PHOTO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.photo = iprot.readBinary();
              struct.setPhotoIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, UserProfile struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.uid != null) {
        oprot.writeFieldBegin(UID_FIELD_DESC);
        oprot.writeString(struct.uid);
        oprot.writeFieldEnd();
      }
      if (struct.email != null) {
        oprot.writeFieldBegin(EMAIL_FIELD_DESC);
        oprot.writeString(struct.email);
        oprot.writeFieldEnd();
      }
      if (struct.encryptedPassword != null) {
        oprot.writeFieldBegin(ENCRYPTED_PASSWORD_FIELD_DESC);
        oprot.writeString(struct.encryptedPassword);
        oprot.writeFieldEnd();
      }
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.statusMessage != null) {
        oprot.writeFieldBegin(STATUS_MESSAGE_FIELD_DESC);
        oprot.writeString(struct.statusMessage);
        oprot.writeFieldEnd();
      }
      if (struct.photo != null) {
        oprot.writeFieldBegin(PHOTO_FIELD_DESC);
        oprot.writeBinary(struct.photo);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class UserProfileTupleSchemeFactory implements SchemeFactory {
    public UserProfileTupleScheme getScheme() {
      return new UserProfileTupleScheme();
    }
  }

  private static class UserProfileTupleScheme extends TupleScheme<UserProfile> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, UserProfile struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetUid()) {
        optionals.set(0);
      }
      if (struct.isSetEmail()) {
        optionals.set(1);
      }
      if (struct.isSetEncryptedPassword()) {
        optionals.set(2);
      }
      if (struct.isSetName()) {
        optionals.set(3);
      }
      if (struct.isSetStatusMessage()) {
        optionals.set(4);
      }
      if (struct.isSetPhoto()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetUid()) {
        oprot.writeString(struct.uid);
      }
      if (struct.isSetEmail()) {
        oprot.writeString(struct.email);
      }
      if (struct.isSetEncryptedPassword()) {
        oprot.writeString(struct.encryptedPassword);
      }
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetStatusMessage()) {
        oprot.writeString(struct.statusMessage);
      }
      if (struct.isSetPhoto()) {
        oprot.writeBinary(struct.photo);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, UserProfile struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.uid = iprot.readString();
        struct.setUidIsSet(true);
      }
      if (incoming.get(1)) {
        struct.email = iprot.readString();
        struct.setEmailIsSet(true);
      }
      if (incoming.get(2)) {
        struct.encryptedPassword = iprot.readString();
        struct.setEncryptedPasswordIsSet(true);
      }
      if (incoming.get(3)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(4)) {
        struct.statusMessage = iprot.readString();
        struct.setStatusMessageIsSet(true);
      }
      if (incoming.get(5)) {
        struct.photo = iprot.readBinary();
        struct.setPhotoIsSet(true);
      }
    }
  }

}
