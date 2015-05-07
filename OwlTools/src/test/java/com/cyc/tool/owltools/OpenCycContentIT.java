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

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Tests for OpenCycContent.
 * 
 */
public class OpenCycContentIT {

  public OpenCycContentIT() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of generateHtmlForConcept method, of class OpenCycContent.
   * @throws java.lang.Exception
   */
  @Test
  public void testGenerateHtmlForConcept() throws Exception {
    System.out.println("generateHtmlForConcept");
    OpenCycContent instance = new OpenCycContent("Mx4rKJAessNqRP6Yzb7lBhCrwQ"); // #$DogBreedShow;;
    String result = instance.generateHtmlForConcept();
    System.out.println(result);
    assertTrue(result.contains("<p>English Phrases: </p>"));
    
  }

  /**
   * Test of getCommentsForConceptFromOWL method, of class OpenCycContent.
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  @Test
  public void testGetCommentsForConcept() throws OWLOntologyCreationException {
    System.out.println("getCommentsForConcept");
    OpenCycContent instance = new OpenCycContent("Mx4rKJAessNqRP6Yzb7lBhCrwQ"); // #$DogBreedShow
    int expResultSize = 1;
    Set<String> result = instance.getCommentsForConcept();
    System.out.println("Comments: " + result);
    assertEquals(expResultSize, result.size());

  }

  /**
   * Test of getLabelForConcept method, of class OpenCycContent.
   * @throws java.lang.Exception
   */
  @Test
  public void testGetLabelForConcept() throws Exception {
    System.out.println("getLabelForConcept");
    OpenCycContent instance = new OpenCycContent("Mx4rKJAessNqRP6Yzb7lBhCrwQ"); // #$DogBreedShow;
    String expResult = "DogBreedShow";
    String result = instance.getLabelForConcept();
    System.out.println("Label: " + result);
    assertEquals(expResult, result);
  }

  /**
   * Test of getPrettyStringsForConceptFromOWL method, of class OpenCycContent.
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  @Test
  public void testGetPrettyStringsForConcept() throws OWLOntologyCreationException {
    System.out.println("getPrettyStringsForConcept");
    OpenCycContent instance = new OpenCycContent("Mx4rKJAessNqRP6Yzb7lBhCrwQ"); // #$DogBreedShow
    int expResultSize = 7;
    Set<String> result = instance.getPrettyStringsForConcept();
    System.out.println("Pretty Strings: " + result);
    assertEquals(expResultSize, result.size());

  }

  /**
   * Test of getTypesForConcept method, of class OpenCycContent.
   * @throws java.lang.Exception
   */
  @Test
  public void testGetTypesForConcept() throws Exception {
    System.out.println("getTypesForConcept");
    OpenCycContent instance = new OpenCycContent("Mx4rKJAessNqRP6Yzb7lBhCrwQ"); // #$DogBreedShow;;
    Set<String> expResult = new HashSet<>();
    expResult.add("Mx4r7LaSPmtpQfiSSf5yKM70tg");
    Set<String> result = instance.getTypesForConcept();
    System.out.println("Types: " + result);
    assertEquals(expResult, result);

  }

}
