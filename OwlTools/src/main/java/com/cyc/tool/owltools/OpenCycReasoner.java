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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * <P>
 * OpenCycReasoner provides access to methods that {@link OpenCycContent} uses to get information
 * out of the OpenCyc OWL file.
 *
 */
public class OpenCycReasoner {

  private static List<String> allClassIRIs = null;
  private static Set<OWLClass> allClasses = null;
  private static OWLAnnotationProperty comment = null;
  private static OWLDataFactory dataFactory = null;
  private static OWLAnnotationProperty label = null;
  private static OWLOntologyManager manager = null;

  private static OpenCycReasoner me = null;

  private static OWLOntology openCyc = null;

  private static OWLAnnotationProperty prettyString = null;

  private static OWLReasoner reasoner = null;
  private static OWLReasonerFactory reasonerFactory = null;
  static final String ocycLocation = OwlToolsConfig.ocycLocation;

  /**
   * Creates a new instance of OpenCycReasoner.
   */
  private OpenCycReasoner() throws OWLOntologyCreationException {
    manager = OWLManager.createOWLOntologyManager();
    openCyc = getManager()
            .loadOntologyFromOntologyDocument(new FileDocumentSource(new File(getOcycLocation())));
    reasonerFactory = new StructuralReasonerFactory();
    reasoner = getReasonerFactory().createReasoner(getOpenCyc());
    dataFactory = getManager().getOWLDataFactory();
    prettyString = getDataFactory().getOWLAnnotationProperty(IRI.create("http://sw.opencyc.org/concept/Mx4rwLSVCpwpEbGdrcN5Y29ycA"));
    comment = getDataFactory().getRDFSComment();
    label = getDataFactory().getOWLAnnotationProperty("label", new DefaultPrefixManager("http://sw.cyc.com/CycAnnotations_v1#"));

    allClasses = openCyc.getClassesInSignature();
    allClassIRIs = getIRIs(allClasses);
  }

  /**
   * Factory method to get an OpenCycReasoner instance.
   *
   * @return an OpenCycReasoner
   * @throws OWLOntologyCreationException
   */
  public static OpenCycReasoner get() throws OWLOntologyCreationException {
    if (me == null) {
      me = new OpenCycReasoner();
    }
    return me;
  }

  /**
   *
   * @return allClasses
   */
  public Set<OWLClass> getAllClasses() {
    return allClasses;
  }

  /**
   *
   * @return allClassIRIs
   */
  public List<String> getAllIRIs() {
    return allClassIRIs;
  }

  /**
   * @return the comment
   */
  public OWLAnnotationProperty getComment() {
    return comment;
  }

  /**
   * @return the dataFactory
   */
  public OWLDataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   *
   * @return the label
   */
  public OWLAnnotationProperty getLabel() {
    return label;
  }

  /**
   * @return the manager
   */
  public OWLOntologyManager getManager() {
    return manager;
  }

  /**
   * @return the ocycLocation
   */
  public String getOcycLocation() {
    return ocycLocation;
  }

  /**
   * @return the openCyc
   */
  public OWLOntology getOpenCyc() {
    return openCyc;
  }

  /**
   * @return the prettyString
   */
  public OWLAnnotationProperty getPrettyString() {
    return prettyString;
  }

  /**
   * @return the reasoner
   */
  public OWLReasoner getReasoner() {
    return reasoner;
  }

  /**
   * @return the reasonerFactory
   */
  public OWLReasonerFactory getReasonerFactory() {
    return reasonerFactory;
  }

  private List<String> getIRIs(Set<OWLClass> allClasses) {
    List<String> allIRIs = new ArrayList<>();
    allClasses.forEach(c -> {
      String iri = c.getIRI().getFragment();
      allIRIs.add(iri);
    });
    return allIRIs;
  }

}
