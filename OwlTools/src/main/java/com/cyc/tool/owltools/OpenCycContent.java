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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * <P>
 * OpenCycContent is designed to hold information about a given OpenCyc concept that can be found in
 * the OWL export of OpenCyc.
 *
 * <BR>This software is the proprietary information of Cycorp, Inc.
 * <P>
 * Use is subject to license terms.
 *
 * Created on : Feb 25, 2015, 2:47:47 PM
 */
public class OpenCycContent {

  Set<String> commentsForConcept;
  String conceptURI;
  String labelForConcept;
  Set<String> prettyStringsForConcept;
  Set<String> subTypesForConcept;

  Set<String> typesForConcept;

  //// Constructors
  /**
   * Creates a new instance of OpenCycContent.
   *
   * @param hlid
   * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
   */
  public OpenCycContent(String hlid) throws OWLOntologyCreationException {
    conceptURI = hlid;
    prettyStringsForConcept = null;
    commentsForConcept = null;
    labelForConcept = null;
    typesForConcept = null;
  }

  /**
   *
   * @return HTML String with information about the concept
   * @throws OWLOntologyCreationException
   */
  public String generateHtmlForConcept() throws OWLOntologyCreationException {
    String html = "";
    String constantName = getLabelForConcept();
    Set<String> commentStr = getCommentsForConcept();
    Set<String> prettyStr = getPrettyStringsForConcept();
    html += "<h1>" + constantName + "</h1>\n\n"
            + selectPicForConcept(getTypesForConcept())
            + "<p>" + commentStr.toArray(new String[0])[0] + "</p>\n"
            + "<p>English Phrases: </p>\n"
            + "<ul>\n";
    for (String s : prettyStr) {
      html += "   <li>" + s + "</li>\n";
    }
    html += "</ul>\n";

    return html;
  }

  /**
   *
   * @return Set of String comments
   * @throws OWLOntologyCreationException
   */
  public Set<String> getCommentsForConcept() throws OWLOntologyCreationException {
    if (commentsForConcept == null) {
      commentsForConcept = getCommentsForConceptFromOWL();
    }
    return commentsForConcept;
  }

  /**
   *
   * @return The CycL constant name
   * @throws OWLOntologyCreationException
   */
  public String getLabelForConcept() throws OWLOntologyCreationException {
    if (labelForConcept == null) {
      labelForConcept = getLabelForConceptFromOWL();
    }
    return labelForConcept;
  }

  /**
   *
   * @return Set of Strings with NL for the concept
   * @throws OWLOntologyCreationException
   */
  public Set<String> getPrettyStringsForConcept() throws OWLOntologyCreationException {
    if (prettyStringsForConcept == null) {
      prettyStringsForConcept = getPrettyStringsForConceptFromOWL();
    }
    return prettyStringsForConcept;
  }

  /**
   *
   * @return Set of Strings with names for generalizations of the concept
   * @throws OWLOntologyCreationException
   */
  public Set<String> getSubTypesForConcept() throws OWLOntologyCreationException {
    if (subTypesForConcept == null) {
      subTypesForConcept = getSubTypesForConceptFromOWL();
    }
    return subTypesForConcept;
  }

  /**
   *
   * @return Set of Strings with names for specializations of the concept
   * @throws OWLOntologyCreationException
   */
  public Set<String> getTypesForConcept() throws OWLOntologyCreationException {
    if (typesForConcept == null) {
      typesForConcept = getTypesForConceptFromOWL();
    }
    return typesForConcept;
  }

  private Set<String> getCommentsForConceptFromOWL() throws OWLOntologyCreationException {
    OpenCycReasoner reasoner = OpenCycReasoner.get();
    Set<String> comments = new HashSet<>();
    OWLClass concept = reasoner.getDataFactory().getOWLClass(IRI.create("http://sw.opencyc.org/concept/" + conceptURI));
    Collection<OWLAnnotation> anns = EntitySearcher.getAnnotations(concept, reasoner.getOpenCyc(), reasoner.getComment());
    anns.forEach(ann -> {
      comments.add(ann.getValue().asLiteral().get().getLiteral());
    });

    return comments;
  }

