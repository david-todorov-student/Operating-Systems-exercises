import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class MusicBand {


    static MusicBandState state = new MusicBandState();
    //TODO: Definicija na globalni promenlivi i semafori

    public static void init() {
        //TODO: da se implementira
    }

    public static class GuitarPlayer extends TemplateThread {

        public GuitarPlayer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: da se implementira
            state.play();
            state.evaluate();
        }

    }

    public static class Singer extends TemplateThread {

        public Singer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: da se implementira
            state.play();
            state.evaluate();
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            Scanner s = new Scanner(System.in);
            int numRuns = 1;
            int numIterations = 100;
            s.close();

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Singer singer = new Singer(numRuns);
                threads.add(singer);
                GuitarPlayer gp = new GuitarPlayer(numRuns);
                threads.add(gp);
                gp = new GuitarPlayer(numRuns);
                threads.add(gp);
                singer = new Singer(numRuns);
                threads.add(singer);
                gp = new GuitarPlayer(numRuns);
                threads.add(gp);
            }

            init();

            ProblemExecution.start(threads, state);
            //System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
class MusicBandState extends AbstractState {

    private static final String PLAYING_NOT_PARALLEL = "The playing is not in parallel!";
    private static final String GROUP_NOT_FORMED = "The previous group is not formed";
    private static final String MAXIMUM_3_GUITAR_PLAYERS = "Maximum 3 Guitar Players playing is allowed.";
    private static final String MAXIMUM_2_SINGER = "Maximum 2 Singers playing is allowed.";
    private static final int MAXIMUM_2_SINGER_POINTS = 5;
    private static final int MAXIMUM_3_GUITAR_PLAYERS_POINTS = 5;
    private static final int GROUP_NOT_FORMED_POINTS = 5;
    private static final int PLAYING_NOT_PARALLEL_POINTS = 5;

    int groupMembersCount = 0;
    private BoundCounterWithRaceConditionCheck guitarPlayer;
    private BoundCounterWithRaceConditionCheck singer;

    public MusicBandState() {
        guitarPlayer = new BoundCounterWithRaceConditionCheck(0, 3,
                MAXIMUM_3_GUITAR_PLAYERS_POINTS, MAXIMUM_3_GUITAR_PLAYERS, null, 0, null);
        singer = new BoundCounterWithRaceConditionCheck(0, 2,
                MAXIMUM_2_SINGER_POINTS, MAXIMUM_2_SINGER, null, 0, null);
    }

    public void play() {
        synchronized (this) {
            groupMembersCount++;
        }
        if (getThread() instanceof MusicBand.GuitarPlayer) {
            log(guitarPlayer.incrementWithMax(false), "Guitar Player is playing");
        } else if (getThread() instanceof MusicBand.Singer) {
            log(singer.incrementWithMax(false), "Singer is playing");
        }
        Switcher.forceSwitch(3);
        if(guitarPlayer.getValue()!=3 || singer.getValue()!=2 ) {
//	      log(new PointsException(5,"Ne se prisutni site"),"");
        }

    }

    public void evaluate() {
        synchronized (this) {
            if (groupMembersCount == 5) {
                if (guitarPlayer.getValue() == 3&&singer.getValue() == 2) {
                    reset();
                    log(null, "The group has performed.");
                } else {
                    log(new PointsException(
                            GROUP_NOT_FORMED_POINTS,
                            GROUP_NOT_FORMED), null);

                }
            }
        }
    }

    private synchronized void reset() {
        guitarPlayer.setValue(0);
        singer.setValue(0);
        groupMembersCount = 0;
    }

    @Override
    public synchronized void finalize() {
        if (guitarPlayer.getMax() == 1&&singer.getMax() == 1) {
            logException(new PointsException(PLAYING_NOT_PARALLEL_POINTS,
                    PLAYING_NOT_PARALLEL));
        }
    }

}

abstract class AbstractState {

    /**
     * Method called after threads ended their execution to validate the
     * correctness of the scenario
     */
    public abstract void finalize();

    /**
     * List of logged actions
     */
    private List<String> actions = new ArrayList<String>();

    /**
     *
     * @return if the current thread is instance of TemplateThread it is
     *         returned, and otherwise null is returned
     */
    protected TemplateThread getThread() {
        Thread current = Thread.currentThread();
        if (current instanceof TemplateThread) {
            TemplateThread t = (TemplateThread) current;
            return t;
        } else {
            return null;
        }
    }

