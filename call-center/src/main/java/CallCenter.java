import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CallCenter {
    private final List<Operator> operators;
    final ConcurrentLinkedQueue<Call> callQueue;
    private TelephoneExchangeSimulator callSource;
    ExecutorService threads;
    AtomicInteger operatorsOnline = new AtomicInteger();

    public CallCenter(int numberOfOperators) {
        operators = new ArrayList<>(numberOfOperators);
        for (int i = 1; i <= numberOfOperators ; i++)
            operators.add(
                    new Operator(this, "Оператор №" + i)
            );
        callQueue = new ConcurrentLinkedQueue<>();
        threads = Executors.newFixedThreadPool(1 + numberOfOperators);
    }

    public void demo(int numberOfCalls) {
        operateCallsOn(new TelephoneExchangeSimulator(numberOfCalls));
    }

    public void operateCallsOn(TelephoneExchangeSimulator ATS) {
        ATS.routeCallSequenceTo(this);
        callSource = ATS;
        ExecutorService threads = Executors.newFixedThreadPool(1 + operators.size());
        threads.submit(ATS);
        operators.forEach(threads::submit);
    }

    public boolean lineOff() {
        return !callSource.isOn();
    }
}
