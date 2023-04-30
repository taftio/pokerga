package pokerga;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public final class Main {

  public static void main(String[] args) {

    if (args.length < 1) {
      System.err.println("Usage: java -jar pokerga <configuration file>");
      System.exit(1);
    }

    // Use 'file:' scheme for Spring config file locations
    String configLocation = args[0];
    if (!configLocation.startsWith("file:")) {
      configLocation = "file:" + configLocation;
    }

    AbstractApplicationContext context = new FileSystemXmlApplicationContext(configLocation);
    try (context) {
      // Find the Evaluator class in the application context and run it.
      Evaluator evaluator = context.getBean(Evaluator.class);
      evaluator.evaluate();

    } catch (Exception e) {
      System.err.println("An exception occurred: " + e.getMessage());
      System.exit(1);
    }

  }
}
