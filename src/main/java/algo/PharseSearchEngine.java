package algo;

/*
 * Click `Run` to execute the snippet below!
 */

import java.io.*;
import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

public class PhraseSearchEngine {
    /** Preprocess doc collection. Assuming input doc collection is represented as
     * List<List<String>>
     * For example:
     * [
     *   ["Cloud", "Computing", "is", "booming", "in", "the", "market"],
     *   ["I" "am", "going", "to", ...],
     *   ["Scientist" "has", "investigated", "computing", "Cloud", "Venus", ...],
     *   ...
     cloud: doc_0: { word_0, }, doc_2: {word_4}
     computing: doc_0: { word_1, }, doc_2: {word_3}

     *


     cloud: 0,
     computing: 1
     is: 2


     Map {word: docid. ... }
     */

    Map<String, Map<Long, Set<Long> > > indexMapping = new HashMap<>();

    void preprocessing(List<List<String>> docCollection) {
        for (int id = 0; id < docCollection.size(); id++) {
            List<String> doc = docCollection.get(id);
            writeDoc(id, doc);
        }

    }

    void writeDoc(Long id, List<String> doc) {
        for (String word: doc) {
            String lower = word.toLower();
            Set<Long> docIds = indexMapping.computeIfAbsent(lower, k -> new HashSet<Long>());
            docIds.add(id);
        }
    }

    /** Word Search API. Assuming DocID is a Long variable */
    List<Long> search(String word) {
        return indexMapping.get(word.toLower());
    }

    /** Word Search API. Assuming DocID is a Long variable */
    List<Long> searchPhrase(String phrase) {
        return indexMapping.get(word.toLower());
    }
}

class Solution {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("Hello, World!");
        strings.add("Welcome to CoderPad.");
        strings.add("This pad is running Java " + Runtime.version().feature());

        for (String string : strings) {
            System.out.println(string);
        }
    }
}
