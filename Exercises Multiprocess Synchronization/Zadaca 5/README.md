Event Processing Simulation Problem 3 (0 / 0)

You need to simulate event processing, using the predefined classes EventGenerator and Processor, which should function as threads.

The main method Processor.main starts a single Processor thread, then starts 100 EventGenerator threads and registers them using the Processor.register(EventGenerator p) method. Then the main method should wait for the Processor thread to finish, for a maximum of 20.000 ms. If it does not finish in this timeframe, main should stop it and write out the message Terminated scheduling; otherwise, it should write out Finished scheduling.

Each of the EventGenerator thread, after being started in the background, needs to sleep for a random interval using Thread.sleep(this.duration), after which it should generate an event using generate() which cannot be executed in parallel in more than 5 generators. Then, it should notify the Processor thread that there is a new event to be processed.

The Processor class is a Thread, which runs a forever-cycle in the background, in which it waits for a new event and then processes it using the process() method. The process() method **should not be called in parallel with the execution of the generate() method**.

Your task is to complete the code provided below according to the requirements, and be careful not to create a Race Condition or a Deadlock.
