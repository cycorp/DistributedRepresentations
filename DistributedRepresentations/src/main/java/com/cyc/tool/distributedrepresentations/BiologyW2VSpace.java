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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.mapdb.DBMaker;

/**
 * The word2vec space produced by BioASQ by training on pubmed.
 *
 * <p>
 * See:
 * http://bioasq.org/news/bioasq-releases-continuous-space-word-vectors-obtained-applying-word2vec-pubmed-abstracts
 */
public class BiologyW2VSpace extends Word2VecSpace {

  private static final String fileBase = "/cyc/projects/kbTaxonomy/ConceptFinder/BioASQ/word2vecTools/";
  private static BiologyW2VSpace singleton;
  private static final String w2vlabelfile = fileBase + "types.txt";
  private static final String w2vvectorfile = fileBase + "vectors.txt";

  private BiologyW2VSpace() throws IOException {
    db = DBMaker.newFileDB(new File(Config.getW2vDBFile()))
            .closeOnJvmShutdown()
            //      .encryptionEnable("password")
            .make();
    vectors = db.getTreeMap(getWord2VecVectorsMapName());
    // vectors.clear();
    if (!vectors.isEmpty()) {
      assert (getVector("anti-mib-1") != null);
      setSize(getVector("hgh-b").length);
      return;
    }
    int i = 0;
    try (BufferedReader labelReader = new BufferedReader(new FileReader(w2vlabelfile))) {
      try (BufferedReader vectorReader = new BufferedReader(new FileReader(w2vvectorfile))) {
        for (String label; (label = labelReader.readLine()) != null;) {
          String vec = vectorReader.readLine();
          float[] d
                  = normVector(
                          Arrays.asList(vec.split("\\s+"))
                                  .stream()
                                  .map(s -> Float.valueOf(s))
                                  .collect(Collectors.toList())
                  );
          if (getSize() != 0) {
            assert d.length == getSize() : "Line without " + getSize() + " floats";
          } else {
            setSize(d.length);
          }
          if (i++ % 100000 == 0) {
            db.commit();
            System.out.println(i + ": " + label);
          }

          vectors.put(label, d);
          // process the line.
        }
        // line is not visible here.
      }
    }
    System.out.println("Read " + i + " term positions for " + BiologyW2VSpace.class.getSimpleName());
    db.commit();
    db.compact();
  }

  /**
   * Factory get method for BiologyW2VSpace.
   * 
   * @return a BiologyW2VSpace
   */
  public static BiologyW2VSpace get() {
    if (singleton == null) {
      try {
        singleton = new BiologyW2VSpace();
      } catch (IOException ex) {
        Logger.getLogger(BiologyW2VSpace.class.getName()).log(Level.SEVERE, null, ex);
        throw new RuntimeException("Can't create the Biology W2VSpace object\n " + ex);
      }
    }
    return singleton;
  }

  /*
   @ToDo: change this to use the class name, so that it's automatically correct
   */
  private static String getWord2VecVectorsMapName() {
    return BiologyW2VSpace.class.getCanonicalName();
  }

}
