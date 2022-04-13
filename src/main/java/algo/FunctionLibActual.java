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

class Solution {


    /*
     * register[{
     *    funA:{["Boolean", "Integer"], isVariadic:false},
     *    funB:{["Integer"], isVariadic:false},
     *    funC:{["Integer"], isVariadic:true}

     * })
     *
     * findMatches(["Boolean", "Integer"])            -> [funA]
     * findMatches(["Integer"])                       -> [funB, funC]
     * findMatches(["Integer", "Integer", "Integer"]) -> [funC]
     */


    static class Function {
        public final List<String> argumentTypes; // e.g. ["Integer", "String", "PersonClass"]
        public final String name;
        public final boolean isVariadic;

        Function(String name, List<String> argumentTypes, boolean isVariadic) {
            this.name = name;
            this.argumentTypes = argumentTypes;
            this.isVariadic = isVariadic;
        }

        public String toString() {
            return this.name;
        }
    }

    static class TrieNode {
        Map<String, TrieNode> children = new HashMap<>();
        Set<Function> vFuncs = new HashSet<>();
        Set<Function> nvFuncs = new HashSet<>();
    }

    static class FunctionLibrary {

        TrieNode root = new TrieNode();


        void register(Set<Function> functions) {
            // implement me
            for (Function func: functions) {
                register(func);
            }
        }

        void register(Function func) {

            TrieNode node = root;

            for (String arg: func.argumentTypes) {
                node = node.children.computeIfAbsent(arg, key -> new TrieNode());
            }

            if (func.isVariadic) {
                node.vFuncs.add(func);
            } else {
                node.nvFuncs.add(func);
            }
        }

        List<Function> findMatches(List<String> argumentTypes) {
            // implement me
            TrieNode node = root;
            List<Function> candidates = new LinkedList<>();
            String lastArgType = "";

            int idx = 0;

            boolean finished = true;

            for ( ; idx < argumentTypes.size(); idx++) {

                String arg = argumentTypes.get(idx);

                if (!arg.equals(lastArgType)) {
                    candidates.clear();
                    lastArgType = arg;
                }

                if (node != null) node = node.children.get(arg);

                if (node == null) {
                    finished = false;
                } else {
                    candidates.addAll(node.vFuncs);
                }
            }

            if (finished) {
                candidates.addAll(node.nvFuncs);
            }


            return candidates;
        }
    }



    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("Hello, World!");
        strings.add("Welcome to CoderPad.");
        strings.add("This pad is running Java " + Runtime.version().feature());

        for (String string : strings) {
            System.out.println(string);
        }


        FunctionLibrary fl = new FunctionLibrary();

        Function A = new Function( "funA", Arrays.asList("String", "Integer"), false );
        Function B = new Function( "funB", Arrays.asList("Integer"), false );
        Function C = new Function( "funC", Arrays.asList("Integer"), true);

        Set<Function> functions = new HashSet<>();
        functions.add(A);
        functions.add(B);
        functions.add(C);

        fl.register(functions);

        System.out.println(fl.findMatches(Arrays.asList("String", "Integer")));
        System.out.println(fl.findMatches(Arrays.asList("Integer")));
        System.out.println(fl.findMatches(Arrays.asList("Integer", "Integer", "Integer")));
        System.out.println(fl.findMatches(Arrays.asList("Integer", "Integer", "Integer", "Float")));
    }
}

/*
SP_A
*/
