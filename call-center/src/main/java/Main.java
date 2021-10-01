import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс для демонстрации работы Центра Звонков, также содержащий общие служебные функции для его работы.
 */
public class Main {
    private static final int NUMBER_OF_CALLS = 60;
    private static final int NUMBER_OF_OPERATORS = 3;

    public static void main(String[] args) throws InterruptedException {
        consoleReport();
        CallCenter callCenter = new CallCenter(NUMBER_OF_OPERATORS);
        callCenter.demo(NUMBER_OF_CALLS);
        while (true) {
            if (!callCenter.running()) {
                break;
            }
        }
        consoleReport(callCenter.waitAndShut());
        consoleReport("Центр Звонков завершил демонстрацию.");
    }

    /**
     * Генерирует случайную задержку в работе потока, симулирующую реальный ход времени при выполнении имитируемых задач.
     * Задержка, с вероятностью 50%, имеет величину от <средняя> до <средняя / добротность>
     * или, с вероятностью 50%, от <средняя> до <средняя * добротность>.
     * @param meanValue среднее значение для вычисления задержки.
     * @param qFactor   показатель добротности распределения.
     */
    static void timePass(int meanValue, double qFactor) {
        if (meanValue <= 0 || qFactor * meanValue < 1 || qFactor > meanValue) {
            throw new IllegalArgumentException();
        }
        if (qFactor < 1) {
            qFactor = 1 / qFactor;
        }
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

    /**
     * Выводит в консоль шаблон-заголовок списка событий.
     */
    static void consoleReport() {
        System.out.println( """
                ================
                мм:сс - событие
                ================""" );
    }

    /**
     * Выводит в консоль текущее время (минуты:секунды) и сообщение о событии.
     * @param msg сообщение о событии.
     */
    static void consoleReport(String msg) {
        System.out.println(
                new SimpleDateFormat("mm:ss - ").format(new Date()) + msg
        );
    }

}
