package com.cm55.miniSerial;

/**
 * long値をキーとしてバイナリデータを格納・取得するインターフェース
 * @author ysugimura
 */
public interface DataIO {
  public <T> byte[] getData(long dataKey);
  public void putData(long dataKey, byte[]data);
}

