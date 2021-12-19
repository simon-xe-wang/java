package concurrency;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
public class TaskScheduler implements Runnable {

    PriorityQueue<ScheduledTask> taskQueue = new PriorityQueue<>(
            (t1, t2) -> Long.compare(t1.scheduledTime, t2.scheduledTime)
    );

    private int capacity = 3;

    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition taskScheduled = lock.newCondition();

    private boolean isRunning = true;

    ExecutorService workerService = Executors.newFixedThreadPool(3);

    ExecutorService scheduler;

    public TaskScheduler(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                lock.lockInterruptibly();
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
                // log
                isRunning = false;
            } finally {
                lock.unlock();
            }
        }
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
        scheduler = Executors.newSingleThreadExecutor();
        scheduler.submit(this);
    }

    public void shutdown() {
        // stop the scheduler
        scheduler.shutdown();

        // shutdown workers
        workerService.shutdown();

        // wait for the completion of currently running tasks
        try {
            scheduler.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // log
        }

        try {
            workerService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // log
        }
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
