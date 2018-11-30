import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPOutputStream;

public class TransactionFileGenerator {

    private static final int MONTH = 90 * 24 * 3600;

    public static void main(String[] args) {
        long batch = Long.parseLong(args[0]);
        int files = Integer.parseInt(args[1]);
        long begin = System.currentTimeMillis() / 1000 - MONTH;
        for (int i = 0; i < files; i++) {
            int id = i;
            new Thread(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                byte[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXZY-0123456789".getBytes();
                byte[] numbers = "0123456789".getBytes();
                // 0          12 14     22 24     32,34   40,42      51
                // ABCDEFGHIJKLI,000000000,000000000,1000000,1498612701\n
                byte[] bytes = "ABCDEFGHIJKLI,000000000,000000000,1000000,1498612701\n".getBytes();
//                try (OutputStream os = System.out) {
                try (OutputStream os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream("payment-" + id + ".csv.gz"),1024*1024),1024*1024,false)) {
                    for (long row = 0; row < batch; row++) {
                        int k=0;
                        // tx-id
                        while (k != 13) bytes[k++]=chars[random.nextInt(37)];
                        k++;
                        // sender account id <= 219999999999
                        bytes[k++]=numbers[random.nextInt(3)];
                        bytes[k++]=numbers[random.nextInt(10)];
                        while (k!=23) bytes[k++]=numbers[random.nextInt(10)];
                        k++;
                        // receiver account id <= 219999999999
                        bytes[k++]=numbers[random.nextInt(3)];
                        bytes[k++]=numbers[random.nextInt(10)];
                        while (k!=33) bytes[k++]=numbers[random.nextInt(10)];
                        k++;
                        // amount full integers
                        while (k!=41) bytes[k++]=numbers[random.nextInt(10)];
                        k+=3;
                        // date > 149000000
                        while (k!=52) bytes[k++]=numbers[random.nextInt(10)];
                        os.write(bytes);
                        if (row % 1_000_000 == 0) {
                            System.out.print(-id);
                            os.flush();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        Thread.yield();
    }
}