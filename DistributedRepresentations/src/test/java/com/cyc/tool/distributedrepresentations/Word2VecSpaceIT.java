package com.cyc.tool.distributedrepresentations;

/*
 * #%L
 * DistributedRepresentations
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
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for Word2VecSpace.
 */
public class Word2VecSpaceIT {

  static List<String> cr = Arrays.asList("Chinese", "river");
  static Word2VecSpace mySpace;

  public Word2VecSpaceIT() {
  }

  @BeforeClass

  public static void setUpClass() throws IOException {
    mySpace = GoogleNewsW2VSpace.get();
  }

  @AfterClass

  public static void tearDownClass() {
    mySpace = null;
  }
  // 

  @Test
  public void distanceTest() {
    assertEquals(1.0, mySpace.cosineSimilarity("skimpy bathing suits", "skimpy_bathing_suits"), 0.00000001);
    assertEquals(0.24279, mySpace.cosineSimilarity("skimpy bathing suits", "Giant Octopus"), 0.0001);
    assertEquals(0.54801, mySpace.cosineSimilarity("skimpy bathing suits", "bathing suits"), 0.0001);
    assertEquals(0.645069, mySpace.cosineSimilarity("apple", "pear"), 0.0001);
    assertEquals(0.20749, mySpace.cosineSimilarity("apple", "cat"), 0.0001);
    
    assertTrue(mySpace.cosineSimilarity("apple", "pear")
            > mySpace.cosineSimilarity("apple", "cat"));
  }

  @Test
  public void getVectorTest1() {
    assertEquals(-0.05338118f, (mySpace.getVector("skimpy bathing suits")[5]), 0.000001);
    assertEquals(0.047296f, (mySpace.getVector("skimpy bathing suits")[105]), 0.000001);
  }

  @Test
  public void getVectorTest2a() {
    assertEquals(-0.049851f, (mySpace.getVector("Chinese")[0]), 0.000001);
    assertEquals(-0.090444f, (mySpace.getVector("Chinese")[5]), 0.000001);
  }

  @Test
  public void getVectorTest2b() {
    assertEquals(0.002663f, (mySpace.getVector("river")[0]), 0.000001);
    assertEquals(-0.029231f, (mySpace.getVector("river")[5]), 0.000001);
  }

  @Test
  public void googleDistanceTest1() {
    try {
      assertEquals(0.667376,
              mySpace.googleSimilarity(cr, "Yangtze_River"), 0.0001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleDistanceTest2() {
    try {
      assertEquals(0.594108,
              mySpace.googleSimilarity(cr, "Hongze_Lake"), 0.0001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleDistanceTest3() {
    try {
      assertEquals(0.604726,
              mySpace.googleSimilarity(cr, "Huangpu_River"), 0.0001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleNormVectorTest0() {
    try {
      float[] norm = mySpace.getGoogleNormedVector(cr);
      assertEquals(-0.032075, norm[0], 0.000001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleNormVectorTest100() {
    float[] norm;
    try {
      norm = mySpace.getGoogleNormedVector(cr);
      assertEquals(-0.095236, norm[100], 0.000001);

    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleNormVectorTest5() {
    try {
      float[] norm = mySpace.getGoogleNormedVector(cr);
      assertEquals(-0.081347, norm[5], 0.000001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void googleNormVectorTest50() {
    try {
      float[] norm = mySpace.getGoogleNormedVector(cr);
      assertEquals(0.080537, norm[50], 0.000001);
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  /**
   * Test if known terms have been loaded from the Word2Vec file or DB
   */
  @Test
  public void knownTermTest() {
    // System.out.println("DB Size:" + vectors.size());
    
    assertTrue(mySpace.knownTerm("Yathra"));
    assertTrue(mySpace.knownTerm("skimpy bathing suits"));
    assertTrue(mySpace.knownTerm("Giant_Octopus"));
    assertTrue(mySpace.knownTerm("Yangtze_River"));
    assertTrue(mySpace.knownTerm("Chinese"));
    // assertTrue(mySpace.knownTerm("Chinese River"));

  }

//  @Test
//  public void findNearbyTerms1() {
//    try {
//      long t1 = System.currentTimeMillis();
//      List<ConceptMatch> matches = mySpace.findNearestNForWithInputTermFiltering(cr, 40);
//      IntStream.range(0, matches.size())
//              .forEach(i -> {
//                System.out.println(i + " " + matches.get(i).toString());
//              });
//      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
//      assertEquals(matches.get(0).getTerm(), "Yangtze_River");
//      assertEquals(0.604726, matches.get(5).getSimilarity(), 0.000001);
//
//      assertEquals(matches.get(23).getTerm(), "rivers");
//    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
//      fail("took unexpected exception:" + ex);
//    }
//  }
//
//  @Test
//
//  public void findNearbyTerms2() {
//    try {
//      long t1 = System.currentTimeMillis();
//      List<ConceptMatch> matches = mySpace.findNearestNForWithInputTermFiltering(Arrays.asList("gangplank"), 40);
//      IntStream.range(0, matches.size())
//              .forEach(i -> {
//                System.out.println(i + " " + matches.get(i).toString());
//              });
//      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
//    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
//      fail("took unexpected exception:" + ex);
//    }
//  }
  @Test
  public void testNGramsFor() {
    List<String> res = Word2VecSpace.nGramsFor(Arrays.asList("this", "is", "a", "test"));
    // System.out.println("test: "+res+" len:"+res.size());    

    assertEquals(10, res.size());
  }

  @Test
  public void testNGramsForCR() {
    List<String> res = Word2VecSpace.nGramsFor(cr);
    System.out.println("test: " + res + " len:" + res.size());
    assertEquals(3, res.size());
  }

}
