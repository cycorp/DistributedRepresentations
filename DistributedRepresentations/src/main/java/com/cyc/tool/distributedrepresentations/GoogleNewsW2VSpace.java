package com.cyc.tool.distributedrepresentations;

/*
 * #%L
 * DistributedRepresentations
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The word2vec space produced by Google by training on 10^11 words of news.
 *
 * <p>
 * See: https://code.google.com/p/word2vec/
 */
public class GoogleNewsW2VSpace extends Word2VecSpaceFromFile {

  private static GoogleNewsW2VSpace singleton;
//  private static final String w2vfile = "/fastscratch/GoogleNews-vectors-negative300.bin.gz";
//  private static final String w2vfile = "/fastscratch/jmoszko/GoogleNews-OpenCyc-vectors3.bin.gz";
  private static final String w2vfile = "/local/cyc/data/GoogleNews-OpenCyc-vectors3.bin.gz";

  private GoogleNewsW2VSpace() throws IOException {
    super();
    vectors = new HashMap<>();
//    vectors = db.getTreeMap(getWord2VecVectorsMapName());
    if (!vectors.isEmpty()) {
      assert (getVector("snowcapped_Caucasus") != null);
      setSize(getVector("dog").length);
      return;
    }
    createW2VinDB(getW2vfile());
  }
  
  //always gets non-normalized versions...
  private GoogleNewsW2VSpace(Path p) throws IOException {
    super();
    vectors = new HashMap<>();
//    vectors = db.getTreeMap(getWord2VecVectorsMapName());
    if (!vectors.isEmpty()) {
      assert (getVector("snowcapped_Caucasus") != null);
      setSize(getVector("dog").length);
      return;
    }
    createNonNormalizedW2VinDB(p.toString());
  }

  /**
   * Factory get method for GoogleNewsW2VSpace.
   * 
   * @return a GoogleNewsW2VSpace
   */
  public static GoogleNewsW2VSpace get() {
    if (singleton == null) {
      try {
        singleton = new GoogleNewsW2VSpace();
      } catch (IOException ex) {
        Logger.getLogger(GoogleNewsW2VSpace.class.getName()).log(Level.SEVERE, null, ex);
        throw new RuntimeException("Can't create the Google News W2VSpace object " + ex);
      }
    }
    return singleton;
  }

  /**
   * Factory get method for GoogleNewsW2VSpace.
   * 
   * @return a GoogleNewsW2VSpace
   */
  public static GoogleNewsW2VSpace get(Path p) {
    if (singleton == null) {
      try {
        singleton = new GoogleNewsW2VSpace(p);
      } catch (IOException ex) {
        Logger.getLogger(GoogleNewsW2VSpace.class.getName()).log(Level.SEVERE, null, ex);
        throw new RuntimeException("Can't create the Google News W2VSpace object " + ex);
      }
    }
    return singleton;
  }
  private static String getW2vfile() {
    return w2vfile;
  }

  private static String getWord2VecVectorsMapName() {
    /*
     @ToDo: change this to use the class name, so that it's automatically correct
     */
    return GoogleNewsW2VSpace.class.getCanonicalName();
    //return word2VecVectorsMapName;
  }

}
