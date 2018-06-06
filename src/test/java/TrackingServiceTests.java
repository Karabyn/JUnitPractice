import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TrackingServiceTests {

    private TrackingService service;

    @BeforeClass
    public static void before() {
        System.out.println("Before Class");
    }

    @AfterClass
    public static void after() {
        System.out.println("After Class");
    }

    @Before
    public void setUp() {
        System.out.println("Before");
        service = new TrackingService(new NotifierStub());
    }

    @After
    public void tearDown() {
        System.out.println("After");
    }

    @Test
    @Category({GoodTestsCategory.class, BadCategory.class})
    public void newTrackingServiceTotalIsZero() {
        System.out.println("newTrackingServiceTotalIsZero");
        assertEquals("Tracking service total was zero", 0, service.getTotal());
    }

    @Test
    @Category(GoodTestsCategory.class)
    public void whenAddingProteinTotalIncreasesByThatAmount() {
        System.out.println("whenAddingProteinTotalIncreasesByThatAmount");
        service.addProtein(10);
        assertEquals("Protein amount was not correct", 10, service.getTotal());
        assertThat(service.getTotal(), is(10));

        assertThat(service.getTotal(), allOf(is(10), instanceOf(Integer.class)));
    }

    @Test
    @Category(GoodTestsCategory.class)
    public void whenRemovingProteinTotalRemainsZero() {
        System.out.println("whenRemovingProteinTotalRemainsZero");
        service.removeProtein(5);
        assertEquals(0, service.getTotal());
    }

    @Test
    public void whenGoalIsMetHistoryIsUpdatedWithStub() throws InvalidGoalException {
        service.setGoal(5);
        service.addProtein(6);
        HistoryItem result = service.getHistory().get(1);
        assertEquals("sent:goal met", result.getOperation());
    }

    @Test
    public void whenGoalIsMetHistoryIsUpdatedWithMock() throws InvalidGoalException {
        Mockery context = new Mockery();
        final Notifier mockNotifier = context.mock(Notifier.class);
        service = new TrackingService(mockNotifier);
        context.checking(new Expectations() {{
            oneOf(mockNotifier).send("goal met");
            will(returnValue(true));
        }});

        service.setGoal(5);
        service.addProtein(6);
        HistoryItem result = service.getHistory().get(1);
        assertEquals("sent:goal met", result.getOperation());

        context.assertIsSatisfied();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @Category(GoodTestsCategory.class)
    public void whenGoalIsSetToLessThanZeroExceptionIsThrown() throws InvalidGoalException {
        System.out.println("whenGoalIsSetToLessThanZeroExceptionIsThrown");
        thrown.expect(InvalidGoalException.class);
        thrown.expectMessage(containsString("Goal"));
        service.setGoal(-5);
    }

    @Rule
    public Timeout timeout = new Timeout(100); // applies to the entire test class

    @Test
    public void badTest() {
        for(int i = 0; i < 10000000; i++) {
            service.addProtein(1);
        }
    }

}
