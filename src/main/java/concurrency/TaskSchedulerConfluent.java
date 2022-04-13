package concurrency;

// This is a sandbox environment that allows you to experiment with CoderPad's execution capabilities
// It's a temporary throw-away session only visible to you so you can test out the programming environment.
// Once you select a language, to execute your code simply hit the 'Run' button which will be located in the top left hand of your screen.
//
// To see more information about the language you have selected, hit the 'Info' button beside the language dropdown.
// You'll find what version of the language is running and the packages available for the given language.

/*
 * Click `Run` to execute the snippet below!
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

class Task {
    int id;
    public Task(int id) {
        this.id = id;
    }

    public void run() {
        System.out.println("running " + id);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }
}

class DelayedTask implements Runnable {
    Task task;
    long created;
    long delay;

    public DelayedTask(Task task, long created, long delay) {
        this.task = task;
        this.delay = delay;
        this.created = created;
    }

    public long getExecutionTime() {
        return created + delay;
    }

    public void run() {
        task.run();
    }
}

class Scheduler implements Runnable {

    PriorityQueue<DelayedTask> taskQueue = new PriorityQueue<>( (t1, t2) -> Long.compare(t1.getExecutionTime(), t2.getExecutionTime()) );

    ExecutorService workerPool;
    Thread scheduler;

    long capacity = 2;


    public void schedule(Task task, long delay) {
        synchronized (this) {

            try {
                while (taskQueue.size() >= capacity) {
                    System.out.println("full ...");
                    wait();
                }
            } catch (Exception e) {

            }

            taskQueue.offer(new DelayedTask(task, System.currentTimeMillis(), delay));
            notifyAll();
        }
    }

    public void start() {
        workerPool = Executors.newFixedThreadPool(3);
        scheduler = new Thread(this);
        scheduler.start();
    }

    public void shutdown() {

        workerPool.shutdown();
        try {
            System.out.println("before");
            workerPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }

        System.out.println("after");

        scheduler.interrupt();
    }

    public void run() {
        boolean running = true;

        while (running) {
            synchronized(this) {
                try {
                    while (taskQueue.isEmpty()) {
                        wait();
                    }
                    long delay = taskQueue.peek().getExecutionTime() - System.currentTimeMillis();

                    System.out.println("top = " + delay);
                    if (delay <= 0 ) {
                        workerPool.submit(taskQueue.poll());
                        notifyAll();
                    } else {
                        wait(delay);
                    }
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }

    }
}

class Solution {
    public static void main(String[] args) throws Exception {

        Scheduler s = new Scheduler();
        s.start();

        s.schedule(new Task(1), 2000L);
        s.schedule(new Task(2), 3000L);
        s.schedule(new Task(3), 1000L);


        System.out.println("waiting");

        Thread.sleep(1000);
        s.shutdown();

        System.out.println("done");
    }
}
