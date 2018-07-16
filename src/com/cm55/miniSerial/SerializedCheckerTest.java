package com.cm55.miniSerial;

import java.lang.reflect.*;
import java.util.*;

import org.junit.*;

public class SerializedCheckerTest {

  public static class Sample {
    List<String>list;
  }
  
  @Test
  public void test() throws Exception {
    Field field = Sample.class.getDeclaredField("list");
    Type type = field.getGenericType();
    System.out.println("" + type + "," + type.getClass());
    if (type instanceof ParameterizedType) {
      ParameterizedType t = (ParameterizedType)type;
      for (Type a: t.getActualTypeArguments()) {
        System.out.println("" + a);
      }
    }
  }

}
