/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.thxsoft.vds.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum MessageType implements org.apache.thrift.TEnum {
  MT_CONTEXT_INVITATION(1),
  MT_FRIEND_REQUEST(2),
  MT_PERSONAL_MESSAGE(3),
  MT_CONTEXT_MESSAGE(4);

  private final int value;

  private MessageType(int value) {
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
  public static MessageType findByValue(int value) { 
    switch (value) {
      case 1:
        return MT_CONTEXT_INVITATION;
      case 2:
        return MT_FRIEND_REQUEST;
      case 3:
        return MT_PERSONAL_MESSAGE;
      case 4:
        return MT_CONTEXT_MESSAGE;
      default:
        return null;
    }
  }
}
