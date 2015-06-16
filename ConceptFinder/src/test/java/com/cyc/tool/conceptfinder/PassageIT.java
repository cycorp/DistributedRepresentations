/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyc.tool.conceptfinder;

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
    String text = "Attempting a board trick"; //* One or more people attempt to do a trick on a skateboard, snowboard, surfboard, or other boardsport board.";
    Passage instance = new Passage(text);
    instance.findConceptsForPassage();
  }

  /**
   * Test of narrowConceptsForPassage method, of class Passage.
   */
  @Test
  public void testNarrowConceptsForPassage() throws Exception {
    System.out.println("narrowConceptsForPassage");
    String text = "Attempting a board trick * One or more people attempt to do a trick on a skateboard, snowboard, surfboard, or other boardsport board.";
    
    Passage instance = new Passage(text);
    List<ConceptMatch> allMatches = instance.findConceptsForPassage();
    List<ConceptMatch> expResult = null;
    List<ConceptMatch> result = instance.narrowConceptsForPassage(allMatches);
    assertEquals(expResult, result);
  }
  
}
