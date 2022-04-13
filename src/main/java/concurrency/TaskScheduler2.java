// This is a sandbox environment that allows you to experiment with CoderPad's execution capabilities
// It's a temporary throw-away session only visible to you so you can test out the programming environment.
// Once you select a language, to execute your code simply hit the 'Run' button which will be located in the top left hand of your screen.
//
// To see more information about the language you have selected, hit the 'Info' button beside the language dropdown.
// You'll find what version of the language is running and the packages available for the given language.

/*
 * Click `Run` to execute the snippet below!
 */

import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

public class TaskScheduler2 {
    static  class Task implements Runnable {
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

    public static void main(String[] args) {
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

        try {
            Assert.assertEquals(expected, actual);
        } finally {
            scheduler.shutdown();
        }
    }
}


/*
 * Questions:
 * 1. task could be long-running?
 * 2. schedule multi thread safe?
 * 3. capacity limit? what if exceed the limit?
 * Implementation:
 * PriorityQueue: hold all submitted tasks sorted by submitting time. Oldest task on the top
 * Scheduler run: Scheduler polls the top. if it's time, call it directly or submit to a thread pool. if not, wait
 * schedule: add the task to the queue and notify the scheduler thread.
 */
class TaskScheduler implements Runnable {

    PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>(
            (t1, t2) -> Long.compare(t1.scheduledTime, t2.scheduledTime)
    );

    private int capacity = 3;

    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition taskScheduled = lock.newCondition();

    private boolean isRunning = true;

    ExecutorService workerService = Executors.newFixedThreadPool(3);

    Thread scheduler;

    public TaskScheduler(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void run() {
        while (isRunning) {
            System.out.println("2222222");

            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
            }

            try {
                System.out.println("task queue size: " + taskQueue.size());
                if (taskQueue.isEmpty()) {
                    taskScheduled.await();
                } else {
                    long delay = taskQueue.peek().getDelay();
                    if (delay <= 0) {
                        ScheduledTask scheduledTask = taskQueue.poll();
                        workerService.submit(scheduledTask.getTask());
                        notFull.signal();
                        System.out.println("run a task " + scheduledTask.task);
                    } else {
                        taskScheduled.await(delay, TimeUnit.MILLISECONDS);
                        System.out.println("waiting next time");
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("111111111 scheduler interrupted");
                isRunning = false;
            } finally {
                lock.unlock();
            }
        }

        System.out.println("===== shceduler is done");
    }

    public boolean schedule(Runnable task, long delayMs, long timeoutMs) {
        ScheduledTask scheduledTask = new ScheduledTask(task, delayMs);

        try {
            lock.lockInterruptibly();
            while (taskQueue.size() >= capacity) {
                if (!notFull.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                    System.out.println("timeout ...");
                    return false;
                }
            }
            taskQueue.offer(scheduledTask);
            System.out.println(Thread.currentThread() + ", " + lock.isHeldByCurrentThread());
            taskScheduled.signal();
        } catch (InterruptedException e) {
            // log
            return false;
        } finally {
            lock.unlock();
        }

        return true;
    }

    public void start() {
        scheduler = new Thread(this);
        scheduler.start();
    }

    public void shutdown() {
        // stop the scheduler
        scheduler.interrupt();

        // shutdown workers
        workerService.shutdown();
        System.out.println("scheduler shutting down");

        try {
            workerService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // log
        }
        System.out.println("work service shutting down " + workerService.isTerminated());


    }
}

class ScheduledTask {

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public long getDelay() {
        return scheduledTime - System.currentTimeMillis();
    }

    long scheduledTime;
    Runnable task;

    public ScheduledTask(Runnable task, long delayMs) {
        this.scheduledTime = System.currentTimeMillis() + delayMs;
        this.task = task;
    }
}
