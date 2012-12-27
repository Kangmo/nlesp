/**
 * Autogenerated by Thrift Compiler (0.8.0)
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

public class PullDataResponse implements org.apache.thrift.TBase<PullDataResponse, PullDataResponse._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PullDataResponse");

  private static final org.apache.thrift.protocol.TField MAX_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("maxTimestamp", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField DATA_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("dataList", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PullDataResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PullDataResponseTupleSchemeFactory());
  }

  public long maxTimestamp; // required
  public List<ContextData> dataList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MAX_TIMESTAMP((short)1, "maxTimestamp"),
    DATA_LIST((short)2, "dataList");

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
        case 1: // MAX_TIMESTAMP
          return MAX_TIMESTAMP;
        case 2: // DATA_LIST
          return DATA_LIST;
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
  private static final int __MAXTIMESTAMP_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MAX_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("maxTimestamp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields.DATA_LIST, new org.apache.thrift.meta_data.FieldMetaData("dataList", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ContextData.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PullDataResponse.class, metaDataMap);
  }

  public PullDataResponse() {
  }

  public PullDataResponse(
    long maxTimestamp,
    List<ContextData> dataList)
  {
    this();
    this.maxTimestamp = maxTimestamp;
    setMaxTimestampIsSet(true);
    this.dataList = dataList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PullDataResponse(PullDataResponse other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.maxTimestamp = other.maxTimestamp;
    if (other.isSetDataList()) {
      List<ContextData> __this__dataList = new ArrayList<ContextData>();
      for (ContextData other_element : other.dataList) {
        __this__dataList.add(new ContextData(other_element));
      }
      this.dataList = __this__dataList;
    }
  }

  public PullDataResponse deepCopy() {
    return new PullDataResponse(this);
  }

  @Override
  public void clear() {
    setMaxTimestampIsSet(false);
    this.maxTimestamp = 0;
    this.dataList = null;
  }

  public long getMaxTimestamp() {
    return this.maxTimestamp;
  }

  public PullDataResponse setMaxTimestamp(long maxTimestamp) {
    this.maxTimestamp = maxTimestamp;
    setMaxTimestampIsSet(true);
    return this;
  }

  public void unsetMaxTimestamp() {
    __isset_bit_vector.clear(__MAXTIMESTAMP_ISSET_ID);
  }

  /** Returns true if field maxTimestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetMaxTimestamp() {
    return __isset_bit_vector.get(__MAXTIMESTAMP_ISSET_ID);
  }

  public void setMaxTimestampIsSet(boolean value) {
    __isset_bit_vector.set(__MAXTIMESTAMP_ISSET_ID, value);
  }

  public int getDataListSize() {
    return (this.dataList == null) ? 0 : this.dataList.size();
  }

  public java.util.Iterator<ContextData> getDataListIterator() {
    return (this.dataList == null) ? null : this.dataList.iterator();
  }

  public void addToDataList(ContextData elem) {
    if (this.dataList == null) {
      this.dataList = new ArrayList<ContextData>();
    }
    this.dataList.add(elem);
  }

  public List<ContextData> getDataList() {
    return this.dataList;
  }

  public PullDataResponse setDataList(List<ContextData> dataList) {
    this.dataList = dataList;
    return this;
  }

  public void unsetDataList() {
    this.dataList = null;
  }

  /** Returns true if field dataList is set (has been assigned a value) and false otherwise */
  public boolean isSetDataList() {
    return this.dataList != null;
  }

  public void setDataListIsSet(boolean value) {
    if (!value) {
      this.dataList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MAX_TIMESTAMP:
      if (value == null) {
        unsetMaxTimestamp();
      } else {
        setMaxTimestamp((Long)value);
      }
      break;

    case DATA_LIST:
      if (value == null) {
        unsetDataList();
      } else {
        setDataList((List<ContextData>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MAX_TIMESTAMP:
      return Long.valueOf(getMaxTimestamp());

    case DATA_LIST:
      return getDataList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MAX_TIMESTAMP:
      return isSetMaxTimestamp();
    case DATA_LIST:
      return isSetDataList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof PullDataResponse)
      return this.equals((PullDataResponse)that);
    return false;
  }

  public boolean equals(PullDataResponse that) {
    if (that == null)
      return false;

    boolean this_present_maxTimestamp = true;
    boolean that_present_maxTimestamp = true;
    if (this_present_maxTimestamp || that_present_maxTimestamp) {
      if (!(this_present_maxTimestamp && that_present_maxTimestamp))
        return false;
      if (this.maxTimestamp != that.maxTimestamp)
        return false;
    }

    boolean this_present_dataList = true && this.isSetDataList();
    boolean that_present_dataList = true && that.isSetDataList();
    if (this_present_dataList || that_present_dataList) {
      if (!(this_present_dataList && that_present_dataList))
        return false;
      if (!this.dataList.equals(that.dataList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(PullDataResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    PullDataResponse typedOther = (PullDataResponse)other;

    lastComparison = Boolean.valueOf(isSetMaxTimestamp()).compareTo(typedOther.isSetMaxTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMaxTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.maxTimestamp, typedOther.maxTimestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDataList()).compareTo(typedOther.isSetDataList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDataList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dataList, typedOther.dataList);
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
    StringBuilder sb = new StringBuilder("PullDataResponse(");
    boolean first = true;

    sb.append("maxTimestamp:");
    sb.append(this.maxTimestamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("dataList:");
    if (this.dataList == null) {
      sb.append("null");
    } else {
      sb.append(this.dataList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PullDataResponseStandardSchemeFactory implements SchemeFactory {
    public PullDataResponseStandardScheme getScheme() {
      return new PullDataResponseStandardScheme();
    }
  }

  private static class PullDataResponseStandardScheme extends StandardScheme<PullDataResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PullDataResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MAX_TIMESTAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.maxTimestamp = iprot.readI64();
              struct.setMaxTimestampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DATA_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.dataList = new ArrayList<ContextData>(_list8.size);
                for (int _i9 = 0; _i9 < _list8.size; ++_i9)
                {
                  ContextData _elem10; // required
                  _elem10 = new ContextData();
                  _elem10.read(iprot);
                  struct.dataList.add(_elem10);
                }
                iprot.readListEnd();
              }
              struct.setDataListIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PullDataResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(MAX_TIMESTAMP_FIELD_DESC);
      oprot.writeI64(struct.maxTimestamp);
      oprot.writeFieldEnd();
      if (struct.dataList != null) {
        oprot.writeFieldBegin(DATA_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.dataList.size()));
          for (ContextData _iter11 : struct.dataList)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PullDataResponseTupleSchemeFactory implements SchemeFactory {
    public PullDataResponseTupleScheme getScheme() {
      return new PullDataResponseTupleScheme();
    }
  }

  private static class PullDataResponseTupleScheme extends TupleScheme<PullDataResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PullDataResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMaxTimestamp()) {
        optionals.set(0);
      }
      if (struct.isSetDataList()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetMaxTimestamp()) {
        oprot.writeI64(struct.maxTimestamp);
      }
      if (struct.isSetDataList()) {
        {
          oprot.writeI32(struct.dataList.size());
          for (ContextData _iter12 : struct.dataList)
          {
            _iter12.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PullDataResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.maxTimestamp = iprot.readI64();
        struct.setMaxTimestampIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.dataList = new ArrayList<ContextData>(_list13.size);
          for (int _i14 = 0; _i14 < _list13.size; ++_i14)
          {
            ContextData _elem15; // required
            _elem15 = new ContextData();
            _elem15.read(iprot);
            struct.dataList.add(_elem15);
          }
        }
        struct.setDataListIsSet(true);
      }
    }
  }

}

