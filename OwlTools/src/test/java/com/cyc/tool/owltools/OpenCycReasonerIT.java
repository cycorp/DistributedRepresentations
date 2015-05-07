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

import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Tests for OpenCycReasoner.
 */
public class OpenCycReasonerIT {

  public OpenCycReasonerIT() {
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
   * Test of get method, of class OpenCycReasoner.
   * @throws java.lang.Exception
   */
  @Test
  public void testGet() throws Exception {
    System.out.println("get");
    OpenCycReasoner result = OpenCycReasoner.get();
    assertTrue(result != null);
  }

  /**
   * Test of getAllClasses method, of class OpenCycReasoner.
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  @Test
  public void testGetAllClasses() throws OWLOntologyCreationException {
    System.out.println("getAllClasses");
    OpenCycReasoner instance = OpenCycReasoner.get();
    int expResultSize = 116842;
    Set<OWLClass> result = instance.getAllClasses();
    assertEquals(expResultSize, result.size());
  }

  /**
   * Test of getAllIRIs method, of class OpenCycReasoner.
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  @Test
  public void testGetAllIRIs() throws OWLOntologyCreationException {
    System.out.println("getAllIRIs");
    OpenCycReasoner instance = OpenCycReasoner.get();
    int expResult = 116842;
    List<String> result = instance.getAllIRIs();
    assertEquals(expResult, result.size());
  }

}
