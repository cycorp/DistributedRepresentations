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
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Methods for finding missing concepts with a ConceptSpace, a Word2VecSpace, and OpenCyc.
 */
abstract public class MissingConceptFinder {

  final private ConceptSpace cSpace;
  final private OpenCycOwl ocyc;
  private final Word2VecSpace w2vs;
  ConcurrentNavigableMap<Integer, List<ConceptMatch>> conceptsForMissingTerms;
  DB db;
  List<String[]> missingConceptNames;
  List<String[]> missingMappingNames;
  ConcurrentNavigableMap<Integer, String[]> missingTerms;

  /**
   * MissingConceptFinder constructor.
   *
   * @param w2v
   * @param oco
   * @throws IOException
   * @throws OWLOntologyCreationException
   */
  public MissingConceptFinder(Word2VecSpace w2v, OpenCycOwl oco) throws IOException, OWLOntologyCreationException {
    this(w2v, oco, null);
  }

  /**
   * MissingConceptFinder constructor.
   *
   * @param w2v
   * @param oco
   * @param cSpace
   * @throws IOException
   * @throws OWLOntologyCreationException
   */
  public MissingConceptFinder(Word2VecSpace w2v, OpenCycOwl oco, ConceptSpace cSpace) throws IOException, OWLOntologyCreationException {
    w2vs = w2v;
    ocyc = oco;
    this.cSpace = cSpace;
    db = DBMaker.newFileDB(new File(ConceptFinderConfig.getMissingConceptDBFile()))
            .closeOnJvmShutdown()
            //      .encryptionEnable("password")
            .make();

    //Use this to reset
    //    missingTerms.clear();    db.commit();
  }

  /**
   *
   * @return a List of Strings
   */
  public List<String> conceptsWithTerms() {
    return this.getConceptsForMissingTerms().keySet().stream()
            .map(i -> Arrays.asList(getMissingTerms().get(i))
                    .stream()
                    .collect(Collectors.joining("|")))
            .collect(Collectors.toList());
  }

  /**
   * @return the conceptsForMissingTerms
   */
  public ConcurrentNavigableMap<Integer, List<ConceptMatch>> getConceptsForMissingTerms() {
    return conceptsForMissingTerms;
  }

  /**
   * @param conceptsForMissingTerms the conceptsForMissingTerms to set
   */
  public void setConceptsForMissingTerms(ConcurrentNavigableMap<Integer, List<ConceptMatch>> conceptsForMissingTerms) {
    this.conceptsForMissingTerms = conceptsForMissingTerms;
  }

  /**
   * @return the db
   */
  public DB getDb() {
    return db;
  }

  /**
   * @return the missingConceptNames
   */
  public List<String[]> getMissingConceptNames() {
    return missingConceptNames;
  }

  /**
   * @param missingConceptNames the missingConceptNames to set
   */
  public void setMissingConceptNames(List<String[]> missingConceptNames) {
    this.missingConceptNames = missingConceptNames;
  }

  /**
   * @return the missingMappingNames
   */
  public List<String[]> getMissingMappingNames() {
    return missingMappingNames;
  }

  /**
   * @param missingMappingNames the missingMappingNames to set
   */
  public void setMissingMappingNames(List<String[]> missingMappingNames) {
    this.missingMappingNames = missingMappingNames;
  }

  /**
   *
   * @return the missingTerms
   */
  public ConcurrentNavigableMap<Integer, String[]> getMissingTerms() {
    return missingTerms;
  }

  /**
   * @param missingTerms the missingTerms to set
   */
  public void setMissingTerms(ConcurrentNavigableMap<Integer, String[]> missingTerms) {
    this.missingTerms = missingTerms;
  }

  /**
   *
   * @return the number of missing concepts
   */
  public int missingConceptCount() {
    return getMissingConceptNames().size();
  }

  /**
   *
   * @param testCase
   * @return a Set of AttachmentHypotheses
   */
  protected Set<AttachmentHypothesis> findNearbyTermsWithGraphCore(String testCase) {
    return findNearbyTermsWithGraphCore(testCase, -1);
  }

  protected Set<AttachmentHypothesis> findAttachmentHypothesesForConceptMatches(List<ConceptMatch> matches) {
    long t1 = System.currentTimeMillis();
    Set<AttachmentHypothesis> hypotheses = new HashSet<>();
    Set<String> allTypes = new HashSet<>();
    Map<String, Double> typeWeights = new HashMap<>();
    Map<String, Double> conceptEvidence = new HashMap<>();

    List<String> termStrings = new ArrayList<>();
    matches.forEach((ConceptMatch m) -> {
      termStrings.add(m.getTerm());
    });

    if (matches.size() == 0) {
      // assertEquals("common_eiders", matches.get(10).term);
      System.out.println("Terms [" + termStrings + "] have no words in Word2Vec");
      return hypotheses; // which is empty at this point
      // fail("took unexpected exception:" + ex);
    }
    IntStream.range(0, matches.size())
            .forEach(i -> {
              ConceptMatch m = matches.get(i);
              //System.out.println(i + " " + m.toString());
              if (m.concept != null) {
                allTypes.add(m.concept);
                typeWeights.put(m.concept,
                        (typeWeights.containsKey(m.concept) ? typeWeights.get(m.concept) : 0.0d)
                        + m.similarity);
              }
            });
    allTypes.forEach(s -> {
      Double weight = typeWeights.get(s);
      Set<String> transTypes = ocyc.getTypesTransitiveURL(s);
      Set<String> immedTypes = ocyc.getTypesURL(s);

      Set<String> ret
              = Stream.concat(
                      transTypes
                      .stream()
                      .filter(type -> allTypes.contains(type)),
                      immedTypes.stream()
              ).collect(Collectors.toSet());

      if (!ret.isEmpty()) {
        ret.forEach(t -> {
          if (!conceptEvidence.containsKey(t)) {
            conceptEvidence.put(t, weight);
          } else {
            conceptEvidence.put(t, conceptEvidence.get(t) + weight);
          }
        });

      }
    });

    final double max = conceptEvidence.entrySet().stream()
            .mapToDouble(e -> e.getValue()).max().orElse(0);

    Set<String> maxc = conceptEvidence.entrySet().stream()
            .filter(e -> e.getValue() == max)
            .map(e -> e.getKey()).collect(Collectors.toSet());
    System.out.println("Maximum parent count:" + max);
    System.out.println("Maximal parents:"
            + maxc.stream().map(s -> ocyc.labelsForConcept(s) + ": " + s)
            .collect(Collectors.joining("\n\t")));
    maxc.forEach(c -> hypotheses.add(new AttachmentHypothesis(-1, termStrings,
            c, max, ocyc.labelsForConcept(c))));
    System.out.println("-----" + (System.currentTimeMillis() - t1) + "ms -----");
    return hypotheses;  // Since we take the max of a double, there should be only one
  }

