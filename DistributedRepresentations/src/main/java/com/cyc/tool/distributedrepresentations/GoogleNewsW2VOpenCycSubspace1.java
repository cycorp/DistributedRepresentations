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
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * News Word2Vec Distributed representation filtered to only contain terms in Open Cyc.
 *
 * <p>
 * Used for rapid searches of the space for open cyc terms
 */
public class GoogleNewsW2VOpenCycSubspace1 extends Word2VecSubspace {

  static GoogleNewsW2VOpenCycSubspace1 singleton;

  private GoogleNewsW2VOpenCycSubspace1() throws IOException {
    super(GoogleNewsW2VSpace.get(),
            m -> true, getWord2VecVectorsMapName());
  }

  private GoogleNewsW2VOpenCycSubspace1(Path p) throws IOException {
    super(GoogleNewsW2VSpace.get(p),
            m -> true, getWord2VecVectorsMapName());
  }

  private GoogleNewsW2VOpenCycSubspace1(Path p, OpenCycOwl ocyc) throws IOException {
    super(GoogleNewsW2VSpace.get(p),
            m -> ocyc.knownTerm(m), getWord2VecVectorsMapName());
  }

  /**
   *
   * @return a WordToVecSubspace limited only to terms in OpenCyc
   */
  public static GoogleNewsW2VOpenCycSubspace1 get() {
    if (singleton == null) {
      try {
        singleton = new GoogleNewsW2VOpenCycSubspace1();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    return singleton;
  }

  /**
   *
   * @return a WordToVecSubspace limited only to terms in OpenCyc
   */
  public static GoogleNewsW2VOpenCycSubspace1 get(Path p) {
    if (singleton == null) {
      try {
        OpenCycOwl ocyc = new OpenCycOwl();
        singleton = new GoogleNewsW2VOpenCycSubspace1(p, ocyc);
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (OWLOntologyCreationException ex) {
        Logger.getLogger(GoogleNewsW2VOpenCycSubspace1.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return singleton;
  }

  static String getWord2VecVectorsMapName() {
    return GoogleNewsW2VOpenCycSubspace1.class.getCanonicalName();
  }

}
