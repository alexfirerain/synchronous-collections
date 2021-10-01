/**
 * Оператор – работника Центра.
 */
public class Operator implements Runnable {
    private static final int MEAN_REST = 900;
    private static final double Q_FACTOR_FOR_REST = 1.5;
    private static final int MEAN_TALK = 3000;
    private static final double Q_FACTOR_FOR_TALK = 4;

    private final CallCenter office;
    private final String name;

    /**
     * Создаёт нового оператора с определённым именем и местом работы.
     * @param office Центр, в котором работает.
     * @param name   отображаемое имя для представления объекта.
     */
    public Operator(CallCenter office, String name) {
        this.office = office;
        this.name = name;
    }

    @Override
    public void run() {
        office.operatorStarted();
        Main.consoleReport(this + " вышел на работу");
        while (true) {
            rest();
            Call call = office.takeCallFromQueue();
            if (call != null) {
                processACall(call);
            } else if (office.lineOff()) {
                office.releaseOperator(this);
                break;
            }
        }
        Main.consoleReport(this + " закончил работу");
        if (office.runningOperators() == 0) {
            Main.consoleReport("Последний закрывает офис");
            office.shutdown();
        }
    }

    /**
     * Симулирует паузу в работе оператора между обработкой звонков.
     */
    private void rest() {
        Main.timePass(MEAN_REST, Q_FACTOR_FOR_REST);
    }

    /**
     * Симулирует обработку звонка оператором.
     * @param request обрабатываемый звонок.
     */
    private void processACall(Call request) {
        Main.consoleReport(name + " принимает " + request + (
                office.queueLength() > 0 ?
                " (ещё в очереди: " + office.queueLength() + ")" :
                ""));
        Main.timePass(MEAN_TALK, Q_FACTOR_FOR_TALK);
        Main.consoleReport(name + " завершает " + request);
    }

    @Override
    public String toString() {
        return name;
    }
}
