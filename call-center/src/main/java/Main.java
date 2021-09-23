import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {
    public static final int NUMBER_OF_CALLS = 15;
    public static final int NUMBER_OF_OPERATORS = 3;

    public static void main(String[] args) throws InterruptedException {
        consoleReport();
        CallCenter callCenter = new CallCenter(NUMBER_OF_OPERATORS);
        callCenter.demo(NUMBER_OF_CALLS);
        while (true)
            if (!callCenter.running())
                break;
        consoleReport(callCenter.threads.awaitTermination(5, SECONDS) ?
                "Дождались завершения" : "Остановили по таймауту");
        consoleReport("Центр Звонков завершил демонстрацию.");
    }

    static void timePass(int meanValue, double qFactor) {
        if (qFactor <= 0 || qFactor > meanValue) throw new IllegalArgumentException();
        if (qFactor < 1) qFactor = 1 / qFactor;
        double minValue = meanValue / qFactor;
        double maxValue = meanValue * qFactor;
        double scaleValue = Math.random();
        double actualValue = scaleValue < 0.5 ?
                minValue + scaleValue * 2 * (meanValue - minValue) :
                meanValue + (scaleValue - 0.5) * 2 * (maxValue - meanValue);
        try {
            Thread.sleep((long) actualValue);
        } catch (InterruptedException e) {
            System.out.println("Прерывание потока во время ожидания");
        }
    }

    static void consoleReport() {
        System.out.println( """
                ================
                мм:сс - событие
                ================""" );
    }

    static void consoleReport(String msg) {
        System.out.println(
                new SimpleDateFormat("mm:ss - ").format(new Date()) + msg
        );
    }

}
