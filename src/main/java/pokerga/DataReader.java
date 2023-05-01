package pokerga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.springframework.beans.factory.InitializingBean;

public final class DataReader implements InitializingBean {

  private static final Pattern regex = Pattern.compile(",");

  private File file = null;

  public void setFile(File file) {
    this.file = file;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Objects.requireNonNull(file);
    if (!file.isFile()) {
      throw new IllegalArgumentException("File is not a regular files or doesn't exist.");
    }
  }

  public void read(Consumer<Hand> consumer) throws IOException {
    try (BufferedReader reader = reader(file)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }

        String[] parts = regex.split(line);
        Hand hand = Hand.from(parts);
        consumer.accept(hand);
      }
    }
  }

  private static BufferedReader reader(File file) throws IOException {
    InputStream is = new FileInputStream(file);
    Reader rd = new InputStreamReader(is, StandardCharsets.UTF_8);
    return new BufferedReader(rd);
  }
}
