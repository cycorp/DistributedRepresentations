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

//import com.cyc.tool.distributedrepresentations.GoogleNewsW2VSpace;
//import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * <P>
 * OpenCycOwl has methods for accessing information in an OpenCyc OWL file. 
 * There is some known overlap with this class, {@link OpenCycReasoner}, 
 * and {@link OpenCycContent}.
 *
 */
public class OpenCycOwl {


  static final String ocycLocation = OwlToolsConfig.ocycLocation;

  /**
   * HLID for testing puproses.
   */
  public String pizzaGUID = "Mx4rvVibapwpEbGdrcN5Y29ycA";
  private final boolean clearLabels = false;
    private final OWLDataFactory dataFactory;
  private final OWLOntologyManager manager;
  private OWLOntology openCyc;
  private final OWLAnnotationProperty prettyString;
  private final OWLAnnotationProperty rdfsLabel;
  private OWLReasoner reasoner;
  private final OWLReasonerFactory reasonerFactory;

  private long t; // time keeper
  Set<String> allConcepts;
  final Map<String, Set<String>> conceptLabels;
  Set<String> conceptsWithTerms;
  DB db;
  ConcurrentNavigableMap<String, Set<String>> ocycConceptForTermLabel;
  ConcurrentNavigableMap<String, Set<String>> ocycConceptForTermLower;
  ConcurrentNavigableMap<String, Set<String>> ocycConceptForTermPrettyString;
  ConcurrentNavigableMap<String, Set<String>> typeGraph;

  /**
   * Creates a new instance of OwlTest.
   * @throws java.io.IOException
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  public OpenCycOwl() throws IOException, OWLOntologyCreationException {

    // A simple example of how to load and save an ontology We first need to
    // obtain a copy of an OWLOntologyManager, which, as the name suggests,
    // manages a set of ontologies. An ontology is unique within an ontology
    // manager. Each ontology knows its ontology manager. To load multiple
    // copies of an ontology, multiple managers would have to be used.
    manager = OWLManager.createOWLOntologyManager();
    // We load an ontology from a document IRI - in this case we'll load the
    // pizza ontology.
    // IRI documentIRI = IRI.create(PIZZA_IRI);
    // Now ask the manager to load the ontology
    // OWLOntology ontology = manager
    // .loadOntologyFromOntologyDocument(documentIRI);
    // but in this test we don't rely on a remote ontology and load it from
    // a string
    //play with mapr
    // System.out.println(Arrays.asList(1,2,3,4,5,6,7,8).stream().map(x->x*x).reduce((x,y)->x+y).get());

    db = DBMaker.newFileDB(new File(OwlToolsConfig.getOcycTermDBFile()))
        .closeOnJvmShutdown()
        //      .encryptionEnable("password")
        .make();

    reasonerFactory = new StructuralReasonerFactory();
    dataFactory = manager.getOWLDataFactory();
    prettyString = dataFactory.getOWLAnnotationProperty(
        guidToIRI("Mx4rwLSVCpwpEbGdrcN5Y29ycA"));
    rdfsLabel = dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    this.getPrettyStringToConceptMap();
    this.getRDFSLabelConceptMap();
    this.getLowerCaseConceptMap();
    this.createTypeGraph();
    conceptLabels = new HashMap<>();
    this.fillConceptLabels();
  }

  /**
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    
    OpenCycOwl my = new OpenCycOwl();
    System.out.println("N Classes:" + my.getOpenCyc().getClassesInSignature().size());
    my.pizzaTest();
// Remove the ontology from the manager
    my.manager.removeOntology(my.getOpenCyc());
  }

  /**
   *
   * @return the allConcepts Set
   * @throws IOException
   */
  public Set<String> allConcepts() throws IOException {
    
    allConcepts = db.getHashSet(OwlToolsConfig.getAllConceptsName());
    if (allConcepts.isEmpty()) {
      Set<String> res
              = getOpenCyc().
                      getClassesInSignature()
                      .stream()
                      .map(clss -> {
                        String csid = clss.toStringID();
                        String s = guidFromURLString(csid);
                        System.out.println("AC:" + csid + "   " + s);
                        return s;
                      })
                      .collect(Collectors.toSet());
      allConcepts.addAll(res);
      db.commit();
    }
    return allConcepts;
  }

  /**
   * Close the ontology access
   */
  public void close() {
    if (openCyc != null) {
      manager.removeOntology(openCyc);
    }
  }
  
