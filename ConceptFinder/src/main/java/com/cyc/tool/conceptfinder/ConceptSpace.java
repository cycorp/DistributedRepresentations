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
import com.cyc.tool.distributedrepresentations.Word2VecSpace.NoWordToVecVectorForTerm;
import com.cyc.tool.distributedrepresentations.Word2VecSubspace;
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <P>
 * ConceptSpace provides access to a Word2VecSpace and methods for finding ConceptMatches.
 */
public class ConceptSpace {

  Word2VecSpace w2vSpace;
  private static ConceptSpace singleton;

  /**
   * Creates a new instance of ConceptSpace.
   *
   * @param w2v
   * @throws java.io.IOException
   */
  public ConceptSpace(Word2VecSpace w2v) throws IOException {
    w2vSpace = w2v;
  }
  
  public static ConceptSpace get(Word2VecSpace w2v) {
    if (singleton == null) {
      try {
        singleton = new ConceptSpace(w2v);
      } catch (IOException ex) {
        Logger.getLogger(ConceptSpace.class.getName()).log(Level.SEVERE, null, ex);
        throw new RuntimeException("Can't create the Google News W2VSpace object " + ex);
      }
    }
    return singleton;
  }

  /**
   *
   * @param terms
   * @param n
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNFor(List<String> terms, Integer n) throws NoWordToVecVectorForTerm {
    return findNearest(w2vSpace.getMaximalNormedVector(terms))
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNFor(String terms, Integer n) throws NoWordToVecVectorForTerm {
    return findNearestNFor(w2vSpace.stringToList(terms), n);

  }

  /**
   *
   * @param terms
   * @param n
   * @param ocyc
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForIn(List<String> terms, Integer n, OpenCycOwl ocyc) throws NoWordToVecVectorForTerm {
    float[] norm = w2vSpace.getMaximalNormedVector(terms);
    return findNearestWhere(norm, m -> ocyc.knownTerm(m), t -> String.join(" | ", ocyc.conceptsFor(t)))
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @param ocyc
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForIn(String terms, Integer n, OpenCycOwl ocyc) throws NoWordToVecVectorForTerm {

    return findNearestNForIn(w2vSpace.stringToList(terms), n, ocyc);

  }

  /**
   *
   * @param terms
   * @param n
   * @param ocyc
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForInStrictW2V(List<String> terms, Integer n, OpenCycOwl ocyc) throws NoWordToVecVectorForTerm {
    float[] norm = w2vSpace.getGoogleNormedVector(terms);
    return findNearestWhere(norm, m -> ocyc.knownTerm(m), t -> String.join(" | ", ocyc.conceptsFor(t)))
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @param ocyc
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForInStrictW2V(String terms, Integer n, OpenCycOwl ocyc) throws NoWordToVecVectorForTerm {
    float[] norm = w2vSpace.getGoogleNormedVector(w2vSpace.stringToList(terms));
    return findNearestWhere(norm, m -> ocyc.knownTerm(m), t -> String.join(" | ", ocyc.conceptsFor(t)))
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   * Find the position of terms in the larger space from which this is derived a larger space, and
   * then search around them in a this space that spans fewer terms, but is otherwise the same
   *
   * Will fail if the space for this concept space is not a SubSpace
   *
   * @param terms The string containing a set of terms to search around
   * @param n How many things to find in this space
   * @param note
   * @return a List of ConceptMatches
   * @throws com.cyc.tool.distributedrepresentations.Word2VecSpace.NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForPosition(String terms, Integer n, Function<String, String> note) throws NoWordToVecVectorForTerm {
    return findNearestNForPosition(w2vSpace.stringToList(terms),
            n, note);
  }

  /**
   * Find the position of terms in the larger space from which this is derived a larger space, and
   * then search around them in a this space that spans fewer terms, but is otherwise the same
   *
   * Will fail if the space for this concept space is not a SubSpace
   *
   * @param terms The string containing a set of terms to search around
   * @param n How many things to find in this space
   * @param note
   * @return a List of ConceptMatches
   * @throws com.cyc.tool.distributedrepresentations.Word2VecSpace.NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForPosition(List<String> terms, Integer n, Function<String, String> note) throws NoWordToVecVectorForTerm {
    Word2VecSpace posSpace = ((Word2VecSubspace) w2vSpace).getSuperSpace();
    return findNearestNForPosition(terms,
            posSpace, n, note);
  }

  /**
   * Find the position of terms in a larger space, and then search around them in a space that spans
   * fewer terms, but is otherwise the same
   *
   * @param terms The string containing a set of terms to search around
   * @param posSpace The other larger space in which to search for those terms.
   * @param n How many things to find in this space
   * @param note
   * @return a List of ConceptMatches
   * @throws com.cyc.tool.distributedrepresentations.Word2VecSpace.NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForPosition(String terms, Word2VecSpace posSpace, Integer n, Function<String, String> note) throws NoWordToVecVectorForTerm {
    return findNearestNForPosition(w2vSpace.stringToList(terms),
            posSpace, n, note);

  }

  /**
   * Find the position of terms in a larger space, and then search around them in a space that spans
   * fewer terms, but is otherwise the same
   *
   * @param terms The list of terms to search around
   * @param posSpace The other larger space in which to search for those terms.
   * @param n How many things to find in this space
   * @param note
   * @return a List of ConceptMatches
   * @throws com.cyc.tool.distributedrepresentations.Word2VecSpace.NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForPosition(List<String> terms, Word2VecSpace posSpace, Integer n, Function<String, String> note) throws NoWordToVecVectorForTerm {
    return findNearest(posSpace.getMaximalNormedVector(terms), note)
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForStrictW2V(List<String> terms, Integer n) throws NoWordToVecVectorForTerm {
    return findNearest(w2vSpace.getGoogleNormedVector(terms))
            .stream()
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForWithInputTermFiltering(List<String> terms, Integer n) throws NoWordToVecVectorForTerm {
    return findNearest(w2vSpace.getMaximalNormedVector(terms))
            .stream()
            .filter(m -> !terms.contains(m.getTerm())) // the google code removes any search term
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @param terms
   * @param n
   * @return a List of ConceptMatches
   * @throws NoWordToVecVectorForTerm
   */
  public List<ConceptMatch> findNearestNForWithInputTermFilteringStrictW2V(List<String> terms, Integer n) throws NoWordToVecVectorForTerm {
    return findNearest(w2vSpace.getGoogleNormedVector(terms))
            .stream()
            .filter(m -> !terms.contains(m.getTerm())) // the google code removes any search term
            .collect(Collectors.toList())
            .subList(0, n);
  }

