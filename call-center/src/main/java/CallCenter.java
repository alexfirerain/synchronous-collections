import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallCenter {
    private final List<Operator> operators;
    final ConcurrentLinkedQueue<Call> callQueue;
    private TelephoneExchangeSimulator callSource;

    public CallCenter(int numberOfOperators) {
        operators = new ArrayList<>(numberOfOperators);
        for (int i = 1; i <= numberOfOperators ; i++)
            operators.add(
                    new Operator(this, "Оператор №" + i)
            );
        callQueue = new ConcurrentLinkedQueue<>();
    }

    public void demo(int numberOfCalls) {
        operateCallsOn(new TelephoneExchangeSimulator(numberOfCalls));
    }

    public void operateCallsOn(TelephoneExchangeSimulator ATS) {
        callSource = ATS;
        ATS.routeCallSequenceTo(this);
        ExecutorService threads = Executors.newFixedThreadPool(1 + operators.size());
        threads.execute(ATS);
        operators.forEach(threads::execute);
//        threads.awaitTermination();
    }

    public boolean lineOff() {
        return !callSource.isOn();
    }
}
