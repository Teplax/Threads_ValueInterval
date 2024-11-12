import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Via_ThreadPool {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }


        long startTs = System.currentTimeMillis(); // start time
        ExecutorService threadPool = Executors.newFixedThreadPool(texts.length);
        List<Future<Integer>> futures = new ArrayList<>(texts.length);
        for (String text : texts) { //запускаем цикл по созданным строкам
            futures.add(
            threadPool.submit( //добавляем потоки в список
                     //создаём для обработки каждой строки отдельный поток
                            () -> { //передаём реализацию интерфейса Runnable через лямбда-функцию
                                int maxSize = 0;
                                for (int i = 0; i < text.length(); i++) {
                                    for (int j = 0; j < text.length(); j++) {
                                        if (i >= j) {
                                            continue;
                                        }
                                        boolean bFound = false;
                                        for (int k = i; k < j; k++) {
                                            if (text.charAt(k) == 'b') {
                                                bFound = true;
                                                break;
                                            }
                                        }
                                        if (!bFound && maxSize < j - i) {
                                            maxSize = j - i;
                                        }
                                    }
                                }
                                return(maxSize);
                            }
                    )
            );

        }
        List<Integer> results = new ArrayList<>();
        for (Future<Integer> future: futures) { //запускаем потоки из списка
            results.add(future.get());
        }
        results.sort(Comparator.naturalOrder());
        System.out.println(results.getLast());
        long endTs = System.currentTimeMillis(); // end time
        threadPool.shutdown();

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}