  /**
   *
   * @return the w2vSpace
   */
  public Word2VecSpace getW2VSpace() {
    return w2vSpace;
  }

  private List<ConceptMatch> findNearest(float[] searchVector, Function<String, String> note) {
    Comparator<Double> compareDouble
            = (Double m1, Double m2) -> Double.compare(m2, m1);

    Comparator<ConceptMatch> compareMatches
            = (ConceptMatch m1, ConceptMatch m2) -> Double.compare(m2.getSimilarity(), m1.getSimilarity());

    // This is a massive sort (3m elements) so it might be better to optimise
    // for top N
    return w2vSpace.getVectors().keySet().stream()
            .map(s -> new ConceptMatch(w2vSpace, searchVector, s, note))
            .sorted(compareMatches).collect(Collectors.toList());
  }

  private List<ConceptMatch> findNearest(float[] searchVector) {
    return findNearest(searchVector, null);
  }

  private List<ConceptMatch> findNearestWhere(float[] searchVector, Predicate<String> pred, Function<String, String> note) {
    Comparator<ConceptMatch> compareMatches
            = (ConceptMatch m1, ConceptMatch m2) -> Double.compare(m2.getSimilarity(), m1.getSimilarity());
    // This is a massive sort (3m elements) so it might be better to optimise
    // for top N
    return w2vSpace.getVectors().keySet().parallelStream()
            .filter(pred)
            .map(s -> new ConceptMatch(w2vSpace, searchVector, s, note))
            .sorted(compareMatches).collect(Collectors.toList());
  }

}
