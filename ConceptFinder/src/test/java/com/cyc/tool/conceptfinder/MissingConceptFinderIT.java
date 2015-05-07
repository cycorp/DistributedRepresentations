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

import com.cyc.tool.distributedrepresentations.GoogleNewsW2VSpace;
import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * MissingConceptFinder tests.
 */
public class MissingConceptFinderIT {

  static ConceptSpace cSpace;
  static List<String> cr = Arrays.asList("Chinese", "river");
  static MissingConceptFinder mcf;
  static Word2VecSpace mySpace;
  static OpenCycOwl ocyc;
  static List<String> pelagicBird = Arrays.asList("pelagic", "bird");

  public MissingConceptFinderIT() {
  }

  @BeforeClass
  public static void setUpClass() throws IOException, OWLOntologyCreationException {
    mySpace = GoogleNewsW2VSpace.get();
    cSpace = new ConceptSpace(mySpace);
    ocyc = new OpenCycOwl();
    mcf = new MissingConceptFinderDefault(mySpace, ocyc, cSpace);
  }

  @AfterClass
  public static void tearDownClass() {
    mySpace = null;
    ocyc.close();
  }
  private static String set2String(Set<Integer> s) {
    if (s.size()>10) return "";
    return s.stream()
            .map(i->{return String.join(",", mcf.getMissingTerms().get(i));})
            .collect(Collectors.joining(";"));
    
  }

  @Test
  public void conceptsWithTermsTest() {
    List<String> res = mcf.conceptsWithTerms();
    System.out.println("There are " + res.size() + " missing concepts with associated KB terms: " + res);
    assertTrue(res.size() + "elements expected none", res.size() == 0);
    // assertTrue(res.containsAll(Arrays.asList("start", "rust", "blueberry")));
  }

