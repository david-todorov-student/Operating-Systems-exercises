import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Processor {

    public static Random random = new Random();
    static List<EventGenerator> scheduled = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        // TODO: create the Processor and start it in the background


        for (int i = 0; i < 100; i++) {
            EventGenerator eventGenerator = new EventGenerator();
            register(eventGenerator);
            // TODO: start the eventGenerator

        }


        // TODO: wait for 20.000 ms for the Processor to finish

        // TODO: write out the execution status
    }

    public static void register(EventGenerator generator) {
        scheduled.add(generator);
    }

    /**
     * Cannot be executed in parallel with the generate() method
     */
    public static void process() {
        System.out.println("processing event");
    }


    public void run() {

        while (!scheduled.isEmpty()) {
            // TODO: wait for a new event

            // TODO: invoke its process() method
            process();
        }

        System.out.println("Done scheduling!");
    }
}


class EventGenerator {

    public Integer duration;

    public EventGenerator() throws InterruptedException {
        this.duration = Processor.random.nextInt(1000);
    }


    /**
     * Cannot be invoked in parallel by more than 5 generators
     */
    public static void generate() {
        System.out.println("Generating event: ");
    }
}