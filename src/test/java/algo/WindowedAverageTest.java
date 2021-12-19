package algo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowedAverageTest {

    @Test
    public void test1() throws Exception {
        WindowedAverage win = new WindowedAverage(3);
        win.put("key1", 1, 1);
        win.put("key2", 2, 2);
        win.put("key3", 3, 3);

        assertEquals(2.0, win.getAvg(3));

        assertEquals(2.5, win.getAvg(5));

        win.put("key4", 4, 5);
        assertEquals(3.0, win.getAvg(5));
    }
}