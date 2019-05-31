/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.index;


import org.apache.lucene.analysis.MockAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.LuceneTestCase.SuppressCodecs;
import org.apache.lucene.util.TestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Simple test that adds numeric terms, where each term has the
 * totalTermFreq of its integer value, and checks that the totalTermFreq is correct.
 */
// TODO: somehow factor this with BagOfPostings? it's almost the same
@SuppressCodecs({"Direct", "Memory"}) // at night this makes like 200k/300k docs and will make Direct's heart beat!
public class TestBagOfPositions extends LuceneTestCase {
    public void test() throws Exception {
        List<String> postingsList = new ArrayList<>();
        int numTerms = atLeast(300);
        final int maxTermsPerDoc = TestUtil.nextInt(random(), 10, 20);
        boolean isSimpleText = "SimpleText".equals(TestUtil.getPostingsFormat("field"));
        
        IndexWriterConfig iwc = newIndexWriterConfig(random(), new MockAnalyzer(random()));
        
        if ((isSimpleText || iwc.getMergePolicy() instanceof MockRandomMergePolicy) && (TEST_NIGHTLY || RANDOM_MULTIPLIER > 1)) {
            // Otherwise test can take way too long (> 2 hours)
            numTerms /= 2;
        }
        if (VERBOSE) {
            System.out.println("maxTermsPerDoc=" + maxTermsPerDoc);
            System.out.println("numTerms=" + numTerms);
        }
        for (int i = 0; i < numTerms; i++) {
            String term = Integer.toString(i);
            for (int j = 0; j < i; j++) {
                postingsList.add(term);
            }
        }
        Collections.shuffle(postingsList, random());
        
        final ConcurrentLinkedQueue<String> postings = new ConcurrentLinkedQueue<>(postingsList);
        
        Directory dir = newFSDirectory(createTempDir("bagofpositions"));
        
        final RandomIndexWriter iw = new RandomIndexWriter(random(), dir, iwc);
        
        int threadCount = TestUtil.nextInt(random(), 1, 5);
        if (VERBOSE) {
            System.out.println("config: " + iw.w.getConfig());
            System.out.println("threadCount=" + threadCount);
        }
        
        Field prototype = newTextField("field", "", Field.Store.NO);
        FieldType fieldType = new FieldType(prototype.fieldType());
        if (random().nextBoolean()) {
            fieldType.setOmitNorms(true);
        }
        int options = random().nextInt(3);
        if (options == 0) {
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS); // we dont actually need positions
            fieldType.setStoreTermVectors(true); // but enforce term vectors when we do this so we check SOMETHING
        } else if (options == 1) {
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        }
        // else just positions
        
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch startingGun = new CountDownLatch(1);
        
        for (int threadID = 0; threadID < threadCount; threadID++) {
            final Random threadRandom = new Random(random().nextLong());
            final Document document = new Document();
            final Field field = new Field("field", "", fieldType);
            document.add(field);
            threads[threadID] = new Thread() {
                @Override
                public void run() {
                    try {
                        startingGun.await();
                        while (!postings.isEmpty()) {
                            StringBuilder text = new StringBuilder();
                            int numTerms = threadRandom.nextInt(maxTermsPerDoc);
                            for (int i = 0; i < numTerms; i++) {
                                String token = postings.poll();
                                if (token == null) {
                                    break;
                                }
                                text.append(' ');
                                text.append(token);
                            }
                            field.setStringValue(text.toString());
                            iw.addDocument(document);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            threads[threadID].start();
        }
        startingGun.countDown();
        for (Thread t : threads) {
            t.join();
        }
        
        iw.forceMerge(1);
        DirectoryReader ir = iw.getReader();
        assertEquals(1, ir.leaves().size());
        LeafReader air = ir.leaves().get(0).reader();
        Terms terms = air.terms("field");
        // numTerms-1 because there cannot be a term 0 with 0 postings:
        assertEquals(numTerms - 1, terms.size());
        TermsEnum termsEnum = terms.iterator();
        BytesRef term;
        while ((term = termsEnum.next()) != null) {
            int value = Integer.parseInt(term.utf8ToString());
            assertEquals(value, termsEnum.totalTermFreq());
            // don't really need to check more than this, as CheckIndex
            // will verify that totalTermFreq == total number of positions seen
            // from a postingsEnum.
        }
        ir.close();
        iw.close();
        dir.close();
    }
}
