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

import java.util.List;

/**
 * An AttachmenHypothesis relates an OpenCyc concept to terms in a W2V Space.
 */
public class AttachmentHypothesis {

  int conceptID;
  String conceptURI;
  String renderedTerms;
  Double score;
  List<String> targetTerms;
  String textLabels;

  /**
   * AttachmentHypothesis constructor
   *
   * @param id
   * @param targetTerms
   * @param conceptURI
   * @param score
   * @param textLabels
   */
  public AttachmentHypothesis(int id, List<String> targetTerms, String conceptURI, Double score, String textLabels) {
    this.conceptURI = conceptURI;
    this.score = score;
    this.textLabels = textLabels;
    this.targetTerms = targetTerms;
    this.conceptID = id;
    this.renderedTerms = String.join("/", this.targetTerms);
  }

  /**
   *
   * @return the headings for the CSV file
   */
  public static String headCSV() {
    return "ConceptID,Name,URI,Score,Strings";
  }

  /**
   *
   * @return the headings for the HTML table
   */
  public static String headHTMLTable() {
    return "<tr><th>ConceptID</th><th>Name</th><th>URI</th><th>Score</th><th>Strings</th></tr>";
  }

  /**
   *
   * @return a CSV representation of the AttachmentHypothesis
   */
  public String toCSV() {
    return conceptID + "," + renderedTerms.replaceAll(",", "<COMMA>") + "," + conceptURI + "," + score + ","
            + textLabels.replaceAll(",", "<COMMA>");
  }

  /**
   *
   * @return an HTML representation of the AttachmentHypothesis
   */
  public String toHTMLTableTR() {
    return "<tr><td>" + conceptID + "</td><td>" + renderedTerms + "</td><td><a href=\"" + conceptURI + "\">" + conceptURI + "</a></td><td>" + score + "</td><td>"
            + textLabels + "</td></tr>";
  }

  @Override
  public String toString() {
    return renderedTerms + "[" + conceptID + "]⟶" + conceptURI + " (" + score + ":" + textLabels + ")";
  }
}
