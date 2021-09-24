import java.util.Map;

public class MapsCompareTester extends Tester {

    public MapsCompareTester(int[] dataSizes, int arrValMin, int arrValMax, int repetitions) {
        super(dataSizes,
                arrValMin,
                arrValMax,
                repetitions);
    }

    @Override
    protected void executeTesting() {
        // для каждого из размеров данных
        for (int arrLength : dataSizes) {
            // подготовить данные
            Integer[] testArray = ArrGenerator.generate(arrLength, arrValMin, arrValMax);
            Map<Integer, Integer> synchronizedMap = null;
            Map<Integer, Integer> concurrentMap = null;

            report.append("----------Карта из %d пар-------------\n".formatted(arrLength))
                    // выполнение тэста на функции А
                    .append("\tСинхронизированная карта:\n")
                    .append(executeSingleBatch(synchronizedMap, repetitions))
                    // выполнение тэста на функции Б
                    .append("\tМногопоточная карта:\n")
                    .append(executeSingleBatch(concurrentMap, repetitions))
                    .append("\n");
        }
        System.out.println(report);
    }

    private String executeSingleBatch(Map<Integer, Integer> map, int repetitions) {
        return null;
    }
}
