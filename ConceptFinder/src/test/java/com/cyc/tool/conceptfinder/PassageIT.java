/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmoszko
 */
public class PassageIT {
  
  public PassageIT() {
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
   * Test of findConceptsForPassage method, of class Passage.
   */
  @Test
  public void testFindConceptsForPassage() throws Exception {
    System.out.println("findConceptsForPassage");
//    String text = "Attempting a board trick"; //* One or more people attempt to do a trick on a skateboard, snowboard, surfboard, or other boardsport board.";
   String text = "Feeding an animal / One or more people give food to an animal, which it eats.";
    Passage instance = new Passage(text);
    instance.findConceptsForPassage();
  }

  /**
   * Test of narrowConceptsForPassage method, of class Passage.
   */
  @Test
  public void testNarrowConceptsForPassage() throws Exception {
    System.out.println("narrowConceptsForPassage");
    String text = "Attempting a board trick / One or more people attempt to do a trick on a skateboard, snowboard, surfboard, or other boardsport board.";
    
    Passage instance = new Passage(text);
    List<ConceptMatch> allMatches = instance.findConceptsForPassage();
    List<ConceptMatch> expResult = null;
    List<ConceptMatch> result = instance.narrowConceptsForPassage(allMatches);
    assertEquals(expResult, result);
  }
  
}
