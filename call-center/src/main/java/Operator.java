public class Operator implements Runnable {
    private final CallCenter office;
    private final String name;

    public Operator(CallCenter office, String name) {
        this.office = office;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " вышел на работу");
        while (true) {
            rest();
            System.out.println(name + " на линии");
            if (!office.callQueue.isEmpty()) {
                System.out.println("Звонков в очереди: " + office.callQueue.size());
                Call call = office.callQueue.poll();
                processACall(call);
            } else if (office.lineOff()) {
                break;
            }
        }
        System.out.println(name + " закончил работу");
    }

    private void rest() {
        Main.timePass(900, 1.5);
    }

    private void processACall(Call request) {
        System.out.println(name + " взял " + request);
        Main.timePass(3000, 4);
        System.out.println(name + " завершил " + request);
    }

    @Override
    public String toString() {
        return name;
    }
}
