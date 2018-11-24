# vertx-unit-future

Future support for Vertx.x JUnit tests.

[https://jitpack.io/#gofabian/vertx-unit-future](https://jitpack.io/#gofabian/vertx-unit-future)

[![Release](https://jitpack.io/v/gofabian/vertx-unit-future.svg)](https://jitpack.io/#gofabian/vertx-unit-future)


## Installation

To use it in your Maven build add:
```xml
  <repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
  </repositories>
```

and the dependency:

```xml
	<dependency>
		<groupId>com.github.gofabian</groupId>
		<artifactId>vertx-unit-future</artifactId>
		<version>x.x.x</version>
	</dependency>
```


## Getting started

The FutureVertxRunner adds Future support to the VertxUnitRunner.

Syntax:

- Parameters: `TestContext` or none
- Return type: `Future` or `void`
- Supported methods: `@Test`, `@Before`, `@After`, `@BeforeClass`, `@AfterClass`

```java
    @RunWith(FutureVertxRunner.class)
    public class MyTest {
        
        @BeforeClass
        public static Future setUpClass() {
            return Future.succeededFuture();
        }
    
        @Before
        public Future setUp() {
            return Future.succeededFuture();
        }
    
        @Test
        public Future testFutureResult(TestContext context) {
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

    }
```


## Examples

Success:

```java
    @Test
    public Future testSuccess() {
        return Future.succeededFuture();
    }
```

Failure:

```java
    @Test
    public Future testFailure() {
        return Future.failedFuture("error");
    }
```

Incomplete (timeout):

```java
    @Test
    public static Future testTimeout() {
        return Future.future(); // incomplete future
    }
```

Returning `null` will behave like a `void` return type:

```java
    @Test
    public Future testNormal() {
        return null; // success
    }
```
