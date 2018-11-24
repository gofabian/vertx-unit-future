package de.gofabian.vertx.test;

import io.vertx.core.Future;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The FutureVertxRunner adds {@link Future} support to the {@link VertxUnitRunner} and supports methods with these
 * annotations: @{@link Test}, @{@link BeforeClass}, @{@link Before}, @{@link After} and @{@link AfterClass}.
 *
 * <p>
 * Successful method:
 * <pre>
 * &#64;Test
 * public Future testSuccess() {
 *   return Future.succeededFuture();
 * }
 * </pre>
 *
 * <p>
 * Failing method:
 * <pre>
 * &#64;Test
 * public Future testFailure() {
 *   return Future.failedFuture("error");
 * }
 * </pre>
 *
 * <p>
 * No result (timeout):
 * <pre>
 * &#64;Test
 * public static Future testTimeout() {
 *   return Future.future(); // incomplete future
 * }
 * </pre>
 *
 * <p>
 * Returning null will behave like a void return type:
 * <pre>
 * &#64;Test
 * public Future testNormal() {
 *   return null; // success
 * }
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public class FutureVertxRunner extends VertxUnitRunner {

    public FutureVertxRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
        if (annotation == Test.class || annotation == Before.class || annotation == After.class ||
                annotation == BeforeClass.class || annotation == AfterClass.class) {
            List<FrameworkMethod> fMethods = getTestClass().getAnnotatedMethods(annotation);
            for (FrameworkMethod fMethod : fMethods) {
                validateMethod(fMethod, isStatic, errors);
            }
        } else {
            super.validatePublicVoidNoArgMethods(annotation, isStatic, errors);
        }
    }

    protected void validateMethod(FrameworkMethod fMethod, boolean isStatic, List<Throwable> errors) {
        validateMethodPublicStatic(fMethod, isStatic, errors);
        validateMethodReturnType(fMethod, errors);
        validateMethodParameters(fMethod, errors);
    }

    protected final void validateMethodPublicStatic(FrameworkMethod fMethod, boolean isStatic, List<Throwable> errors) {
        if (fMethod.isStatic() != isStatic) {
            String state = isStatic ? "should" : "should not";
            errors.add(new Exception("Method " + fMethod.getMethod().getName() + "() " + state + " be static"));
        }
        if (!fMethod.isPublic()) {
            errors.add(new Exception("Method " + fMethod.getMethod().getName() + "() should be public"));
        }
    }

    protected void validateMethodReturnType(FrameworkMethod fMethod, List<Throwable> errors) {
        Class<?> returnType = fMethod.getMethod().getReturnType();
        if ((returnType != Void.TYPE) && (returnType != Future.class)) {
            errors.add(new Exception("Method " + fMethod.getName() + "() should have no or the " +
                    Future.class.getName() + " return type"));
        }
    }

    protected void validateMethodParameters(FrameworkMethod fMethod, List<Throwable> errors) {
        try {
            // original VertxUnitRunner does that for us
            super.validateTestMethod(fMethod);
        } catch (Exception e) {
            errors.add(e);
        }
    }

    @Override
    protected void validateTestMethod(FrameworkMethod fMethod) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void invokeTestMethod(FrameworkMethod fMethod, Object test, TestContext context) throws InvocationTargetException, IllegalAccessException {
        Method method = fMethod.getMethod();
        Class<?>[] paramTypes = method.getParameterTypes();

        Object result;
        if (paramTypes.length == 0) {
            result = method.invoke(test);
        } else {
            result = method.invoke(test, context);
        }

        if (result instanceof Future) {
            Future<?> future = (Future<?>) result;
            future.setHandler(context.asyncAssertSuccess());
        }
    }

}
