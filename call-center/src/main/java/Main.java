public class Main {
    public static final int NUMBER_OF_CALLS = 7;

    static void timePass(int meanValue, double qFactor) {
        if (qFactor < 1)
            qFactor = 1 / qFactor;
        double minValue = meanValue / qFactor;
        double maxValue = meanValue * qFactor;
        double scaleValue = Math.random();
        double actualValue = scaleValue < 0.5 ?
                minValue + scaleValue * (meanValue - minValue) :
                meanValue + (scaleValue - 0.5) * (maxValue - meanValue);
        try {
            Thread.sleep((long) actualValue);
        } catch (InterruptedException e) {
            System.out.println("Прерывание потока во время ожидания");
        }
    }

    public static void main(String[] args) {
        CallCenter callCenter = new CallCenter(3);
        callCenter.demo(NUMBER_OF_CALLS);
//        TelephoneExchangeSimulator ats = new TelephoneExchangeSimulator(60);
//        callCenter.operateCallsOn(ats);
    }

}
