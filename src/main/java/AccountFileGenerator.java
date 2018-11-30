import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class AccountFileGenerator {
    public static void main(String[] args) {
        int files = Integer.parseInt(args[0]);
        long total = 2_300_000_000L;
		if (args.length > 1) total = Long.parseLong(args[1]);
        long chunk = total / files + (total % files > 0 ? 1 : 0);
        for (int i = 0; i < files; i++) {
            int id = i;
            new Thread(() -> {
                try (OutputStream os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream("account-" + id + ".csv.gz"),1024*1024),1024*1024,false)) {
                    for (long row = 0; row < chunk; row++) {
                        os.write(Long.toString(row + id * chunk).getBytes());
                        os.write('\n');
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
