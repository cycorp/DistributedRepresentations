package com.cyc.tool.conceptfinder;

//// Internal Imports
//// External Imports
import com.cyc.tool.distributedrepresentations.GoogleNewsW2VSpace;
import com.cyc.tool.distributedrepresentations.Word2VecSpace;
import com.cyc.tool.owltools.OpenCycOwl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * <P>
 * Passage is designed to represent a longer passage of text that is broken up so that we can
 * permform concept finder activities on it.
 *
 */
public class Passage {

  String longText;
  static ConceptSpace cSpace;
  
  static Word2VecSpace mySpace;
  static OpenCycOwl ocyc;
  static MissingConceptFinder mcf;
  
  List<List<ConceptMatch>> matchesForPassage = new ArrayList<>(); 
  Set<Set<AttachmentHypothesis>> hypothesesForPassage = new HashSet<>();

  //// Constructors
  /**
   * Creates a new instance of Passage.
   */
  public Passage(String text) throws IOException, OWLOntologyCreationException {
    longText = text;
    mySpace = GoogleNewsW2VSpace.get();
    cSpace = new ConceptSpace(mySpace);
    ocyc = new OpenCycOwl();
    mcf = new MissingConceptFinderDefault(mySpace, ocyc, cSpace);
  }

  //// Public Area
  public List<ConceptMatch> narrowConceptsForPassage(List<ConceptMatch> allMatches) {
    List<ConceptMatch> narrowedMatches = new ArrayList<>();
    List<String> allMatchesTermStrings = new ArrayList<>();
    allMatches.forEach((ConceptMatch c) -> {
      allMatchesTermStrings.add(c.getTerm());
    });
    // Narrow concepts based on score
    allMatches.forEach((ConceptMatch c) -> {
      if (c.getSimilarity() > .4 && Collections.frequency(allMatchesTermStrings, c.getTerm()) > 1) {
        narrowedMatches.add(c);
      }
    });

    return narrowedMatches;
  }

  public List<ConceptMatch> findConceptsForPassage() {
    List<ConceptMatch> allMatches = new ArrayList<>();
    List<String> splitText = Arrays.asList(longText.replaceAll("[^a-zA-Z ]", "").split("\\s"));
    splitText.remove("*");
    /*
     Try to find an exact match for the first 3 token long chunk.
     - If we find one, great.  Get nearest terms and move on to the next 3 token chunck, not including
     any of the first 3 tokens.
     - If we don't find one, back off to the first two tokens and try to find an exact match.
     - And so on...
     */
    int i = 0;
    try {
      while (splitText.get(i) != null) {
        String chunk;
//      if (splitText.subList(i, i+3).size() == 3) {
        try {
          chunk = splitText.get(i) + " " + splitText.get(i + 1) + " " + splitText.get(i + 2);
          try {
            processChunk(chunk);
            i += 3;
//          continue;
          } catch (Exception ex) {
            try {
              chunk = splitText.get(i) + " " + splitText.get(i + 1);
              processChunk(chunk);
              i += 2;
//            return;
            } catch (Exception ex1) {
              try {
                chunk = splitText.get(i);
                processChunk(chunk);
                i++;
              } catch (Exception ex2) {
                System.out.println("No exact match found for smallest possible chunk.");
                i++;
//              return;
              }
            }
          }
        } catch (IndexOutOfBoundsException indexEx) {
          try {
            chunk = splitText.get(i) + " " + splitText.get(i + 1);
            try {
              processChunk(chunk);
              i += 2;
//            return;
            } catch (Exception ex1) {
              try {
                chunk = splitText.get(i);
                processChunk(chunk);
                i++;
//              return;
              } catch (Exception ex2) {
                System.out.println("No exact match found for smallest possible chunk.");
                i++;
//              return;
              }
            }
          } catch (IndexOutOfBoundsException indexEx1) {
            try {
              chunk = splitText.get(i);
              processChunk(chunk);
              i++;
//            return;
            } catch (Exception ex2) {
              System.out.println("No exact match found for smallest possible chunk.");
              i++;
//            return;
            }
          }
        }
      }
    } catch (IndexOutOfBoundsException indexEx2) {
      System.out.println("Processing complete");
    }
    System.out.println("Attachment Hypotheses for this passage: ");
    hypothesesForPassage.forEach((Set<AttachmentHypothesis> hyps) -> {
      hyps.forEach((AttachmentHypothesis h) -> {
        System.out.println(h + "\n");
      });
    });
    for (List<ConceptMatch> matchList : matchesForPassage) {
      allMatches.addAll(matchList);
    }
    return allMatches;
  }

  private void processChunk(String chunk) throws Exception {
    List<ConceptMatch> matches = runChunk(chunk);
    matchesForPassage.add(matches);
    Set<AttachmentHypothesis> hyp = mcf.findAttachmentHypothesesForConceptMatches(matches);
    hypothesesForPassage.add(hyp);
    for (AttachmentHypothesis h : hyp) {
      System.out.println("Hypothesis \"" + chunk + "\" " + h.score + " " + h.conceptURI + " \"" + h.targetTerms + "\"");
    }
  }

  private List<ConceptMatch> runChunk(String chunk) throws Exception {
    List<ConceptMatch> matches = new ArrayList<>();

    if (mySpace.knownTerm(chunk)) {
      matches.addAll(cSpace.findNearestNForIn(chunk, 40, ocyc));
      return matches;
    } else {
      throw new Exception("Exact match not found, examine a smaller chunk.");
    }
  }
}
