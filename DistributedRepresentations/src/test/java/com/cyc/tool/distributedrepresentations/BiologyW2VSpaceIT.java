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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for BiologyW2VSpace.
 */
public class BiologyW2VSpaceIT {

  public BiologyW2VSpaceIT() {
  }

  @Test
  public void testGet() {
    System.out.println("get");

    BiologyW2VSpace result = BiologyW2VSpace.get();
    assertTrue(result != null);
  }

  @Test
  public void testNumberOfVectors() {
    System.out.println("getNVectors");

    int result = BiologyW2VSpace.get().getNVectors();

    assertEquals(result, 1701632);
  }

}
