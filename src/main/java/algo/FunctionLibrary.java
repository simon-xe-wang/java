package algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 Question:
   Suppose you are building a library and have following definition of a function and two methods register and findMatches.
   Register method registers a function and its argumentTypes
   in the library and findMatches accepts an input argument list and
   tries to find all the functions that match this input argument list.
 Note:
   If a function is marked as isVariadic=true, then the last argument can occur one or more number of times.

   Input: String Int
   LIB:   String, String, var
   F

   INPUT: STR, STR
   LIB: STR, STR, STR, var
   F

   INPUT: STR, STR
   LIB: STR, var
   T

 */
class Function {
    String name;
    List<String> argumentTypes;
    boolean isVariadic;

    Function(String name, List<String> argumentTypes, boolean isVariadic) {
        this.name = name;
        this.argumentTypes = argumentTypes;
        this.isVariadic = isVariadic;
    }
}

public class FunctionLibrary {

    private TrieNode root = new TrieNode(); // empty arg

    public void register(Set<Function> functionSet)  {
        // implement this method.
        for (Function func: functionSet) {
            TrieNode curNode = root;
            // for each arg
            for (String arg: func.argumentTypes) {
                // add arg to the children of current node
                curNode = curNode.children.computeIfAbsent(arg, k -> new TrieNode());
            }

            // at the end, add func to either vFuncs or nvFuncs
            if (func.isVariadic) {
                curNode.vFuncs.add(func);
            } else {
                curNode.nvFuncs.add(func);
            }

        }
    }

    public List<Function> findMatches(List<String> argumentTypes) {
        // implement this method.
        List<Function> candidates = new ArrayList<>();
        String candiArg = null;
        TrieNode curNode = root;

        // for each arg
        int idx = 0;
        for (; idx < argumentTypes.size(); idx++) {
            String arg = argumentTypes.get(idx);

            if (!curNode.children.containsKey(arg)) {
                break;
            }

            curNode = curNode.children.get(arg);

            if (candiArg != null && !arg.equals(candiArg)) {
                // clear old candidates
                candidates.clear();
            }

            candidates.addAll(curNode.vFuncs);
            candiArg = arg;
        }

        // reach the end, add all nvFuncs to the candidates
        if (idx == argumentTypes.size()) {
            candidates.addAll(curNode.nvFuncs);
        } else {
            candidates.clear();
        }

        return candidates;
    }

    private class TrieNode {
        Set<Function> vFuncs = new HashSet<>();
        List<Function> nvFuncs = new ArrayList<>();
        Map<String, TrieNode> children = new HashMap<>();
    }
}