  @Test
  public void findNearbyTerms1() {
    long t1 = System.currentTimeMillis();
    System.out.println("FNT1");
    List<ConceptMatch> matches;
    try {
      matches = cSpace.findNearestNForIn(cr, 40, ocyc);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
      assertEquals("Chinese", matches.get(0).term);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void findNearbyTerms2() {
    try {
      long t1 = System.currentTimeMillis();
      System.out.println("FNT2");
      List<ConceptMatch> matches = cSpace.findNearestNForIn(cr, 40, ocyc);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");

      assertEquals(0.5539201713461387, matches.get(13).similarity, 0.000001);

    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);

    }

  }

  @Test
  public void findNearbyTerms3() {
    try {
      long t1 = System.currentTimeMillis();
      System.out.println("FNT3");
      List<ConceptMatch> matches = cSpace.findNearestNForIn(cr, 40, ocyc);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");

      assertEquals("creek", matches.get(7).term);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void findNearbyTerms4() {
    try {
      long t1 = System.currentTimeMillis();
      System.out.println("FNT4");
      List<ConceptMatch> matches = cSpace.findNearestNForIn(cr, 40, ocyc);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");

      assertEquals("riverbank", matches.get(12).term);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void findNearbyTermsWithGraphListTest() {
    System.out.println("FNT WG 3");
    IntStream.rangeClosed(3, 6)
            .forEach(ti -> {
              Arrays.asList(mcf.getMissingTerms().get(ti))
                      .forEach((String ss) -> {
                        mcf.findNearbyTermsWithGraphCore(ss, ti);
                      });
            });
    assertTrue(true);
  }
  
  @Test
  public void findNearbyTermsWithGraphTest1() {
    System.out.println("FNT WG 1");
    mcf.findNearbyTermsWithGraphCore("pelagic bird");
    assertTrue(true);
  }

  @Test
  public void findNearbyTermsWithGraphTest2(){
    System.out.println("FNT WG 2");
    mcf.findNearbyTermsWithGraphCore("tobacco shop");
    assertTrue(true);
  }

  @Test
  public void findNearbyTermsWithGraphTest3() {
    System.out.println("FNT WG 3");
    mcf.findNearbyTermsWithGraphCore("pelagic bird");
    mcf.findNearbyTermsWithGraphCore("tobacco shop");
    mcf.findNearbyTermsWithGraphCore("net melon");
    mcf.findNearbyTermsWithGraphCore("glowworm");
    mcf.findNearbyTermsWithGraphCore("tightrope walking");
    mcf.findNearbyTermsWithGraphCore("Adelie penguin");
    assertTrue(true);
  }

  @Test
  public void findNearbyTermsWithGraphTest4() {
    System.out.println("FNT WG 4");

    Set<AttachmentHypothesis> hyp = mcf.findNearbyTermsWithGraphCore("Adelie penguin");
    System.out.println("HYP" + hyp);
    assertEquals(1, hyp.size());
  }

  @Test
  public void findSomeMissingTerms1() {
    IntStream.rangeClosed(0, 3)
            .forEach(ti -> {
              Arrays.asList(mcf.getMissingTerms().get(ti))
                      .forEach((String ss) -> {
                        lookItUpWithOcyc(ss);
              });
            });
    assertTrue(true);
  }
  
  @Test
  public void findSomeMissingTerms2() {
    IntStream.of(1, 5, 7)
            //See https://docs.google.com/a/cyc.com/document/d/1Lwi21-yxcC0DGKJMcc4GFN3M_DBzEDAcSNjYufCRIfE/edit
            .forEach(ti -> {
              Arrays.asList(mcf.getMissingTerms().get(ti))
                      .forEach((String ss) -> {
                        lookItUpWithOcyc(ss);
                      });
            });
    assertTrue(true);
  }
  
  @Test
  public void findSomeMissingTerms3() {
    IntStream.of(2, 3, 6)
            //See https://docs.google.com/a/cyc.com/document/d/1Lwi21-yxcC0DGKJMcc4GFN3M_DBzEDAcSNjYufCRIfE/edit
            .forEach(ti -> {
              Arrays.asList(mcf.getMissingTerms().get(ti))
                      .forEach((String ss) -> {
                lookItUpAllW2V(ss);
                      });
            });
    assertTrue(true);
  }
  
  @Test
  public void howManyMissingTermsInW2V() throws IOException {
    final Set<Integer> found = new HashSet<>();
    final Set<Integer> foundSpace = new HashSet<>();
    final Set<Integer> unfound = new HashSet<>();
    
    mcf.getMissingTerms().keySet().forEach(i -> {
      Arrays.asList(mcf.getMissingTerms().get(i))
              .forEach((String ss) -> {
                if (mySpace.knownTerm(ss)) {
                  found.add(i);
                  if (ss.contains(" ")) {
                    foundSpace.add(i);
                  }
                } else {
                  unfound.add(i);
                }
              });
    });
    System.out.println("Found directly in W2V           : " + found.size()+" "+set2String(found));
    System.out.println("Found directly in W2V with space: " + foundSpace.size()+" "+set2String(foundSpace));
    System.out.println("Not found in W2V                : " + unfound.size()+" "+set2String(unfound));
    assertEquals(2, foundSpace.size());
    assertEquals(8, unfound.size());
  }
  
  @Test
  public void listSomeTest() {
    IntStream.rangeClosed(0, 8)
            .forEach(i -> {
              System.out.println(i + ":\t" + String.join(", ",
                              Arrays.asList(mcf.getMissingTerms().get(i))));
            });
    assertTrue(true);
  }

//  @Test
//  public void namesInW2VTest() {
//    List<String> res;
//    res = mcf.namesInW2V();
//    assertEquals(12343, res.size());
//  }
  @Test
  public void missingConceptCountTest() {
    assertEquals(9, mcf.missingConceptCount());
  }
  
  private void lookItUpAllW2V(String ss) {
    try {
      System.out.println("=======[" + ss + "]=======");
      long t1 = System.currentTimeMillis();
      List<ConceptMatch> matches
              = cSpace.findNearestNFor(Arrays.asList(ss.split("\\s+")), 40);
      
      System.out.println("Matches:" + (matches == null ? "null" : matches.size()));
      IntStream.range(0, matches.size())
              .forEach(i -> {
                String matchTerm = matches.get(i).term;
                String mat = matches.get(i).toString();
                if (ocyc.knownTerm(matchTerm)) {
                  // System.out.println("Known:" +matchTerm);
                  // System.out.println("Match is: "+ocyc.conceptsFor(matchTerm));
                  mat = mat.replace("---",
                          String.join(" | ", ocyc.conceptsFor(matchTerm)));
                }
                System.out.println(i + " " + mat);
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      System.out.println("--- position not known in word to vec space:[" + ss + "]");
      // Logger.getLogger(MissingVideoConceptFinderTest.class.getName()).log(Level.INFO, null, ex);
    }
  }
  
  private void lookItUpWithOcyc(String ss) {
    try {
      System.out.println("=======[" + ss + "]=======");
      long t1 = System.currentTimeMillis();
      List<ConceptMatch> matches
              = cSpace.findNearestNForIn(Arrays.asList(ss.split("\\s+")), 40, ocyc);
      
      System.out.println("Matches:" + (matches == null ? "null" : matches.size()));
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      System.out.println("--- position not known in word to vec space:[" + ss + "]");
      // Logger.getLogger(MissingVideoConceptFinderTest.class.getName()).log(Level.INFO, null, ex);
    }
  }
}
