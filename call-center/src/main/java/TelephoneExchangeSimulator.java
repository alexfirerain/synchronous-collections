public class TelephoneExchangeSimulator implements Runnable {
    public static final int MEAN_CALL_INTERVAL = 1300;
    public static final int Q_FACTOR_FOR_CALL_INTERVALS = 2;

    private final int CALLS_TO_IMITATE;
    private CallCenter target;
    private boolean isOn = true;

    public TelephoneExchangeSimulator(int CALLS_TO_IMITATE) {
        this.CALLS_TO_IMITATE = CALLS_TO_IMITATE;
    }

    public void routeCallSequenceTo(CallCenter target) {
        this.target = target;
    }

    @Override
    public void run() {
        if (target == null) {
            System.out.println("АТС: не определён получатель звонков!");
            return;
        }
        int calls = 0;
        while (calls < CALLS_TO_IMITATE) {
            Main.timePass(MEAN_CALL_INTERVAL, Q_FACTOR_FOR_CALL_INTERVALS);
            Call call = new Call();
            Main.consoleReport("АТС: имитирую " + call);
            target.callQueue.add(call);
            calls++;
        }
        Main.consoleReport("АТС имитировала " + calls + " звонков и выключается.");
        isOn = false;
    }

    public boolean isOn() {
        return isOn;
    }
}
