package com.cm55.miniSerial;

import java.lang.reflect.*;
import java.util.*;

/**
 * 指定されたクラス以下が、JSONシリアライズフィールド保持用のSerializedアノテーションが付けられているかをチェックし、トップクラスのキーを返す。
 * @author admin
 */
public class SerializedChecker {

  private static Map<Class<?>, Long>checkedMap = new HashMap<Class<?>, Long>();
  
  public static long getKey(Class<?>clazz) {
    // チェック済か
    Long key = checkedMap.get(clazz);
    if (key != null) return key;
    
    // トップはキーが必要
    key = checkAnnotation(clazz);
    if (key == 0) throw new RuntimeException("top key should not be zero");
    
    // トップ以下をチェックする
    checkSerialized(clazz);
    
    // チェック済
    checkedMap.put(clazz, key);
    return key;
  }

  /** 指定クラス以下について、シリアライズ用の準備がされているかをチェック */
  private static void checkSerialized(Class<?>clazz) {
    // プリミティブ
    if (clazz.isPrimitive()) return;
    
    // 配列
    if (clazz.isArray()) {
      // 配列要素型をチェックする
      checkSerialized(clazz.getComponentType());
      return;
    }
    
    // その他の型。型名を取得し、java.パッケージであれば何もしない。
    String className = clazz.getName();
    if (className.startsWith("java.")) return;
    
    // おそらくユーザ定義クラスの場合、Serializedアノテーションをチェック
    checkAnnotation(clazz);
    
    // このクラスのフィールドすべてをチェック
    Arrays.stream(clazz.getDeclaredFields()).forEach(field->checkSerialized(field));
  }
  
  /** フィールドについてシリアライズ用の準備がされているかをチェック */
  private static void checkSerialized(Field field) {
    // static/transientは無視
    if ((field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) != 0) return;
    
    // このフィールドの型の型パラメータをチェック
    Type type = field.getGenericType();
    if (type instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType)type;
      for (Type arg: pt.getActualTypeArguments()) {
        checkSerialized((Class<?>)arg);
      }
    }
    
    // フィールド型自体をチェック
    checkSerialized(field.getType());
  }
    
  /** @Serializedアノテーションをチェックし、キー値を取得する */
  static long checkAnnotation(Class<?>clazz) {
    Serialized ann = clazz.getAnnotation(Serialized.class);
    if (ann == null)
      throw new RuntimeException("Not Serialized:" + clazz);
    return ann.key();
  }
  
}
