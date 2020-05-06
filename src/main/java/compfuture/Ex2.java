package compfuture;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Ex2 {
  private static void delay(int d) {
    try {
      Thread.sleep(d);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  public static String mightBreak(String s) throws SQLException {
    if (Math.random() > 0.5) throw new SQLException("Database Broke!");
    else return "Didn't Break: " + s;
  }
  public static void main(String[] args) throws Throwable {

    CompletableFuture<Void> cfv = CompletableFuture.supplyAsync(() -> "Hello")
        .thenApplyAsync(s -> {
          delay(1000);
          return s.toUpperCase();
        })
        .thenApplyAsync(ExFunction.wrap(s -> mightBreak(s)))
        .thenApplyAsync(s -> "apply3-" + s)
        .thenApplyAsync(s -> "apply4-" + s)
        .handleAsync((s, t) -> {
          System.out.println("in handleAsync s=" + s
          + ", t=" + t);
          return (s != null) ? s : t.getCause().getCause().getMessage();
        })
        .thenApplyAsync(s -> "accept5-" + s)
        .thenAcceptAsync(s -> System.out.println("Result is " + s));
    System.out.println("Pipeline built");
    cfv.join();
    System.out.println("Pipeline finished execution");
  }
}
