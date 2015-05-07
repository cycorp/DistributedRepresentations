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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * ConceptSpace tests.
 */
public class ConceptSpaceIT {

  static List<String> cr = Arrays.asList("Chinese", "river");
  static ConceptSpace mySpace;

  public ConceptSpaceIT() {
  }

  @BeforeClass

  public static void setUpClass() throws IOException, OWLOntologyCreationException {
    mySpace = new ConceptSpace(GoogleNewsW2VSpace.get());

  }

  @AfterClass

  public static void tearDownClass() {
    mySpace = null;
  }

  @Test
  public void findNearbyTerms1() {
    try {
      long t1 = System.currentTimeMillis();
      List<ConceptMatch> matches = mySpace.findNearestNForWithInputTermFiltering(cr, 40);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
      assertEquals(matches.get(0).getTerm(), "Yangtze_River");
      assertEquals(0.6047259562339493, matches.get(5).getSimilarity(), 0.000001);

      assertEquals(matches.get(23).getTerm(), "rivers");
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Test
  public void findNearbyTerms2() {
    try {
      long t1 = System.currentTimeMillis();
      List<ConceptMatch> matches = mySpace.findNearestNForWithInputTermFiltering(Arrays.asList("gangplank"), 40);
      IntStream.range(0, matches.size())
              .forEach(i -> {
                System.out.println(i + " " + matches.get(i).toString());
              });
      System.out.println("Took " + (System.currentTimeMillis() - t1) + "ms");
    } catch (Word2VecSpace.NoWordToVecVectorForTerm ex) {
      fail("took unexpected exception:" + ex);
    }
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

}
