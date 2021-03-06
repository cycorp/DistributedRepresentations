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

//import com.cyc.tool.distributedrepresentations.GoogleNewsW2VSpace;
//import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Tests for OpenCycOwl.
 */
public class OpenCycOwlIT {

  static OpenCycOwl ocyc;

  public OpenCycOwlIT() {
  }

  @BeforeClass
  public static void setUpClass() throws IOException, OWLOntologyCreationException {
    ocyc = new OpenCycOwl();

  }

  @AfterClass
  public static void tearDownClass() {
    // Remove the ontology from the manager
    ocyc.close();
  }

  @Test
  public void conceptForTest1() {
    Set<String> res = ocyc.conceptsFor("the Yangtze");
    assertEquals(1, res.size());
    assertTrue(res.contains("http://sw.opencyc.org/concept/Mx4rvVj5qJwpEbGdrcN5Y29ycA"));
  }

  @Test
  public void conceptsForBirdTest1() {
    Set<String> res = ocyc.conceptsFor("Bird");
    System.out.println("HEY Bird "+res);
    assertEquals(2,res.size());
    assertTrue(res.contains("http://sw.opencyc.org/concept/Mx4rvVi8SJwpEbGdrcN5Y29ycA"));
  }

  @Test
  public void conceptsForBirdTest2() {
    Set<String> res = ocyc.conceptsFor("bird");
    System.out.println("HEY bird "+res);
    assertEquals(2, res.size());
    assertTrue(res.contains("http://sw.opencyc.org/concept/Mx4rvVi8SJwpEbGdrcN5Y29ycA"));
  }
  
//  @Test
//  public void conceptsWithTermsTest() throws IOException {
//    Set<String> res = ocyc.conceptsWithW2VTerms();
//    assertEquals(49616, res.size());
//  }
  
  @Test
  public void getLabelsTest() {
    int res = ocyc.ocycConceptForTermLabel.size();
    //ocyc.ocycConceptForTermLabel.keySet().forEach(s->{
    // System.out.println(s+"\t"+ocyc.ocycConceptForTermLabel.get(s));
    // });
    System.out.println("N RDFS Labels with concepts:" + res);
    assertEquals(240258, res);
  }

  @Test
  public void getLowerCaseStringTest() {
    int res = ocyc.ocycConceptForTermLower.size();
    System.out.println("N downcased pretty strings or labels with concepts:" + res);
    assertEquals(576678, res);
  }

  @Test
  public void getNConceptsTest() {
    int res = ocyc.size();
    System.out.println("N Classes:" + res);
    assertEquals(116842, res);
  }

  @Test
  public void getPrettyStringTest() {
    int res = ocyc.ocycConceptForTermPrettyString.size();
    System.out.println("N pretty Strings with concepts:" + res);
    assertEquals(345298, res);
  }

  @Test
  public void getTypesTest() throws IOException {
    Set<String> res = ocyc.getTypes(ocyc.pizzaGUID);
    res.forEach(s -> {
      System.out.println("Pizza: " + ocyc.guidToURLString(s));
    });
    assertEquals(4, res.size());
  }

  @Test
  public void getTypesTransitiveTest() throws IOException {
    Set<String> res = ocyc.getTypesTransitive(ocyc.pizzaGUID);
    res.forEach(s -> {
      System.out.println("Pizza: " + ocyc.guidToURLString(s));
    });
    assertEquals(62, res.size());
  }

  @Test
  public void guidFromURLStringTest() {
    String res = ocyc.guidFromURLString(ocyc.guidToURLString(ocyc.pizzaGUID));
    assertEquals(ocyc.pizzaGUID, res);
  }

  @Test
  public void knownTermTest1() {
    boolean res = ocyc.knownTerm("Yangtze_River");
    assertTrue(res);
  }

  @Test
  public void knownTermTest1b() {
    // Tests whether terms starting with "the " like "the Yangtze River" are
    // also being added without the "the "
    boolean res = ocyc.knownTerm("Yangtze River");
    assertTrue(res);
  }

  @Test
  public void knownTermTest2() {
    boolean res = ocyc.knownTerm("the Yangtze");
    assertTrue(res);
  }

  @Test
  public void knownTermTest3() {
    boolean res = ocyc.knownTerm("rivers");
    assertTrue(res);

  }

    @Test
    public void knownTermTest4() {
      boolean res = ocyc.knownTerm("Hubble_Space_Telescope");
      assertTrue(res);

  }
    @Test
    public void stringsForBirdConceptTest() {
      String res = ocyc.labelsForConcept("http://sw.opencyc.org/concept/Mx4rvVi8SJwpEbGdrcN5Y29ycA");
      assertEquals("Birding|bird|Birds|Birder|Aves|birds|fowl", res);
  }
  
  @Test
  public void stringsForConceptTest1() {
    String res = ocyc.labelsForConcept("http://sw.opencyc.org/concept/Mx4rvVj5qJwpEbGdrcN5Y29ycA");
    assertEquals("Chang Jiang|the Yangtze River|Yangtze|Chang Jiang River|the Yangtze|the Chang Jiang|Yangtze River|the Chang Jiang River", res);
  }
  
//  @Test
//  public void testConceptMap() throws IOException {
//    Word2VecSpace sp = GoogleNewsW2VSpace.get();
//    Set<String> yesses = new HashSet<>();
//    Set<String> allTerms = new HashSet<>();
//    Iterables.concat(
//            ocyc.ocycConceptForTermPrettyString.keySet(),
//            ocyc.ocycConceptForTermLabel.keySet(),
//            ocyc.ocycConceptForTermLower.keySet()).forEach(lit -> {
//              if (sp.knownTerm(lit)) {
//                yesses.add(lit);
//              }
//              allTerms.add(lit);
//              
//            });
//    System.out.println("Term strings for ocyc contained in W2V knownterm test:");
//    System.out.println("\tYes:" + yesses.size());
//    System.out.println("\t No:" + (allTerms.size() - yesses.size()));
//    System.out.println("\tAll:" + allTerms.size());
//    // System.out.println("Yesses: \n" + String.join(", ", yesses));
//    // System.out.println("Nos: \n" + String.join("; ", allTerms));
//    assertEquals(67532, yesses.size());
//    assertEquals(886523, allTerms.size());
//  }
}

