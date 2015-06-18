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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.EndianUtils;
import org.mapdb.DBMaker;

/**
 * Word2Vec distributed representation space from Google Format file.
 *
 * <p>
 * This class represents any distributed represenation computed using word2vec and initially loaded
 * from a Google word2vec formatted file
 */
public abstract class Word2VecSpaceFromFile extends Word2VecSpace {

  final StringBuilder sb = new StringBuilder();

  /**
   * Constructor for Word2VecSpaceFromFile
   * 
   * @throws IOException
   */
  public Word2VecSpaceFromFile() throws IOException {
    db = DBMaker.newFileDB(new File(Config.getW2vDBFile()))
            .closeOnJvmShutdown()
            //      .encryptionEnable("password")
            .make();

  }

  /**
   * Create a W2V space in a DB.
   * 
   * @param w2vZipFile
   * @throws FileNotFoundException
   * @throws IOException
   */
  protected final void createW2VinDB(String w2vZipFile) throws FileNotFoundException, IOException {
    try (DataInputStream data_in
            = new DataInputStream(
                    new GZIPInputStream(new FileInputStream(
                                    new File(w2vZipFile))))) {
                              getWordsAndSize(data_in);
                              if (vectors.size() == words) {
                                System.out.println("Word2Vec is in DB");
                              } else {
                                System.out.println("DB Size:" + vectors.size());

                                System.out.println("Want to read Word Count: " + words);
                                System.out.println("Size:" + getSize());
                                for (int w = 0; w < words; w++) {
                                  float[] v = new float[getSize()];
                                  String key = getVocabString(data_in);
                                  System.out.println(w + ":\t" + key);

                                  IntStream.range(0, getSize()).forEach(i -> v[i]
                                          = getFloat(data_in));
                                  vectors.put(key, normVector(v));
                                  if (w % 100000 == 1) {
                                    db.commit();
                                  }
                                }
                                db.commit();
                                db.compact();
                              }
                            }
  }

  private float getFloat(DataInputStream s) {
    try {
      float v = EndianUtils.readSwappedFloat(s);
      //System.out.println(st+"["+i+"]: "+v);
      return v;
    } catch (IOException ex) {
      Logger.getLogger(Word2VecSpace.class.getName()).log(Level.SEVERE, null, ex);
      return 0.0f;
    }
  }

  private String getVocabString(DataInputStream s) throws IOException {
    sb.setLength(0);
    for (char ch = (char) s.read();
            (!Character.isWhitespace(ch) && ch >= 0 && ch <= 256);
            ch = (char) s.read()) {
      sb.append((char) ch);
    }
    return sb.toString();
  }

  private void getWordsAndSize(DataInputStream s) throws IOException {
    sb.setLength(0);
    for (char ch = (char) s.read(); ch != '\n'; ch = (char) s.read()) {
      sb.append(ch);
    }
    String[] parts = sb.toString().split("\\s+");
    words = Long.parseLong(parts[0]);
    setSize((int) Long.parseLong(parts[1]));
  }

}
