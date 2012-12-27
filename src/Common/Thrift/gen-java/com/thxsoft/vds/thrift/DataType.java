/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.thxsoft.vds.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum DataType implements org.apache.thrift.TEnum {
  DT_CONTEXT_INVITATION(1),
  DT_FRIEND_REQUEST(2),
  DT_PERSONAL_MESSAGE(3),
  DT_CONTEXT_DATA(4);

  private final int value;

  private DataType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static DataType findByValue(int value) { 
    switch (value) {
      case 1:
        return DT_CONTEXT_INVITATION;
      case 2:
        return DT_FRIEND_REQUEST;
      case 3:
        return DT_PERSONAL_MESSAGE;
      case 4:
        return DT_CONTEXT_DATA;
      default:
        return null;
    }
  }
}
