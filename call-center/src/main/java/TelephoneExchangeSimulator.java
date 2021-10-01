/**
 * Имитатор АТС, генерирующий заданной величины последовательность звонков по заданному адресу.
 */
public class TelephoneExchangeSimulator implements Runnable {
    public static final int MEAN_CALL_INTERVAL = 1300;
    public static final int Q_FACTOR_FOR_CALL_INTERVALS = 2;

    private final int CALLS_TO_IMITATE;
    private CallCenter target;
    private boolean isOn = true;

    /**
     * Создаёт новый имитатор АТС, который сгенерирует нужной длины последовательность звонков.
     * @param CALLS_TO_IMITATE  сколько звонков сгенерировать.
     */
    public TelephoneExchangeSimulator(int CALLS_TO_IMITATE) {
        this.CALLS_TO_IMITATE = CALLS_TO_IMITATE;
    }

    /**
     * Определить получателя последовательности звонков.
     * @param target Центр Звонков, в который будут направлены звонки.
     */
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
            target.putCallToQueue(call);
            calls++;
        }
        Main.consoleReport("АТС имитировала " + calls + " звонков и выключается.");
        isOn = false;
    }

    /**
     * Сообщает, продолжает ли имитатор АТС генерацию звонков.
     * @return {@code true}, если заданное количество звонков ещё не сгенерировано, {@code false}, если АТС уже имитировала заданное количество звонков.
     */
    public boolean isOn() {
        return isOn;
    }
}