    /**
     * Log this exception or action
     *
     * @param e
     *            occurred exception (null if no exception)
     * @param action
     *            Description of the occurring action
     */
    public synchronized void log(PointsException e, String action) {
        TemplateThread t = (TemplateThread) Thread.currentThread();
        if (e != null) {
            t.setException(e);
            actions.add(t.toString() + "\t(e): " + e.getMessage());
        } else if (action != null) {
            actions.add(t.toString() + "\t(a): " + action);
        }
    }

    /**
     * Logging exceptions
     *
     * @param e
     */
    protected synchronized void logException(PointsException e) {
        Thread t = Thread.currentThread();
        if (e != null) {
            if (t instanceof TemplateThread) {
                ((TemplateThread) t).setException(e);
            }
            TemplateThread.hasException=true;
            actions.add("\t(e): " + e.getMessage());
        }
    }

    /**
     * Printing of the actions and exceptions that has occurred
     */
    public synchronized void printLog() {
        System.out
                .println("Poradi konkurentnosta za pristap za pecatenje, mozno e nekoja od porakite da ne e na soodvetnoto mesto.");
        System.out.println("Log na izvrsuvanje na akciite:");
        System.out.println("=========================");
        System.out.println("tip\tid\titer\takcija/error");
        System.out.println("=========================");
        for (String l : actions) {
            System.out.println(l);
        }
    }

    /**
     * Prints the status of the execution, with the exceptions that has occur
     */
    public void printStatus() {
        finalize();
        if (!TemplateThread.hasException) {
            int poeni = 25;
            if (PointsException.getTotalPoints() == 0) {
                System.out
                        .println("Procesot e uspesno sinhroniziran. Osvoeni 25 poeni.");
            } else {
                poeni -= PointsException.getTotalPoints();
                PointsException.printErrors();
                System.out.println("Maksimalni osvoeni poeni: " + poeni);
            }

        } else {
            System.out
                    .println("Procesot ne e sinhroniziran spored uslovite na zadacata");
            printLog();
            System.out
                    .println("====================================================");
            PointsException.printErrors();
            int total = (25 - PointsException.getTotalPoints());
            if (total < 0) {
                total = 0;
            }
            System.out.println("Maksimum Poeni: " + total);
        }

    }
}

class BoundCounterWithRaceConditionCheck {

    private static final int RACE_CONDITION_POINTS = 25;
    private static final String RACE_CONDITION_MESSAGE = "Race condition occured";

    private int value;
    private Integer maxAllowed;
    private Integer minAllowed;
    private int maxErrorPoints;
    private int minErrorPoints;
    private String maxErrorMessage;
    private String minErrorMessage;

    private int max;

    /**
     *
     * @param value
     */
    public BoundCounterWithRaceConditionCheck(int value) {
        super();
        this.value = value;
        this.max = value;
    }

    /**
     *
     * @param value
     *            initial value
     * @param maxAllowed
     *            upper bound of the value
     * @param maxErrorPoints
     *            how many points are lost with the max value constraint
     *            violation
     * @param maxErrorMessage
     *            message shown when the upper bound constrain is violated
     * @param minAllowed
     *            lower bound of the value
     * @param minErrorPoints
     *            how many points are lost with the min value constraint
     *            violation
     * @param minErrorMessage
     *            message shown when the lower bound constrain is violated
     */
    public BoundCounterWithRaceConditionCheck(int value, Integer maxAllowed, int maxErrorPoints,
                                              String maxErrorMessage, Integer minAllowed, int minErrorPoints,
                                              String minErrorMessage) {
        super();
        this.value = value;
        this.max = value;
        this.maxAllowed = maxAllowed;
        this.minAllowed = minAllowed;
        this.maxErrorPoints = maxErrorPoints;
        this.minErrorPoints = minErrorPoints;
        this.maxErrorMessage = maxErrorMessage;
        this.minErrorMessage = minErrorMessage;
    }

    /**
     *
     * @return the maximum value of the integer variable that occurred at some
     *         point of the execution
     */
    public int getMax() {
        return max;
    }

    /**
     *
     * @return the current value
     */
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Testing for race condition. NOTE: there are no guarantees that the race
     * condition will be detected
     *
     * @return
     */
    public PointsException checkRaceCondition() {
        return checkRaceCondition(3, RACE_CONDITION_MESSAGE);
    }

