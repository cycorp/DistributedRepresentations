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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mapdb.DB;

/**
 * A space of words from Google Word2Vec
 *
 */
public abstract class Word2VecSpace {

  private int size;
  DB db;
  Map<String, float[]> vectors;
  long words;

  /**
   *
   * @param terms
   * @return a List of Strings containing nGrams for terms
   */
  public static List<String> nGramsFor(List<String> terms) {
    final List<String> grams = new ArrayList<String>();
    IntStream.rangeClosed(1, terms.size()).forEach(length -> {
      IntStream.rangeClosed(0, terms.size() - length).forEach(start -> {
        List<String> l = terms.subList(start, start + length);
        grams.add(String.join(" ", l));
      });

    });
    return grams;
  }

  private static String norm(String term) {
    return term.replaceAll("\\s+", "_");
  }

  private double cosineSimilarity(float[] v1, float[] v2) {
    return dotProduct(v1, v2) / (magnitude(v1) * magnitude(v2));
  }

  /**
   *
   * @param t1
   * @param t2
   * @return the cosine similarity
   */
  public double cosineSimilarity(String t1, String t2) {
    return cosineSimilarity(getVector(t1), getVector(t2));
  }

  private double dotProduct(float[] v1, float[] v2) {
    return IntStream.range(0, v1.length)
            .mapToDouble(i -> (double) v1[i] * (double) v2[i])
            .sum();
  }

  private double euclidianDistance(float[] v1, float[] v2) {
    double dist = Math.sqrt(IntStream.range(0, v1.length)
            .mapToDouble(i -> Math.pow((double) v1[i] - (double) v2[i], 2))
            .sum());
    return dist;
  }

  private double euclidianDistance(String t1, String t2) {
    return euclidianDistance(getVector(t1), getVector(t2));
  }

  private float[] getAverageVector(List<String> terms) {
    final float sum[] = new float[size];
    final double mult = 1.0 / terms.size();
    terms.forEach(s -> {
      float v[] = getVector(s);
      IntStream.range(0, size)
              .forEach(i -> {
                sum[i] += mult * v[i];
              });
    });
    return sum;
  }

  /**
   *
   * @return the db
   */
  public DB getDb() {
    return db;
  }

  /**
   * Set up the DB.
   *
   * @param db
   */
  public void setDb(DB db) {
    this.db = db;
  }

  /**
   *
   * @param terms
   * @return the sum of term vectors divided by vector length
   * @throws NoWordToVecVectorForTerm
   */
  public float[] getGoogleNormedVector(List<String> terms) throws NoWordToVecVectorForTerm {
    // Sum of term vectors divided by vector length
    // Note that this will miss multi-word exact matches, so prefer getMaximalNormedVector
    //except for exact code comparison tests
    final float sum[] = new float[size];
    if (terms.stream().allMatch(s -> !knownTerm(s))) {
      throw new NoWordToVecVectorForTerm("Can't find vector for:" + String.join(", ", terms));
    }
    terms.stream()
            .filter(s -> knownTerm(s))
            .forEach(s -> {
              float v[] = getVector(s);
              IntStream.range(0, size)
              .forEach(i -> {
                sum[i] += v[i];
              });
            });
    return normVector(sum);
  }

  /**
   *
   * @param interms
   * @return the maximal normed vector
   * @throws NoWordToVecVectorForTerm
   */
  public float[]
          getMaximalNormedVector(List<String> interms) throws NoWordToVecVectorForTerm {
    // Sum of term ngram vectors divided by vector length
    List<String> terms = nGramsFor(interms);
    final float sum[] = new float[size];
    if (terms.stream().allMatch(s -> !knownTerm(s))) {
      throw new NoWordToVecVectorForTerm("Can't find vector for:" + String.join(", ", terms));
    }
    terms.stream()
            .filter(s -> knownTerm(s))
            .forEach(s -> {
              float v[] = getVector(s);
              IntStream.range(0, size)
              .forEach(i -> {
                sum[i] += v[i];
              });
            });
    return normVector(sum);
  }

  /**
   *
   * @return size of vectors
   */
  public int getNVectors() {
    return vectors.size();
  }

  /**
   *
   * @return size of the Word2VecSpace
   */
  public int getSize() {
    return size;
  }

  /**
   *
   * @param size
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   *
   * @param term
   * @return the vector for term
   */
  public float[] getVector(String term) {
    return vectors.get(norm(term));
  }

  /**
   *
   * @return the vectors
   */
  public Map<String, float[]> getVectors() {
    return vectors;
  }

  /**
   *
   * @param vectors
   */
  public void setVectors(ConcurrentNavigableMap<String, float[]> vectors) {
    this.vectors = vectors;
  }

  /**
   *
   * @return the words
   */
  public long getWords() {
    return words;
  }

  /**
   *
   * @param words
   */
  public void setWords(long words) {
    this.words = words;
  }

  /**
   *
   * @param v1
   * @param v2
   * @return the similarity between v1 and v2
   */
  public double googleSimilarity(float[] v1, float[] v2) {
    return dotProduct(v1, v2);
  }

  private double googleSimilarity(String t1, String t2) {
    return googleSimilarity(getVector(t1), getVector(t2));
  }

  /**
   *
   * @param terms
   * @param term
   * @return the similarity
   * @throws NoWordToVecVectorForTerm
   */
  public double googleSimilarity(List<String> terms, String term) throws NoWordToVecVectorForTerm {
    return googleSimilarity(getGoogleNormedVector(terms), getVector(term));
  }

  /**
   *
   * @param term
   * @return true if term is in vectors
   */
  public boolean knownTerm(String term) {
    return vectors.containsKey(norm(term));
  }

  private double magnitude(float[] v) {
    return Math.sqrt(IntStream.range(0, v.length).mapToDouble(i -> v[i] * v[i]).sum());
  }

  private double magnitude(List<Float> v) {
    return Math.sqrt(v.stream().mapToDouble(i -> i * i).sum());
  }

  /**
   *
   * @param v
   * @return normalized vector for v
   */
  public float[] normVector(float[] v) {
    final float normed[] = new float[size];
    double len = magnitude(v);

    IntStream.range(0, size)
            .forEach(i -> {
              normed[i] = v[i] / (float) len;
            });
    return normed;
  }

  /**
   *
   * @param v
   * @return normalized vector for v
   */
  public float[] normVector(List<Float> v) {
    final float normed[] = new float[v.size()];
    double len = magnitude(v);

    IntStream.range(0, v.size())
            .forEach(i -> {
              normed[i] = v.get(i) / (float) len;
            });
    return normed;
  }

  /**
   *
   * @param s
   * @return List of Strings
   */
  public List<String> stringToList(String s) {
    return Arrays.asList(s.split("\\s+"));
  }

  /**
   *
   * @param includeIf the predicate that is applied to the strings (the keys or embedded strings)
   * of the word to vec space to determine whether they should be retained in the output vector list
   * @return filtered vectors Map
   */
  protected Map<String, float[]> filterVectors(Predicate<String> includeIf) {
    return vectors.entrySet().stream().filter(entry -> {
      return includeIf.test(entry.getKey());
    }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  /**
   * No Vector for Term
   * <p>
   * Exception to use check when a term looked up in the space has no known position
   */
  public static class NoWordToVecVectorForTerm extends Exception {

    /**
     *
     * @param message
     */
    public NoWordToVecVectorForTerm(String message) {
      super(message);
    }
  }
}
