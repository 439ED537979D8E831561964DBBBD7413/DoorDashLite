package adamhurwitz.github.io.doordashlite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import adamhurwitz.github.io.doordashlite.DependencyInjection.DaggerDataComponent;
import adamhurwitz.github.io.doordashlite.DependencyInjection.DataComponent;
import adamhurwitz.github.io.doordashlite.UI.MainActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainViewTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private DataComponent getCom() {
        return DaggerDataComponent.builder()
                .doorDashModule(new TestDataModule(getAppFromInstrumentation()))
                .build();
    }

    @Before
    public void init() {
        getAppFromInstrumentation().setComponent(getCom());
    }

    private DoorDashLiteApplication getAppFromInstrumentation() {
        return (DoorDashLiteApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    }

    @Test
    public void useAppContext() throws Exception {
        //Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("adamhurwitz.github.io.doordashlite", appContext.getPackageName());
    }

    @Test
    public void checkDbPopulationNotNull() throws Exception {
        MainViewModel.getRestaurantsFromDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurants -> assertEquals(true, !restaurants.isEmpty()));
    }

    @Test
    public void checkFavIsSaved() throws Exception {
        int checkPositionAt = 0;
        try {
            onView(withId(R.id.recyclerView)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(checkPositionAt, MyViewAction.clickChildViewWithId(R.id.fav)));
        } catch (PerformException e) {
            Log.v(MainViewTests.class.getSimpleName(), "ERROR, RecyclerView Click: " + e.toString());
            e.printStackTrace();
        }

        assertEquals(true, MainViewModel.getRestaurantsFromDb().toBlocking().first().get(checkPositionAt).isFavorite());
    }

    @Test
    public void checkRecyclerViewIsPopulated() throws Exception {
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(247));
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            assertEquals(expectedCount, MainViewModel.getRestaurantsFromDb().toBlocking().first().size());
        }
    }

}

