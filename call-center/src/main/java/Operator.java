public class Operator implements Runnable {
    public static final int MEAN_REST = 900;
    public static final double Q_FACTOR_FOR_REST = 1.5;
    public static final int MEAN_TALK = 3000;
    public static final double Q_FACTOR_FOR_TALK = 4;

    private final CallCenter office;
    private final String name;

    public Operator(CallCenter office, String name) {
        this.office = office;
        this.name = name;
    }

    @Override
    public void run() {
        office.operatorsOnline.incrementAndGet();
        Main.consoleReport(this + " вышел на работу");
        while (true) {
            rest();
            Call call = office.callQueue.poll();
            if (call != null) {
                processACall(call);
            } else if (office.lineOff()) {
                office.operatorsOnline.decrementAndGet();
                office.onLine.remove(this);
                break;
            }
        }
        Main.consoleReport(this + " закончил работу");
        if (office.operatorsOnline.get() == 0) {
            Main.consoleReport("Последний закрывает офис");
            office.threads.shutdownNow();
        }
    }

    private void rest() {
        Main.timePass(MEAN_REST, Q_FACTOR_FOR_REST);
    }

    private void processACall(Call request) {
        int leftInQueue = office.callQueue.size();
        Main.consoleReport(name + " принимает " + request + (
                leftInQueue > 0 ?
                " (ещё в очереди: " + leftInQueue + ")" :
                ""));
        Main.timePass(MEAN_TALK, Q_FACTOR_FOR_TALK);
        Main.consoleReport(name + " завершает " + request);
    }

    @Override
    public String toString() {
        return name;
    }
}
