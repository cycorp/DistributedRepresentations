package com.cyc.tool.owltools;

/*
 * #%L
 * OwlTools
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
 * <P>OwlToolsConfig provides some locations to use for classes in the OwlTools project.
 */
public class OwlToolsConfig extends MapDBConfiguration{
  
  /**
   * The location of the OpenCyc OWL export file.
   */
//  final public static String ocycLocation = "/cyc/projects/kbTaxonomy/owl-export-unversioned.owl";
  final public static String ocycLocation = "/home/cyc/TaxonomyViewer/data/owl-export-unversioned.owl";
  private static final String allConceptsName = "allConcepts";
  private static final String conceptsWithTermsName = "termsWithConcepts";
//  private static final String fallBackDBLocation = "/cyc/projects/kbTaxonomy/Experiments/ConceptFinder/";
  private static final String fallBackDBLocation = "/home/cyc/TaxonomyViewer/data/";
  
  // From OwlToolsConfig.java in W2VOCyc
  private static final String ocycTermDBFile = "/ocycTerm";
  private static final String ocycTermMapName = "owlTerms";
  
  
  private static final String typeGraphName = "typeGraph";

  /**
   *
   * @return the allConceptsName
   */
  protected static String getAllConceptsName() {
    return allConceptsName;
  }

  /**
   *
   * @return the conceptsWithTermsName
   */
  protected static String getConceptsWithTermsName() {
    return conceptsWithTermsName;
  }

  /**
   *
   * @return the location of the ocycTermDBFile
   */
  protected static String getOcycTermDBFile() {
    return  getMapDBBase(fallBackDBLocation) +
            ocycTermDBFile;
  }

  /**
   *
   * @return the ocycTermMapName
   */
  protected static String getOcycTermMapName() {
    return ocycTermMapName;
  }

  /**
   *
   * @return the typeGraphName
   */
  protected static String getTypeGraphName() {
    return typeGraphName;
  }
}
