package concurrency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskSchedulerTest {

    class Task implements Runnable {
        private String name;
        private List<String> results;

        public List<String> getResults() {
            return results;
        }

        public void setResults(List<String> results) {
            this.results = results;
        }


        Task(String name, List<String> results) {
            this.name = name;
            this.results = results;
        }

        @Override
        public void run() {
            results.add(this.name);
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", results=" + results +
                    '}';
        }
    }

    @Test
    public void test1() {
        TaskScheduler scheduler = new TaskScheduler(3);
        scheduler.start();

        List<String> expected = List.of("task1", "task2", "task3");
        List<String> actual = new ArrayList<>();

        scheduler.schedule(new Task("task1", actual), 100, 0);
        scheduler.schedule(new Task("task2", actual), 200, 0);
        scheduler.schedule(new Task("task3", actual), 300, 0);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assertions.assertIterableEquals(expected, actual);
    }

    @Test
    public void test2() {
        TaskScheduler scheduler = new TaskScheduler(2);
        scheduler.start();

        List<String> expected = List.of("task1", "task2");
        List<String> actual = new ArrayList<>();

        scheduler.schedule(new Task("task1", actual), 200, 0);
        scheduler.schedule(new Task("task2", actual), 300, 0);
        boolean scheduled = scheduler.schedule(new Task("task3", actual), 300, 100);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertIterableEquals(expected, actual);
        Assertions.assertFalse(scheduled);
    }
}