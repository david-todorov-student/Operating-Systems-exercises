import java.util.*;
import java.util.concurrent.Semaphore;

public class Poker {


    public static void init() {
    }

    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {

            state.playerSeat();

            state.dealCards();

            state.play();

            state.endRound();
        }

    }

    public static void main(String[] args) {

        run();

    }

    static PokerState state = new PokerState();

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 1200;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Player c = new Player(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class PokerState extends AbstractState {

    private static final int PREVIOUS_ROUND_NOT_FINISHED_POINTS = 7;
    private static final String PREVIOUS_ROUND_NOT_FINISHED = "Prethodnata grupa nema zavrseno so igrata.";
    private static final int NO_6_FINISHED_PLAYERS_POINTS = 7;
    private static final String NO_6_FINISHED_PLAYERS = "Nema 6 igraci koi zavrsile so igranje";

    private static final int PLAYERS_STIL_PLAYING_POINTS = 7;
    private static final String PLAYERS_STIL_PLAYING = "Ima uste igraci koi ne zavrsile so igranje";

    private static final int PLAYING_NOT_PARALLEL_POINTS = 7;
    private static final String PLAYING_NOT_PARALLEL = "ne e paralelno igranjeto";

    private static final int CARDS_NOT_DEALED_POINTS = 7;
    private static final String CARDS_NOT_DEALED = "Ne moze da igra koga kartite ne se podeleni";

    private static final int MAXIMUM_6_CUSTOMERS_POINTS = 7;
    private static final String MAXIMUM_6_CUSTOMERS = "Poveke od 6 igraci probuvaat da sednat istovremeno";

    private static final int NOT_ENOUGH_PLAYERS_POINTS = 7;
    private static final String NOT_ENOUGH_PLAYERS = "nema dovolno igraci za da se sostavi grupa";

    private BoundCounterWithRaceConditionCheck playersAtTable;
    private BoundCounterWithRaceConditionCheck peoplePlaying;
    private BoundCounterWithRaceConditionCheck peopleFinishedPlaying;
    private boolean emptyTable = true;
    private boolean cardsDealed = false;

    public PokerState() {
        playersAtTable = new BoundCounterWithRaceConditionCheck(0, 6,
                MAXIMUM_6_CUSTOMERS_POINTS, MAXIMUM_6_CUSTOMERS, null, 0, null);

        peoplePlaying = new BoundCounterWithRaceConditionCheck(0);
        peopleFinishedPlaying = new BoundCounterWithRaceConditionCheck(0);
    }

    /*
     * Igracot sednuva na masata
     */
    public void playerSeat() {
        synchronized (PokerState.class) {
            if (emptyTable) {
                emptyTable = false;
                playersAtTable.assertEquals(0,
                        PREVIOUS_ROUND_NOT_FINISHED_POINTS,
                        PREVIOUS_ROUND_NOT_FINISHED);
            }
        }
        log(playersAtTable.incrementWithMax(false), "Igrac sednuva");
        Switcher.forceSwitch(5);
    }

    /*
     * Dilerot deli karti
     */
    public void dealCards() {
        log(playersAtTable.assertEquals(6, NOT_ENOUGH_PLAYERS_POINTS,
                NOT_ENOUGH_PLAYERS), null);
        synchronized (PokerState.class) {
            cardsDealed = true;
        }
        Switcher.forceSwitch(5);
    }

    /*
     * Igracot zapocnuva so igrata
     */
    public void play() {
        synchronized (PokerState.class) {
            if (!cardsDealed) {
                log(new PointsException(CARDS_NOT_DEALED_POINTS,
                        CARDS_NOT_DEALED), null);
            }
        }
        log(peoplePlaying.incrementWithMax(false), "Igracot igra poker");
        Switcher.forceSwitch(10);
        log(peoplePlaying.decrementWithMin(false), null);
        peopleFinishedPlaying.incrementWithMax(false);
    }

    /*
     * Site igraci zavrsile so igranjeto, moze da vleze nova grupa
     */
    public void endRound() {
        peopleFinishedPlaying.assertEquals(6, NO_6_FINISHED_PLAYERS_POINTS,
                NO_6_FINISHED_PLAYERS);
        log(peoplePlaying.assertEquals(0, PLAYERS_STIL_PLAYING_POINTS,
                PLAYERS_STIL_PLAYING), "Site igraci zavrsija so igranje.");

        synchronized (PokerState.class) {
            // reset scenario
            emptyTable = true;
            cardsDealed = false;
            playersAtTable.setValue(0);
            peopleFinishedPlaying.setValue(0);
        }
        Switcher.forceSwitch(3);
    }

    @Override
    public void finalize() {
        if (peoplePlaying.getMax() == 1) {
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
            throw e;
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
            TemplateThread.hasException = true;
            actions.add("\t(e): " + e.getMessage());
            throw e;
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
        try {
            finalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (!exceptions.isEmpty()) {
            System.out.println("Gi imate slednite greski: ");
            for (Map.Entry<String, PointsException> e : exceptions.entrySet()) {
                System.out.println(String.format("[%s] : (-%d)", e.getKey(), e
                        .getValue().getPoints()));
            }
        }
    }

    public int getPoints() {
        return points;
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
    public int iteration = 0;
    protected Exception exception = null;
    int numRuns = 1;

    public TemplateThread(int numRuns) {
        this.numRuns = numRuns;
    }

    public abstract void execute() throws InterruptedException;

    @Override
    public void run() {
        try {
            for (int i = 0; i < numRuns && !hasException; i++) {
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
            return String.format("[%d]%s\t%d\t%d", new Date().getTime(), ""
                            + current.getClass().getSimpleName().charAt(0), getId(),
                    iteration);
        } else {
            return String.format("[%d]%s\t%d\t", new Date().getTime(), ""
                    + current.getClass().getSimpleName().charAt(0), getId());
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

    public static int raceConditionDefaultTime = 3;

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
    public BoundCounterWithRaceConditionCheck(int value, Integer maxAllowed,
                                              int maxErrorPoints, String maxErrorMessage, Integer minAllowed,
                                              int minErrorPoints, String minErrorMessage) {
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
    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    /**
     * Throws exception when the val is different than the value of the counter.
     *
     * @param val
     * @param points
     * @param errorMessage
     * @return
     */
    public synchronized PointsException assertEquals(int val, int points,
                                                     String errorMessage) {
        if (this.value != val) {
            PointsException e = new PointsException(points, errorMessage);
            return e;
        } else {
            return null;
        }
    }

    public synchronized PointsException assertNotEquals(int val, int points,
                                                        String errorMessage) {
        if (this.value == val) {
            PointsException e = new PointsException(points, errorMessage);
            return e;
        } else {
            return null;
        }
    }

    /**
     * Testing for race condition. NOTE: there are no guarantees that the race
     * condition will be detected
     *
     * @return
     */
    public PointsException checkRaceCondition() {
        return checkRaceCondition(raceConditionDefaultTime,
                RACE_CONDITION_MESSAGE);
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

abstract class ProblemExecution {

    public static void start(HashSet<Thread> threads, AbstractState state)
            throws Exception {

        startWithoutDeadlock(threads, state);

        checkDeadlock(threads, state);
    }

    public static void startWithoutDeadlock(HashSet<Thread> threads,
                                            AbstractState state) throws Exception {

        // start the threads
        for (Thread t : threads) {
            t.start();
        }

        // wait threads to finish
        for (Thread t : threads) {
            t.join(1000);
        }

    }

    private static void checkDeadlock(HashSet<Thread> threads,
                                      AbstractState state) {
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