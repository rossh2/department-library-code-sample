package pa2.testSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({BookTests.class, SplayTreeNodeTests.class, SplayTreeUtilsTests.class, SplayTreeDigitalLibraryTests.class,
        SplayTreeDigitalLibraryRunnerTests.class})
public class masterTestSuite {
}
