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
                new Function("a", List.of("I"), false),
                new Function("b", List.of("I"), true),
                new Function("c", List.of("I", "I"), true),
                new Function("d", List.of("I", "S"), false),
                new Function("e", List.of("I", "S", "I"), true)
        );

        functionLibrary.register(funcs);
    }

    @Test
    void findMatch1() {
        List<Function> actual = functionLibrary.findMatches(List.of("I"));

        assertEquals(
                List.of("a", "b"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch2() {
        List<Function> actual = functionLibrary.findMatches(List.of("I", "I"));

        assertEquals(
                List.of("b", "c"),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Test
    void findMatch3() {
        List<Function> actual = functionLibrary.findMatches(List.of("I", "I", "I", "I"));

        assertEquals(
                List.of(),
                actual.stream()
                        .map( f -> f.name)
                        .sorted()
                        .collect(Collectors.toList()));
    }
}