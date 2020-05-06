package compfuture;

import java.util.concurrent.CompletableFuture;

public class CallbackExample {

  public static String longRunning(String s) {
    System.out.println("longRunning starting...");
    try {
      Thread.sleep(2000); // **Pretend** this is OPERATING SYSTEM WORK
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("longRunning ending...");
    return "longrun: " + s;
  }

  public static CompletableFuture<String> osCallToLongRunning(String s) {
    CompletableFuture<String> cfs = new CompletableFuture<>();
    new Thread(() -> {
      String result = longRunning(s);
      cfs.complete(result);
    }).start();
    return cfs;
  }

  public static void main(String[] args) {
    CompletableFuture<Void> cfv =
        CompletableFuture.supplyAsync(() -> "Hello")
            .thenApplyAsync(s -> s + ".txt")
            .thenComposeAsync(s -> osCallToLongRunning(s))
            .thenAcceptAsync(s -> System.out.println("Final answer: " + s));
    System.out.println("Pipeline constructed");
    cfv.join();
    System.out.println("Pipeline completed");
  }
}
