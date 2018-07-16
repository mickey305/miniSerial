package com.cm55.miniSerial;

import java.lang.annotation.*;

/**
 * シリアライズされるクラスにつけられるlong値キー
 * @author ysugimura
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)  
public @interface Serialized {

  public long key() default 0;
}
