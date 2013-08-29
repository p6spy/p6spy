# Known Issues

## Non-standard (driver specific) JDBC methods are not supported without using a workaround.

Many drivers provide methods that expose driver-specific, non-standard functionality. Most developers do not use these features, but in the event that an application does use these features they are not natively supported by P6Spy. For example, the MySQL JDBC drivers allow you to call the auto-increment function as follows:

    ((org.gjt.mm.mysql.PreparedStatement)stmt).getLastInsertId();

That cast fails when P6SpyDriver is in place, since the class it tries to cast is against P6SpyDriver itself. P6SpyDriver does not expose that driver-specific method and the P6Spy PreparedStatement class is not a subclass of org.gjt.mm.mysql.PreparedStatement. The one workaround available requires code changes in the application. Each P6Spy class exposes a method called getJDBC() that returns the real JDBC driver. With this method, an application can cast a class, such as PreparedStatement, to P6PreparedStatement, invoke getJDBC(), and then cast the returned value to the native driver class, as in OraclePreparedStatement. In this example, the non-JDBC statement is not logged but the application will continue to function.

Ideally, a long-term, native solution will be provided. One idea under consideration is to provide vendor-specific versions (Oracle or MySQL, for example) of P6Spy that subclass the vendor driver, so a cast can be successfully executed. These versions would expose the vendor-specific methods. However, this is not yet under development, and other suggestions are welcome