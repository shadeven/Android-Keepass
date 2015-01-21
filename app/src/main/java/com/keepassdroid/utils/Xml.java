package com.keepassdroid.utils;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class Xml {
  
  private Xml() {}
  
  /**
   * Creates a new xml serializer.
   */
  public static XmlSerializer newSerializer() {
    try {
      return XmlSerializerFactory.instance.newSerializer();
    } catch (XmlPullParserException e) {
      throw new AssertionError(e);
    }
  }
  /** Factory for xml serializers. Initialized on demand. */
  static class XmlSerializerFactory {
    static final String TYPE
        = "org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer";
    static final XmlPullParserFactory instance;
    static {
      try {
        instance = XmlPullParserFactory.newInstance(TYPE, null);
      } catch (XmlPullParserException e) {
        throw new AssertionError(e);
      }
    }
  }
}