  private String getLabelForConceptFromOWL() throws OWLOntologyCreationException {
    OpenCycReasoner reasoner = OpenCycReasoner.get();
    String label = "";
    List<String> labels = new ArrayList<>();
    OWLClass concept = reasoner.getDataFactory().getOWLClass(IRI.create("http://sw.opencyc.org/concept/" + conceptURI));
    Collection<OWLAnnotation> anns = EntitySearcher.getAnnotations(concept, reasoner.getOpenCyc(), reasoner.getLabel());
    anns.forEach(ann -> {
      labels.add(ann.getValue().asLiteral().get().getLiteral());
    });
    if (conceptURI.contains("Mx")) {
      try {
        label = labels.get(0);
      } catch (Exception e) {
        System.out.println("Something went wrong getting the label from OWL");
        label = "FakeName";
      }
    }
    return label;
  }

  private Set<String> getPrettyStringsForConceptFromOWL() throws OWLOntologyCreationException {
    OpenCycReasoner reasoner = OpenCycReasoner.get();
    Set<String> prettyStrings = new HashSet<>();
    OWLClass concept = reasoner.getDataFactory().getOWLClass(IRI.create("http://sw.opencyc.org/concept/" + conceptURI));
    Collection<OWLAnnotation> anns = EntitySearcher.getAnnotations(concept, reasoner.getOpenCyc(), reasoner.getPrettyString());
    anns.forEach(ann -> {
      prettyStrings.add(ann.getValue().asLiteral().get().getLiteral());
    });

    return prettyStrings;
  }

  private Set<String> getSubTypesForConceptFromOWL() throws OWLOntologyCreationException {
    OpenCycReasoner reasoner = OpenCycReasoner.get();
    Set<String> types = new HashSet<>();
    OWLClass concept = reasoner.getDataFactory().getOWLClass(IRI.create("http://sw.opencyc.org/concept/" + conceptURI));
    NodeSet<OWLClass> subClasses = reasoner.getReasoner().getSubClasses(concept, true);
    subClasses.forEach(node -> {
      Set<OWLClass> ents = node.getEntities();
      ents.forEach(ent -> {
        types.add(ent.getIRI().getShortForm());
      });
    });
    return types;
  }

  private Set<String> getTypesForConceptFromOWL() throws OWLOntologyCreationException {
    OpenCycReasoner reasoner = OpenCycReasoner.get();
    Set<String> types = new HashSet<>();
    OWLClass concept = reasoner.getDataFactory().getOWLClass(IRI.create("http://sw.opencyc.org/concept/" + conceptURI));
    NodeSet<OWLClass> subClasses = reasoner.getReasoner().getSuperClasses(concept, true);
    subClasses.forEach(node -> {
      Set<OWLClass> ents = node.getEntities();
      ents.forEach(ent -> {
        types.add(ent.getIRI().getShortForm());
      });
    });
    return types;
  }

  //// Protected Area
  private String selectPicForConcept(Set<String> types) {
    String picHTML = "<img src=\"http://lorempixel.com/100/75/cats\">";
    for (String type : types) {
      if (type.equalsIgnoreCase("Mx4rvViADZwpEbGdrcN5Y29ycA")) {
        // Event
        picHTML = "<img src=\"http://lorempixel.com/100/75/sports\">";
        return picHTML;
      } else if (type.equalsIgnoreCase("Mx4rIcwFloGUQdeMlsOWYLFB2w")) {
        // Human
        picHTML = "<img src=\"http://lorempixel.com/100/75/people\">";
        return picHTML;
      } else if (type.equalsIgnoreCase("Mx4rv-6HepwpEbGdrcN5Y29ycA")) {
        // Transportation
        picHTML = "<img src=\"http://lorempixel.com/100/75/transport\">";
      }
    }

    return picHTML;
  }
}
