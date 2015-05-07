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

import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import java.io.Serializable;
import java.util.function.Function;

/**
 * A ConceptMatch relates a concept to a term.
 */
public class ConceptMatch implements Serializable {

  final String concept;

  final double similarity;
  final String term;

  /**
   * ConceptMatch constructor
   *
   * @param w2v
   * @param search
   * @param term
   * @param noter
   */
  public ConceptMatch(Word2VecSpace w2v, float[] search, String term,
          Function<String, String> noter) {
    this.term = term;
    if (noter == null) {
      this.concept = "---";
    } else {
      this.concept = noter.apply(term);
    }
    similarity = w2v.googleSimilarity(search, w2v.getVector(term));
  }

  /**
   * ConceptMatch constructor
   *
   * @param w2v
   * @param search
   * @param term
   */
  public ConceptMatch(Word2VecSpace w2v, float[] search, String term) {
    this(w2v, search, term, null);
  }

  /**
   *
   * @return the concept
   */
  public String getConcept() {
    return concept;
  }

  /**
   *
   * @return the similarity
   */
  public double getSimilarity() {
    return similarity;
  }

  /**
   *
   * @return the term
   */
  public String getTerm() {
    return term;
  }

  @Override
  public String toString() {
    return term + ": " + similarity + ": " + (concept == null ? "--" : concept);
  }
}
