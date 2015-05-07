package com.cyc.tool;

/*
 * #%L
 * CycMapDBTools
 * %%
 * Copyright (C) 2015 Cycorp, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>
 * MapDBConfiguration defines some defaults to use when accessing MapDB locations.
 */
public class MapDBConfiguration {

  private static String baseString = null;
  static final String dirName = "MapDB";
  static final String goodBase = "/fastscratch";
  static final String goodLocation = goodBase + "/" + dirName;

  /**
   *
   * @param fb
   * @return base location for MapDB
   */
  public static final String getMapDBBase(String fb) {
   if (null == baseString) {
     try {
       baseString
               = getMapDBDirectoryWithFallbackTo(new File(fb)).getCanonicalPath();
     } catch (IOException ex) {
       throw new RuntimeException(ex);
     }
   }
   return baseString;
 }

  private static File getMapDBDirectoryWithFallbackTo(File fallback) throws FileNotFoundException, IOException {
    File base = new File(goodBase);
    if (base.exists() && base.canWrite()) {
      File mdb = new File(goodLocation);
      if (mdb.exists() || mdb.mkdirs()) {
        System.out.println("INFO: "+" using "+mdb.getCanonicalPath());
        return mdb;
      }
    } else {
      System.out.println("WARN: "+goodBase+" not available, backing off to "
              +fallback.getCanonicalPath());
      File completeFallBack = new File(fallback.getCanonicalPath() + "/" + dirName);
      if (completeFallBack.exists() || completeFallBack.mkdirs()) {
        return completeFallBack;
      }
    }
    throw new FileNotFoundException(goodBase + "is not avaliable for " + dirName
            + "and neither is" + fallback);
    
  }

}
