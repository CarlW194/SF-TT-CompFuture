package compfuture;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

interface ExFunction<E, F> {
  F apply(E e) throws Throwable;
  public static <E, F> Function<E, F> wrap(ExFunction<E, F> op) {
    return e -> {
      try {
        return op.apply(e);
      } catch (Throwable throwable) {
        throw new RuntimeException("Wrapped Exception", throwable);
      }
    };
  }
}

public class Ex1 {
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

    CompletableFuture<Void> cfv = CompletableFuture.supplyAsync(() -> {
      System.out.println("supplyAsync running in "
          + Thread.currentThread().getName());
      return "Hello";
    })
        .thenApplyAsync(s -> {
          System.out.println("thenApply starting");
          delay(1000);
          return s.toUpperCase();
        })
        .thenApplyAsync(ExFunction.wrap(s -> mightBreak(s)))
        .thenAcceptAsync(s -> System.out.println("Result is " + s));
    System.out.println("Pipeline built");
    cfv.join();
    System.out.println("Pipeline finished execution");
//    String result = cfv.get();
//    System.out.println("Pipeline Completed, result is " + result);
  }
}
