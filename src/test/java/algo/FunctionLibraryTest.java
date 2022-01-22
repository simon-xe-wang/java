package algo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FunctionLibraryTest {
    static FunctionLibrary functionLibrary = new FunctionLibrary();

    @BeforeAll
    static void setup() {
        Set<Function> funcs = Set.of(
                new Function("FuncA", List.of("String", "Integer", "Integer"), false),
                new Function("FuncB", List.of("String", "Integer"), true),
                new Function("FuncC", List.of("Integer"), true),
                new Function("FuncD", List.of("Integer", "Integer"), true),
                new Function("FuncE", List.of("Integer", "Integer", "Integer"), false),
                new Function("FuncF", List.of("String"), false),
                new Function("FuncG", List.of("Integer"), false)
        );

        functionLibrary.register(funcs);
    }

    @Test
    void findMatch1() {
        List<Function> actual = functionLibrary.findMatches(List.of("Integer", "Integer", "Integer", "Integer"));

        assertEquals(
                List.of("FuncC", "FuncD"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch2() {
        List<Function> actual = functionLibrary.findMatches(List.of("Integer", "Integer", "Integer"));

        assertEquals(
                List.of("FuncC", "FuncD", "FuncE"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch3() {
        List<Function> actual = functionLibrary.findMatches(List.of("String", "Integer", "Integer", "Integer"));

        assertEquals(
                List.of("FuncB"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch4() {
        List<Function> actual = functionLibrary.findMatches(List.of("String", "Integer", "Integer"));

        assertEquals(
                List.of("FuncA", "FuncB"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch5() {
        List<Function> actual = functionLibrary.findMatches(List.of("Integer"));

        assertEquals(
                List.of("FuncC", "FuncG"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch6() {
        List<Function> actual = functionLibrary.findMatches(List.of("String"));

        assertEquals(
                List.of("FuncF"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

}