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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * <P>
 * BiologyW2VSpace filtered to only contain terms in Open Cyc.
 */
public class BiologyW2VOpenCycSubspace extends Word2VecSubspace {

  static BiologyW2VOpenCycSubspace singleton;

  private BiologyW2VOpenCycSubspace(OpenCycOwl ocyc) throws IOException {
    super(BiologyW2VSpace.get(),
            m -> ocyc.knownTerm(m), getWord2VecVectorsMapName());
  }

  /**
   *
   * @return a WordToVecSubspace limited only to terms in OpenCyc
   */
  public static BiologyW2VOpenCycSubspace get() {
    if (singleton == null) {
      try {
        OpenCycOwl ocyc = new OpenCycOwl();
        singleton = new BiologyW2VOpenCycSubspace(ocyc);
      } catch (IOException ex) {
        Logger.getLogger(BiologyW2VSpace.class.getName()).log(Level.SEVERE, null, ex);
        throw new RuntimeException("Can't create the Biology W2VSpace object " + ex);
      } catch (OWLOntologyCreationException ex) {
        Logger.getLogger(BiologyW2VOpenCycSubspace.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return singleton;
  }

  static String getWord2VecVectorsMapName() {
    return BiologyW2VOpenCycSubspace.class.getCanonicalName();
  }

}
