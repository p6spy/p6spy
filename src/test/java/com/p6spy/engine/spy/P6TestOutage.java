package com.p6spy.engine.spy;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.p6spy.engine.outage.P6OutageOptions;

@RunWith(Parameterized.class)
public class P6TestOutage extends P6TestFramework {

  public P6TestOutage(String db) throws SQLException, IOException {
    super(db);
  }

  // make sure outage is properly configured
  private static final Collection<Object[]> DBS_IN_TEST = Arrays
      .asList(new Object[][] { { "outage" } });

  /**
   * Always returns {@link P6TestOptions#dbs()} as we don't need to rerun for each DB here, rather
   * we run for the specific config only.
   * 
   * @return
   */
  @Parameters
  public static Collection<Object[]> dbs() {
    return DBS_IN_TEST;
  }

  /** DB alias making response delayed by sleep. */
  public static final String SLEEP_ALIAS = "CREATE ALIAS SLEEP AS $$ "
      + " void sleep(long miliseconds) {" 
      + "   try { "
      + "         java.lang.Thread.sleep(miliseconds); "
      + "   } catch (java.lang.InterruptedException e) {} " 
      + " } $$;";

  @Before
  public void activateOutage() throws SQLException {
    // disable outage, as procedure creation takes already quite a while
    P6OutageOptions.getActiveInstance().setOutageDetection("false");
    Statement statement = connection.createStatement();
    statement.execute(SLEEP_ALIAS);
    P6OutageOptions.getActiveInstance().setOutageDetection("true");
  }

  @Test
  public void testOutage() throws SQLException {
    // exec fast query => no outage detected
    callSleep(1);
    Assert.assertFalse(super.getLastButOneLogEntry().contains("OUTAGE"));
    Assert.assertFalse(super.getLastLogEntry().contains("OUTAGE"));
    Assert.assertTrue(super.getLastLogEntry().contains("CALL SLEEP"));

    // exec slooooow query => outage detected
    callSleep(2000);
    Assert.assertTrue(super.getLastButOneLogEntry().contains("OUTAGE"));
    Assert.assertTrue(super.getLastLogEntry().contains("CALL SLEEP"));
  }

  private void callSleep(long miliseconds) throws SQLException {
    final Statement statement = connection.createStatement();
    statement.executeQuery("CALL SLEEP( " + miliseconds + ")");
  }
}
