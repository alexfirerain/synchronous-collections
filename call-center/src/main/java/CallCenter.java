import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Кол-Центр, в котором операторы отвечают на звонки, получаемые от АТС.
 */
public class CallCenter {
    private final List<Operator> onLine;
    private final AtomicInteger operatorsOnline = new AtomicInteger();
    private final ExecutorService threads;
    private TelephoneExchangeSimulator callSource;
    private final ConcurrentLinkedQueue<Call> callQueue;

    /**
     * Инициализирует новый Центр Звонков.
     * @param numberOfOperators количество операторов.
     */
    public CallCenter(int numberOfOperators) {
        onLine = new CopyOnWriteArrayList<>();
        for (int i = 1; i <= numberOfOperators ; i++) {
            onLine.add(
                    new Operator(this, "Оператор №" + i)
            );
        }
        callQueue = new ConcurrentLinkedQueue<>();
        threads = Executors.newFixedThreadPool(1 + numberOfOperators);
    }

    // практические методы

    /**
     * Создаёт новую АТС и запускает Центр на обработку звонков от неё.
     * @param numberOfCalls количество звонков, которые сгенерирует АТС.
     */
    public void demo(int numberOfCalls) {
        operateCallsOn(new TelephoneExchangeSimulator(numberOfCalls));
    }

    /**
     * Запускает обработку Центром звонков от указанной АТС.
     * @param source АТС, от которой будет обработана последовательность звонков.
     */
    public void operateCallsOn(TelephoneExchangeSimulator source) {
        source.routeCallSequenceTo(this);
        callSource = source;
        ExecutorService threads = Executors.newFixedThreadPool(1 + onLine.size());
        threads.submit(source);
        onLine.forEach(threads::submit);
    }

    // методы работы с очередью звонков

    /**
     * Возвращает количество непринятых звонков, ожидающих в очереди.
     * @return длину очереди звонков.
     */
    public int queueLength() {
        return callQueue.size();
    }

    /**
     * Возвращает звонок, дольше всех ожидающий ответа, и удаляет его из очереди.
     * @return звонок из головы очереди или {@code null}, если очередь пуста.
     */
    public Call takeCallFromQueue() {
        return callQueue.poll();
    }

    /**
     * Ставит новый звонок в очередь ожидания.
     * @param call звонок, который будет поставлен в хвост очереди.
     */
    public void putCallToQueue(Call call) {
        callQueue.add(call);
    }

    // методы организации работы Центра

    /**
     * Проверяет, не отключилась ли уже АТС.
     * @return {@code true}, если АТС уже отключилась, {@code false}, если АТС ещё генерирует звонки.
     */
    public boolean lineOff() {
        return !callSource.isOn();
    }

    /**
     * Проверяет, есть ли в данный момент работающие операторы.
     * @return {@code true}, если список операторов на линии не пуст, {@code false}, если все операторы уже завершили работу.
     */
    public boolean running() {
        return !onLine.isEmpty();
    }

    /**
     * Отпускает оператора со смены (удаляет его из списка операторов на линии и декрементирует счётчик работающих операторов).
     * @param worker оператор, завершающий работу.
     */
    public void releaseOperator(Operator worker) {
        onLine.remove(worker);
        operatorsOnline.decrementAndGet();
    }

    /**
     * Запускает оператора на смену (инкрементирует счётчик работающих операторов).
     */
    public void operatorStarted() {
        operatorsOnline.incrementAndGet();
    }

    /**
     * Сообщает о количестве операторов на линии в данный момент.
     * @return значение счётчика работающих операторов.
     */
    public int runningOperators() {
        return operatorsOnline.get();
    }

    /**
     * Пытается загасить обойму потоков, в которой выполнялись АТС и операторы.
     */
    public void shutdown() {
        threads.shutdownNow();
    }

    /**
     * Пытается 5 секунд дождаться окончания выполняемых в обойме потоков и вырубить её.
     * @return сообщение о том, производится ли выключение по окончанию потоков либо по таймауту.
     * @throws InterruptedException если во время ожидания этот поток прерывается извне.
     */
    public String waitAndShut() throws InterruptedException {
        return threads.awaitTermination(5, SECONDS) ?
                "Дождались завершения" : "Остановили по таймауту";
    }
}