  /**
   *
   * @param term
   * @return all concepts for a given term String
   */
  public Set<String> conceptsFor(String term) {
    Set<String> ret = new HashSet<>();
    if (ocycConceptForTermPrettyString.containsKey(term)) {
      ret.addAll(ocycConceptForTermPrettyString.get(term));
    }
    if (ocycConceptForTermLabel.containsKey(term)) {
      ret.addAll(ocycConceptForTermLabel.get(term));
    }
    String l = term.toLowerCase(Locale.ENGLISH);
    if (ocycConceptForTermLower.containsKey(l)) {
      ret.addAll(ocycConceptForTermLower.get(l));
    }
    if (term.contains("_")) {
      ret.addAll(conceptsFor(term.replace("_", " ")));
    }
    return ret;
  }



  /**
   *
   * @return Set of concepts with terms in the W2V space
   * @throws IOException
   */
//  public Set<String> conceptsWithW2VTerms() throws IOException {
//    /* @Todo: Consider making this more independent of the particular W2V space */
//    Word2VecSpace w2v = GoogleNewsW2VSpace.get();
//    conceptsWithTerms = db.getHashSet(OwlToolsConfig.getConceptsWithTermsName());
//    if (conceptsWithTerms.isEmpty()) {
//      Set<String> res
//              = Stream.concat(
//                      Stream.concat(
//                              ocycConceptForTermPrettyString.entrySet().stream(),
//                              ocycConceptForTermLabel.entrySet().stream()),
//                      ocycConceptForTermLower.entrySet().stream())
//                      .filter(s -> w2v.knownTerm(s.getKey()))
//                      .map(s -> s.getValue())
//                      .flatMap(conceptSet -> conceptSet.stream())
//                      .collect(Collectors.toSet());
//      conceptsWithTerms.addAll(res);
//      db.commit();
//    }
//    return conceptsWithTerms;
//  }
  
  /**
   *
   * @param forT
   * @return Set of types for a term
   */
  public Set<String> getTypes(String forT) {
    Set<String> ret = new HashSet<>();
    if (typeGraph.containsKey(forT)) {
      return typeGraph.get(forT);
    }
    if (forT.equals("Thing")) {
      return ret;
    }
    //  System.out.println("No types for :" + guidToURLString(forT));
    return ret;
  }
  
  /**
   *
   * @param conceptGUID
   * @return Set of types for a concept
   * @throws OWLOntologyCreationException
   */
  public Set<String> getTypesForConceptFromOWL(String conceptGUID) throws OWLOntologyCreationException {
    
    Set<String> types = new HashSet<>();
    OWLClass concept
            = dataFactory.getOWLClass(guidToIRI(conceptGUID));
    NodeSet<OWLClass> subClasses = getReasoner()
            .getSuperClasses(concept, true);
    subClasses.forEach(node -> {
      Set<OWLClass> ents = node.getEntities();
      ents.forEach(ent -> {
        types.add(ent.getIRI().getShortForm());
      });
    });
    return types;
  }
  
  /**
   *
   * @param forT
   * @return Set of types for a term
   */
  public Set<String> getTypesTransitive(String forT) {
    Set<String> ret = new HashSet<>();
    if (typeGraph.containsKey(forT)) {

      typeGraph
          .get(forT)
              .forEach(t -> {
                getTypesTransitive(t, ret);
              });
      return ret;
    }
    // System.out.println("PROBLEM: " + forT);
    return ret;
  }
  
  /**
   *
   * @param forT
   * @return Set of types for a term
   */
  public Set<String> getTypesTransitiveURL(String forT) {
    return getTypesTransitive(guidFromURLString(forT))
            .stream()
            .map(t -> guidToURLString(t))
            .collect(Collectors.toSet());
  }
  
  /**
   *
   * @param forT
   * @return Set of types of a term
   */
  public Set<String> getTypesURL(String forT) {
    return getTypes(guidFromURLString(forT))
            .stream()
            .map(t -> guidToURLString(t))
            .collect(Collectors.toSet());
  }

  /**
   *
   * @param url
   * @return GUID from a URL
   */
  public String guidFromURLString(String url) {
    return url.replaceFirst("http://sw.opencyc.org/concept/", "");
  }

  /**
   *
   * @param conceptGuid
   * @return URL from a GUID
   */
  public String guidToURLString(String conceptGuid) {
    return "http://sw.opencyc.org/concept/" + conceptGuid;
  }

  /**
   *
   * @param term
   * @return true if term is in the ontology
   */
  public boolean knownTerm(String term) {
    if (ocycConceptForTermPrettyString.containsKey(term)) {
      return true;
    }
    if (ocycConceptForTermLabel.containsKey(term)) {
      return true;
    }
    if (ocycConceptForTermLower.containsKey(term.toLowerCase(Locale.ENGLISH))) {
      return true;
    }
    if (term.contains("_")) {
      return knownTerm(term.replace("_", " "));
    }
    return false;
  }
  
