package com.cm55.miniSerial;

import com.cm55.gson.*;


/**
 * 指定されたクラスのオブジェクトを指定されたファイルについてJSON形式に変換後バイナリでセーブ・ロードする。
 * ただし、ロード時にエラーが起こった場合には、そのクラスのnewInstance()を呼び出してオブジェクトを作成して返す。
 * @param <T>
 */
public class DataSerializer<T> {
  
  private final DataIO dataIO;
  private final Class<T>clazz;
  private final long dataKey;
  private final Serializer<T>jsonSerializer;
   
  protected DataSerializer(DataIO dataIO, Class<T>clazz) {
    this.dataIO = dataIO;
    this.clazz = clazz;
    this.jsonSerializer = new Serializer<T>(clazz);
    this.dataKey = SerializedChecker.getKey(clazz);
  }
  
  /** ファイルにセーブする */
  public void put(T object) {
    try {
      byte[]serialized = toSerialized(object);
      dataIO.putData(dataKey, serialized);
      loaded = object;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public byte[]toSerialized(T object) {
    if (object == null) return null;
    byte[]serialized = jsonSerializer.serializeGzip(object);
    return serialized;
  }
  
  private T loaded;
  
  /** ファイルからロードする。存在しないときはインスタンスを作成する */
  public T get() {   
    mayGet();
    if (loaded != null) return loaded;
    return loaded = createNew();
  }

  /** ファイルからロードする。存在しないときはnullを返す */
  public T mayGet() {
    if (loaded != null) return loaded;
    byte[]serialized = dataIO.getData(dataKey);
    if (serialized == null) return null;
    try {
      return loaded = fromSerialized(serialized);
    } catch (Exception ex) {
      return null;
    }
  }
  
  public T fromSerialized(byte[]serialized) {
    return jsonSerializer.deserializeGzip(serialized);
  }
  
  private T createNew() {
    try {
      return clazz.newInstance();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }    
  }
}
