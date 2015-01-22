package com.keepassdroid.utils;

import java.util.Calendar;

public class DateUtils {
  
  public static final Calendar INSTANCE = Calendar.getInstance();
  private static final int DATE_SIZE = 5;

  private DateUtils() {}
  
  public static long getTimeInMillis(int year, int month, int day, int hour, int min, int sec) {
    INSTANCE.set(year, month, day, hour, min, sec);
    return INSTANCE.getTimeInMillis();
  }

  public static long getTimeInMillis(byte[] buf, int offset) {
    byte[] date = new byte[DATE_SIZE];
    System.arraycopy(buf, offset, date, 0, DATE_SIZE);
    return bytesToMilliseconds(date, 0);
  }

  /**
   * Unpack date from 5 byte format. The five bytes at 'offset' are unpacked
   * to a java.util.Date instance.
   */
  public static long bytesToMilliseconds(byte[] buf, int offset) {
    int dw1 = Types.readUByte(buf, offset);
    int dw2 = Types.readUByte(buf, offset + 1);
    int dw3 = Types.readUByte(buf, offset + 2);
    int dw4 = Types.readUByte(buf, offset + 3);
    int dw5 = Types.readUByte(buf, offset + 4);

    // Unpack 5 byte structure to date and time
    int year = (dw1 << 6) | (dw2 >> 2);
    int month = ((dw2 & 0x00000003) << 2) | (dw3 >> 6);
    int day = (dw3 >> 1) & 0x0000001F;
    int hour = ((dw3 & 0x00000001) << 4) | (dw4 >> 4);
    int minute = ((dw4 & 0x0000000F) << 2) | (dw5 >> 6);
    int second = dw5 & 0x0000003F;

    // File format is a 1 based month, java Calendar uses a zero based month
    // File format is a 1 based day, java Calendar uses a 1 based day
    return getTimeInMillis(year, month-1, day, hour, minute, second);
  }

  public static byte[] millisecondsToBytes(long date) {
    byte[] buf = new byte[5];
    INSTANCE.setTimeInMillis(date);

    int year = INSTANCE.get(Calendar.YEAR);
    // File format is a 1 based month, java Calendar uses a zero based month
    int month = INSTANCE.get(Calendar.MONTH) + 1;
    // File format is a 0 based day, java Calendar uses a 1 based day
    int day = INSTANCE.get(Calendar.DAY_OF_MONTH) - 1;
    int hour = INSTANCE.get(Calendar.HOUR_OF_DAY);
    int minute = INSTANCE.get(Calendar.MINUTE);
    int second = INSTANCE.get(Calendar.SECOND);

    buf[0] = Types.writeUByte(((year >> 6) & 0x0000003F));
    buf[1] = Types.writeUByte(((year & 0x0000003F) << 2)
        | ((month >> 2) & 0x00000003));
    buf[2] = (byte) (((month & 0x00000003) << 6)
        | ((day & 0x0000001F) << 1) | ((hour >> 4) & 0x00000001));
    buf[3] = (byte) (((hour & 0x0000000F) << 4) | ((minute >> 2) & 0x0000000F));
    buf[4] = (byte) (((minute & 0x00000003) << 6) | (second & 0x0000003F));

    return buf;
  }
}