  /**
   *
   * @param concept
   * @return a String with labels for the concept
   */
  public String labelsForConcept(String concept) {
    if (conceptLabels.containsKey(concept)) {
      return String.join("|", conceptLabels.get(concept));
    }
    return concept;
  }

  /**
   *
   * @return a Predicate to test if a concept is present
   */
  public Predicate<String[]> noConcept() {
    return a -> !Arrays.stream(a)
            .anyMatch(hasConcept());
  }
  
  /**
   *
   * @return Number of classes in the ontology
   */
  public int size() {
    return getOpenCyc().getClassesInSignature().size();
  }
  
  /**
   *
   * @return an OWLOntology for OpenCyc
   */
  protected OWLOntology getOpenCyc() {
    if (openCyc == null) {
      try {
        t = System.currentTimeMillis();
        openCyc = manager
                .loadOntologyFromOntologyDocument(
                        new FileDocumentSource(
                                new File(ocycLocation)));
        System.out.println("Open Cyc Load time:"
                + (System.currentTimeMillis() - t) + "ms");
      } catch (OWLOntologyCreationException ex) {
        Logger.getLogger(OpenCycOwl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return openCyc;
  }
  
  /**
   *
   * @return an OWLReasoner
   */
  protected OWLReasoner getReasoner() {
    if (reasoner == null) {
      reasoner = reasonerFactory.createReasoner(getOpenCyc());
    }
    return reasoner;
  }

  private void createTypeGraph() throws IOException {
    typeGraph = db.getTreeMap(OwlToolsConfig.getTypeGraphName());
    if (typeGraph.isEmpty()) {
      allConcepts().
              stream().
              map(c -> guidFromURLString(c))
              .forEach(s -> {
                try {
                  Set<String> types = getTypesForConceptFromOWL(s);
                  System.out.println("Types for " + s + ": " + types.size());
                  typeGraph.put(s, types);
                } catch (OWLOntologyCreationException ex) {
                  Logger.getLogger(OpenCycOwl.class.getName()).log(Level.SEVERE, null, ex);
                }
              });
      db.commit();
      db.compact();
    }

  }
  
  private void fillConceptLabels() {
    
    t = System.currentTimeMillis();
    Iterables.concat(ocycConceptForTermLabel.entrySet(),
            ocycConceptForTermLabel.entrySet(),
            ocycConceptForTermPrettyString.entrySet()).forEach(entry -> {
              Set<String> concepts = entry.getValue();
              concepts.forEach(concept -> {
                if (!conceptLabels.containsKey(concept)) {
                  conceptLabels.put(concept, new HashSet<>());
                }
                conceptLabels.get(concept).add(entry.getKey());
              });
            });
    System.out.println("Concept to term map creation:"
            + (System.currentTimeMillis() - t) + "ms");
  }
  
  private void getLowerCaseConceptMap() {
    
    ocycConceptForTermLower = db.getTreeMap(OwlToolsConfig.getOcycTermMapName() + "_Lower");
    if (clearLabels) {
      ocycConceptForTermLower.clear();
    }
    if (ocycConceptForTermLower.isEmpty()) {
      ocycConceptForTermPrettyString.keySet().forEach(s -> {
        storeDownCaseLabel(s, ocycConceptForTermPrettyString);
      });
      
      ocycConceptForTermLabel.keySet().forEach(s -> {
        storeDownCaseLabel(s, ocycConceptForTermLabel);
      });
      db.commit();
      db.compact();
    }

  }
  
  private void getPrettyStringToConceptMap() {
    // Print out all of the classes which are contained in the signature of
    // the ontology. These are the classes that are referenced by axioms in
    // the ontology.
    
    ocycConceptForTermPrettyString = db.getTreeMap(OwlToolsConfig.getOcycTermMapName());
    if (clearLabels) {
      ocycConceptForTermPrettyString.clear();
    }
    if (ocycConceptForTermPrettyString.isEmpty()) {
      Iterables.concat(
              getOpenCyc().getClassesInSignature(),
              getOpenCyc().getIndividualsInSignature()).forEach(owlObj -> {
                System.out.println("Loading PrettyStrings for "
                        + (owlObj instanceof OWLClass ? "Class" : "Individual") + ": " + owlObj);
                Collection<OWLAnnotation> annotations
                        = EntitySearcher.getAnnotations(owlObj, getOpenCyc(), prettyString);
                annotations.forEach(ann -> {
                  storeConceptLabel(ann, owlObj, ocycConceptForTermPrettyString);
                });
              });
      db.commit();
      db.compact();
      
    }
  }
  
  private void getRDFSLabelConceptMap() {
    // Print out all of the classes which are contained in the signature of
    // the ontology. These are the classes that are referenced by axioms in
    // the ontology.
    
    ocycConceptForTermLabel = db.getTreeMap(OwlToolsConfig.getOcycTermMapName() + "_Label");
    if (clearLabels) {
      ocycConceptForTermLabel.clear();
    }
    if (ocycConceptForTermLabel.isEmpty()) {
      // Get the terms for collections and individuals
      Iterables.concat(
              getOpenCyc().getClassesInSignature(),
              getOpenCyc().getIndividualsInSignature()).forEach(owlObj -> {
                System.out.println("Loading RDFS Labels for "
                        + (owlObj instanceof OWLClass ? "Class" : "Individual") + ": " + owlObj);
                Collection<OWLAnnotation> annotations
                        = EntitySearcher.getAnnotations(owlObj, getOpenCyc(), rdfsLabel);
                annotations.forEach(ann -> {
                  storeConceptLabel(ann, owlObj, ocycConceptForTermLabel);
                });
              });
      
      db.commit();
      db.compact();
    }
  }
  
  private void getTypesTransitive(String forT, Set<String> soFar) {
    if (!soFar.contains(forT)) {
      soFar.add(forT);
      if (forT.equals("Thing")) {
        return;
      }
      getTypes(forT)
              .forEach(st -> {
                getTypesTransitive(st, soFar);
              });

    }
  }
  
  private IRI guidToIRI(String conceptGuid) {
    return IRI.create(guidToURLString(conceptGuid));
  }

  private Predicate<String> hasConcept() {
    return a -> knownTerm(a);
  }
  
  private void pizzaTest() {
    // Now save a copy to another location in OWL/XML format (i.e. disregard
    // the format that the ontology was loaded in).
    //File f = folder.newFile("owlapiexample_example1.xml");
    //IRI documentIRI2 = IRI.create(f);
    //manager.saveOntology(ontology, new OWLXMLDocumentFormat(), documentIRI2);

    OWLClass pizza
            = dataFactory.getOWLClass(guidToIRI(pizzaGUID));
    
    NodeSet<OWLClass> subClses = getReasoner().getSubClasses(pizza, true);
    // Set<OWLObjectProperty>op=pizza.getObjectPropertiesInSignature();
    t = System.currentTimeMillis();
    Collection<OWLAnnotation> anns
            = EntitySearcher.getAnnotations(pizza, getOpenCyc(), prettyString);
    
    System.out.println("Search time:" + (System.currentTimeMillis() - t) + "ms");
    anns.forEach(ann
            -> System.out.println(ann.getValue().asLiteral().get().getLiteral()
        ));

    subClses.forEach((Node<OWLClass> node) -> {
      Set<OWLClass> em = node.getEntities();
      em.forEach(clss -> {
        System.out.println("SubType:" + clss);
        Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(clss, getOpenCyc(), prettyString);
        annotations.forEach(ann -> {
          String lit = ann.getValue().asLiteral().get().getLiteral();
          System.out.println("\t:" + lit);
        });
      });
    });
  }
  
  private void storeConceptLabel(OWLAnnotation ann, OWLLogicalEntity owlObj, ConcurrentNavigableMap<String, Set<String>> labelMap) {
    String lit = ann.getValue().asLiteral().get().getLiteral();
    final Set<String> newLabels = new HashSet<>();
    if (labelMap.containsKey(lit)) {
      newLabels.addAll(labelMap.get(lit));
    }
    newLabels.add(owlObj.toStringID());
    labelMap.put(lit, newLabels);
    if (lit.startsWith("the ")) { //hack to artificially extend reach
      final Set<String> newLabelsThe = new HashSet<>();
      String key = lit.replace("the ", "");
      if (labelMap.containsKey(key)) {
        newLabelsThe.addAll(labelMap.get(key));
      }
      newLabelsThe.add(owlObj.toStringID());
      labelMap.put(key, newLabelsThe);
    }
    //  System.out.println((sp.knownTerm(lit) ? "+" : "-") + lit);
  }

  private void storeDownCaseLabel(String s, ConcurrentNavigableMap<String, Set<String>> labelMap) {
    final Set<String> newLabels = new HashSet<>();
    String l = s.toLowerCase(Locale.ENGLISH);

    if (ocycConceptForTermLower.containsKey(l)) {
      newLabels.addAll(ocycConceptForTermLower.get(l));
    }
    newLabels.addAll(labelMap.get(s));

    ocycConceptForTermLower.put(l, newLabels);
  }

}