  /**
   *
   * @param termStrings
   * @param n
   * @return a Set of AttachmentHypotheses
   */
  protected Set<AttachmentHypothesis>
          findNearbyTermsWithGraphCore(List<String> termStrings, int n) {
    long t1 = System.currentTimeMillis();
    Set<AttachmentHypothesis> hypotheses = new HashSet<>();

    Set<String> allTypes = new HashSet<>();
    Map<String, Double> typeWeights = new HashMap<>();

    Map<String, Double> conceptEvidence = new HashMap<>();
    System.out.print("====" + String.join("/", termStrings) + "====" + (n < 0 ? "" : " " + n) + " \t");
    List<ConceptMatch> matches = new ArrayList<>();
    for (String term : termStrings) {
      try {
        matches.addAll(cSpace.findNearestNForIn(term, 40, ocyc));
      } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      }
    }
    if (matches.size() == 0) {
      // assertEquals("common_eiders", matches.get(10).term);
      System.out.println("Terms [" + termStrings + "] have no words in Word2Vec");
      return hypotheses; // which is empty at this point
      // fail("took unexpected exception:" + ex);
    }
    IntStream.range(0, matches.size())
            .forEach(i -> {
              ConceptMatch m = matches.get(i);
              //System.out.println(i + " " + m.toString());
              if (m.concept != null) {
                allTypes.add(m.concept);
                typeWeights.put(m.concept,
                        (typeWeights.containsKey(m.concept) ? typeWeights.get(m.concept) : 0.0d)
                        + m.similarity);
              }
            });
    allTypes.forEach(s -> {
      Double weight = typeWeights.get(s);
      Set<String> transTypes = ocyc.getTypesTransitiveURL(s);
      Set<String> immedTypes = ocyc.getTypesURL(s);

      Set<String> ret
              = Stream.concat(
                      transTypes
                      .stream()
                      .filter(type -> allTypes.contains(type)),
                      immedTypes.stream()
              ).collect(Collectors.toSet());

      if (!ret.isEmpty()) {
        ret.forEach(t -> {
          if (!conceptEvidence.containsKey(t)) {
            conceptEvidence.put(t, weight);
          } else {
            conceptEvidence.put(t, conceptEvidence.get(t) + weight);
          }
        });

      }
    });

    final double max = conceptEvidence.entrySet().stream()
            .mapToDouble(e -> e.getValue()).max().orElse(0);

    Set<String> maxc = conceptEvidence.entrySet().stream()
            .filter(e -> e.getValue() == max)
            .map(e -> e.getKey()).collect(Collectors.toSet());
    System.out.println("Maximum parent count:" + max);
    System.out.println("Maximal parents:"
            + maxc.stream().map(s -> ocyc.labelsForConcept(s) + ": " + s)
            .collect(Collectors.joining("\n\t")));
    maxc.forEach(c -> hypotheses.add(new AttachmentHypothesis(n, termStrings,
            c, max, ocyc.labelsForConcept(c))));
    System.out.println("-----" + (System.currentTimeMillis() - t1) + "ms -----");
    return hypotheses;  // Since we take the max of a double, there should be only one
  }

  /**
   *
   * @param testCase
   * @param n
   * @return a Set of AttachmentHypotheses
   * @deprecated
   */
  @Deprecated
  protected Set<AttachmentHypothesis> findNearbyTermsWithGraphCore(String testCase, int n) {
    List<String> termStrings = new ArrayList<>();
    termStrings.add(testCase);
    return findNearbyTermsWithGraphCore(termStrings, n);

  }

  /**
   *
   * @return a List of names in the W2V space
   * @deprecated
   */
  @Deprecated  //Depends on a variable that is only set in an initialisation phase
  protected List<String> namesInW2V() {
    if (getMissingMappingNames() == null) {
      return null;
    }
    return getMissingMappingNames().stream()
            .filter(hasElementInW2V())
            .map(a -> a[0])
            .collect(Collectors.toList());
  }

  Predicate<String[]> hasElementInW2V() {
    return a -> Arrays.stream(a)
            .anyMatch(w2vs::knownTerm);
  }

}
