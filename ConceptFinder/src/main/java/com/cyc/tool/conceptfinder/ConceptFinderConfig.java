package com.cyc.tool.conceptfinder;

/*
 * #%L
 * ConceptFinder
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

import com.cyc.tool.MapDBConfiguration;

/**
 * <P>
 * ConceptFinderConfig is designed to set paths for caching and data access for this package.
 */
public class ConceptFinderConfig extends MapDBConfiguration {

//  private static final String fallBackLocation = "/cyc/pojects/kbTaxonomy/Experiments/ConceptFinder/";
  private static final String fallBackLocation = "/local/cyc/data/";
  private static final String missingConceptDBFile = "/missingConcept";

  private static final String w2vDBFile = "/w2vdb";
//  private static final String w2vVectorFile = "/cyc/projects/kbTaxonomy/Experiments/ConceptFinder/GoogleNews-vectors-negative300.bin.gz";
  private static final String w2vVectorFile = "/local/cyc/data/GoogleNews-vectors-negative300.bin.gz";
  private static final String word2VecVectorsMapName = "word2Vec";

  /**
   *
   * @return the missingConceptDBFile location
   */
  protected static String getMissingConceptDBFile() {
    return getMapDBBase(fallBackLocation) + missingConceptDBFile;
  }

  /**
   *
   * @return the w2vVectorFile
   */
  protected static String getW2VVectorfile() {
    return w2vVectorFile;
  }

  /**
   *
   * @return the w2vDBFile location
   */
  protected static String getW2vDBFile() {
    return getMapDBBase(fallBackLocation) + w2vDBFile;
  }

  /**
   *
   * @return the word2VecVectorsMapName
   */
  protected static String getWord2VecVectorsMapName() {
    return word2VecVectorsMapName;
  }

}
