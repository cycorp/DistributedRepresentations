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

import com.cyc.tool.MapDBConfiguration;

/**
 * <P>
 * Config provides default locations for the DistributedRepresentations project.
 */
public class Config extends MapDBConfiguration {

  private static final String fallBackDBLocation = "/cyc/projects/kbTaxonomy/Experiments/ConceptFinder/";

  private static final String w2vDBFile = "/w2vdb";

  /**
   *
   * @return W2VDB file location
   */
  protected static String getW2vDBFile() {
    return getMapDBBase(fallBackDBLocation) + w2vDBFile;
  }

}
