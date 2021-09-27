public class Main {
    static final int[] DATA_SIZES = {
            10,
            100,
            1000,
            10_000,
            100_000,
            1_000_000,
//            10_000_000,
//            100_000_000,
//            1_000_000_000
    };

    public static void main(String[] args) throws InterruptedException {
//        System.out.println("""
//                Тестовое сравнение двух способов суммирования.
//                Данная реализация сначала всё считает, потом выводит.
//                Это может занять некоторое время, к тому же может кончиться память.
//
//                """);
//        Tester arraysSumTester = new ArraysSumTester(DATA_SIZES, 0, 255, 10, 7);
//        arraysSumTester.executeTesting();

                System.out.println("""
                Тестовое сравнение производительности двух видов карты.
                Данная реализация сначала всё считает, потом выводит.
                Это может занять некоторое время (до нескольких минут для размеров данных от миллиона),
                к тому же может кончиться память.

                """);
        Tester mapsCompareTester = new MapsCompareTester(DATA_SIZES, 0, 255, 10, 5);
        mapsCompareTester.executeTesting();

    }
}
