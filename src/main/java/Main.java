import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();

        if (args.length < 2) {
            System.out.println("Input and output location are required.");
            args = new String[2];
            try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.print("Input Location: " );
                args[0] = reader.readLine();
                System.out.print("Output Location: ");
                args[1] = reader.readLine();
            } catch (IOException e) {
                System.out.println("Error: " + e);
                return;
            }
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);

        try (var reader = Files.newBufferedReader(input)) {
            Indexes indexes = gson.fromJson(reader, Indexes.class);

            indexes.objects.forEach((path, meta) -> {
                Path hashedLocation = input.getParent().resolveSibling(meta.getPath());
                System.out.println(meta.hash + "->" + path);
                if (Files.exists(hashedLocation)) {
                    Path unhashedLocation = output.resolve(path);
                    try {
                        Files.createDirectories(unhashedLocation.getParent());
                        Files.copy(hashedLocation, unhashedLocation);
                    } catch (IOException e) {
                        System.out.println("Error: " + e);
                    }
                } else {
                    System.out.println("File not found: " + hashedLocation);
                }
            });

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        System.out.println("Done");
    }

    class Indexes {
        Map<String, FileMeta> objects;
    }

    class FileMeta {
        String hash;
        long size;

        public Path getPath() {
            return Path.of("objects", hash.substring(0, 2), hash);
        }
    }
}