    /**
     * Testing for race condition. NOTE: there are no guarantees that the race
     * condition will be detected, but higher the time argument is, the
     * probability for race condition occurrence is higher
     *
     * @return
     */
    public PointsException checkRaceCondition(int time, String message) {
        int val;

        synchronized (this) {
            val = value;
        }
        Switcher.forceSwitch(time);
        if (val != value) {
            PointsException e = new PointsException(RACE_CONDITION_POINTS,
                    message);
            return e;
        }
        return null;

    }

    public PointsException incrementWithMax() {
        return incrementWithMax(true);
    }

    public PointsException incrementWithMax(boolean checkRaceCondition) {
        if (checkRaceCondition) {
            PointsException raceCondition = checkRaceCondition();
            if (raceCondition != null) {
                return raceCondition;
            }
        }
        synchronized (this) {
            value++;

            if (value > max) {
                max = value;
            }
            if (maxAllowed != null) {
                if (value > maxAllowed) {
                    PointsException e = new PointsException(maxErrorPoints,
                            maxErrorMessage);
                    return e;
                }
            }
        }

        return null;
    }

    public PointsException decrementWithMin() {
        return decrementWithMin(true);
    }

    public PointsException decrementWithMin(boolean checkRaceCondition) {
        if (checkRaceCondition) {
            PointsException raceCondition = checkRaceCondition();
            if (raceCondition != null) {
                return raceCondition;
            }
        }

        synchronized (this) {
            value--;
            if (minAllowed != null) {
                if (value < minAllowed) {
                    PointsException e = new PointsException(minErrorPoints,
                            minErrorMessage);
                    return e;
                }
            }
        }
        return null;
    }
}

class PointsException extends RuntimeException {

    private static HashMap<String, PointsException> exceptions = new HashMap<String, PointsException>();
    private int points;

    public PointsException(int points, String message) {
        super(message);
        this.points = points;
        exceptions.put(message, this);
    }

    public static int getTotalPoints() {
        int sum = 0;
        for (PointsException e : exceptions.values()) {
            sum += e.getPoints();
        }
        return sum;
    }

    public static void printErrors() {
        System.out.println("Gi imate slednite greski: ");
        for (Map.Entry<String, PointsException> e : exceptions.entrySet()) {
            System.out.println(String.format("[%s] : (-%d)", e.getKey(), e
                    .getValue().getPoints()));
        }
    }

    public int getPoints() {
        return points;
    }
}
abstract class ProblemExecution {

    public static void start(HashSet<Thread> threads, AbstractState state)
            throws Exception {


        // start the threads
        for (Thread t : threads) {
            t.start();
        }

        // wait threads to finish
        for (Thread t : threads) {
            t.join(1000);
        }

        // check for deadlock
        for (Thread t : threads) {
            if (t.isAlive()) {
                t.interrupt();
                if (t instanceof TemplateThread) {
                    TemplateThread tt = (TemplateThread) t;
                    tt.setException(new PointsException(25, "DEADLOCK"));
                }
            }
        }

        // print the status
        state.printStatus();
    }

}
class Switcher {
    private static final Random RANDOM = new Random();

    /*
     * This method pauses the current thread i.e. changes its state to be
     * Blocked. This should force thread switch if there are threads waiting
     */
    public static void forceSwitch(int range) {
        try {
            Thread.sleep(RANDOM.nextInt(range));
        } catch (InterruptedException e) {
        }
    }
}
abstract class TemplateThread extends Thread {

    static boolean hasException = false;
    int numRuns = 1;
    public int iteration = 0;
    protected Exception exception = null;

    public TemplateThread(int numRuns) {
        this.numRuns = numRuns;
    }

    public abstract void execute() throws InterruptedException;

    @Override
    public void run() {
        try {
            for (int i = 0; i < numRuns&&!hasException; i++) {
                execute();
                iteration++;

            }
        } catch (InterruptedException e) {
            // Do nothing
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
            hasException = true;
        }
    }

    public void setException(Exception exception) {
        this.exception = exception;
        hasException = true;
    }

    @Override
    public String toString() {
        Thread current = Thread.currentThread();
        if (numRuns > 1) {
            return String.format("%s\t%d\t%d", ""
                            + current.getClass().getSimpleName().charAt(0), getId(),
                    iteration);
        } else {
            return String.format("%s\t%d\t", ""
                    + current.getClass().getSimpleName().charAt(0), getId());
        }
    }
}
