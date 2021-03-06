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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import org.mapdb.DBMaker;

/**
 * A space of words from Google Word2Vec.
 *
 */
public abstract class Word2VecSubspace extends Word2VecSpace {

  final Word2VecSpace mySuperSpace;

  /**
   * Word2VecSubspace constructor.
   *
   * @param ofSpace
   * @param includeIf
   * @param persistLoc
   * @throws IOException
   */
  protected Word2VecSubspace(Word2VecSpace ofSpace, Predicate<String> includeIf, String persistLoc) throws IOException {

    mySuperSpace = ofSpace;
    if (db == null) {
      db = DBMaker.newFileDB(new File(Config.getW2vDBFile()))
              .closeOnJvmShutdown()
              //      .encryptionEnable("password")
              .make();
    }
    vectors = db.getTreeMap(persistLoc);
    // vectors.clear();
    if (!vectors.isEmpty()) {
      setSize(vectors.values().iterator().next().length);
      System.out.println("Got cached w2vspace for " + persistLoc + " of dimensionality " + getSize() + " and with " + vectors.size() + " entries.");
      return;
    }
    // assert(vectors == null) :"Subspaces msut be  completely empty when created"; 
    System.out.println("Filtering vectors for:" + persistLoc);
    Map<String, float[]> newvectors = ofSpace.filterVectors(includeIf);
    newvectors.entrySet().forEach(e -> {
      vectors.put(e.getKey(), e.getValue());
    });
    db.commit();
    db.compact();
    db.commit();
    System.out.println("Vectors filtered and persisted.");
  }

  /**
   *
   * @return the mySuperSpace
   */
  public Word2VecSpace getSuperSpace() {
    return mySuperSpace;
  }

}
