package de.gofabian.vertx.test;

import io.vertx.core.Future;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;

@RunWith(FutureVertxRunner.class)
public class FutureVertxRunnerTest {

    @BeforeClass
    public static Future setUpClass() {
        return Future.succeededFuture();
    }

    @Before
    public Future setUp() {
        return Future.succeededFuture();
    }

    @After
    public Future tearDown() {
        return Future.succeededFuture();
    }

    @AfterClass
    public static Future tearDownClass() {
        return Future.succeededFuture();
    }


    @Test
    public void normalSuccess() {
    }

    @Test(expected = AssertionError.class)
    public void normalFailure() {
        fail();
    }

    @Test
    public void vertxSuccess(TestContext context) {
        Async async = context.async();
        runAsynchronous(async::complete);
    }

    @Test(expected = AssertionError.class)
    public void vertxFailure(TestContext context) {
        context.async();
        runAsynchronous(context::fail);
    }

    @Test
    public Future futureSuccess() {
        return Future.succeededFuture();
    }

    @Test(expected = AssertionError.class)
    public Future futureFailure() {
        return Future.failedFuture(new AssertionError());
    }

    @Test
    public Future futureSuccessDueToNull() {
        return null;
    }

    @Test(expected = AssertionError.class)
    public Future futureFailureDueToException() {
        throw new AssertionError();
    }

    @Test
    public Future futureSuccessWithContextUsage(TestContext context) {
        Async async = context.async();
        runAsynchronous(async::complete);
        return Future.succeededFuture();
    }

    @Test(expected = AssertionError.class)
    public Future futureFailureDueToContextUsage(TestContext context) {
        context.async();
        context.fail(new AssertionError());
        return Future.succeededFuture();
    }

    @Test(expected = NumberFormatException.class)
    public Future futureFailureThoughContextUsage(TestContext context) {
        context.async().complete();
        return Future.failedFuture(new NumberFormatException());
    }

    @Test(timeout = 1L, expected = TimeoutException.class)
    public Future timeoutDueToIncompleteFuture() {
        return Future.future();
    }

    private void runAsynchronous(Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                runnable.run();
            } catch (AssertionError e) {
                // drop
            }
        }).start();
    }

}
