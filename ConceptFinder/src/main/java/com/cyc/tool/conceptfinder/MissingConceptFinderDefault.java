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

import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * The default implementation for MissingConceptFinder.
 */
public class MissingConceptFinderDefault extends MissingConceptFinder {

  static final boolean reset = true;
  String[][] conceptStrings = {{"Facebook", "the Facebook"},
  {"telephone microphone"},
  {"telephone speaker"},
  {"backhoe"},
  {"facial scar", "scar on face"},
  {"blue eyes"},
  {"saluting the flag"},
  {"muddy paws"},
  {"strong muscles"},
  {"pan balance"},
  {"graduated cylinder"},
  {"tape measure"},
  {"hand lens"},
  {"measuring cup"}
  };
  List<String[]> conceptsToLookFor = Arrays.asList(conceptStrings);

  /**
   * MissingConceptFinderDefault constructor
   * 
   * @param w2v
   * @param oco
   * @throws IOException
   * @throws OWLOntologyCreationException
   */
  public MissingConceptFinderDefault(Word2VecSpace w2v, OpenCycOwl oco) throws IOException, OWLOntologyCreationException {
    this(w2v, oco, null);
  }

  /**
   * MissingConceptFinderDefault constructor
   * 
   * @param w2v
   * @param oco
   * @param cs
   * @throws IOException
   * @throws OWLOntologyCreationException
   */
  public MissingConceptFinderDefault(Word2VecSpace w2v, OpenCycOwl oco, ConceptSpace cs) throws IOException, OWLOntologyCreationException {
    super(w2v, oco, cs);
    missingTerms = db.getTreeMap(DefaultConceptFinderConfig.getMissingTermMapName());
    conceptsForMissingTerms = db.getTreeMap(DefaultConceptFinderConfig.getConceptsForMissingTermsName());
    if (reset) {
      missingTerms.clear();
    }
    if (missingTerms.isEmpty()) {
      conceptsForMissingTerms.clear();
      OpenCycOwl oc = new OpenCycOwl();

      missingMappingNames = conceptsToLookFor;
      missingConceptNames = missingMappingNames.stream()
              .filter(oc.noConcept())
              .collect(Collectors.toList());
      IntStream.range(0, missingConceptNames.size())
              .forEach(i -> missingTerms.put(i, missingConceptNames.get(i)));
      db.commit();
      db.compact();
      oc.close();

    } else {
      missingConceptNames = new ArrayList<>();
      missingTerms.keySet().forEach(k -> missingConceptNames.add(missingTerms.get(k)));
    }
  }

}
