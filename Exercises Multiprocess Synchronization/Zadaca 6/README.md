First In First Out Scheduling Problem 4 (0 / 0)

You need to implement the "First In First Out" process scheduling algorithm, using the defined classes Scheduler and Process that should behave as threads.

The main method Scheduler.main should start 100 new Process threads, and to register them using Scheduler.register(Process p) method, and then to start the Scheduler thread. Then it should wait at most 20.000 ms for the Scheduler thread to finish. If the Scheduler thread does not finish in this time, it should be interrupted, and the message Terminated scheduling should be printed, while in the opposite case the message Finished scheduling should be printed.

Each of the Process threads should sleep a random time (using Thread.sleep(this.duration)) right after it stars its background execution. The method Process.execute should start the background execution of its Process thread.

The class Scheduler is a Thread, which executes a loop in background until there are elements in the scheduled list. In this loop, it first sleeps for 100 ms, and then it takes and executes the next process using next().execute(). The loop execution then should wait until the Process thread finishes its background execution.

Your task is to complete the given code according to the previous requirements, preventing the Race Condition and Deadlock from occurring.
