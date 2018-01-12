package co.siempo.phone;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.eyeem.chips.ChipsEditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
//  @Inject
//  Clock clock;
//
//  @Singleton
//  @Component(modules = MockClockModule.class)
//  public interface TestComponent extends DemoComponent {
//    void inject(MainActivityTest mainActivityTest);
//  }


  @Rule
  public ActivityTestRule<MainActivity_> mainActivityActivityTestRule = new ActivityTestRule<MainActivity_>(MainActivity_.class){
    @Override
    protected Intent getActivityIntent() {
      Intent intent = new Intent(InstrumentationRegistry.getContext(),MainActivity_.class);
      intent.putExtra("Key","Value");
      return intent;
    }
  };

//  @Rule
//  public ActivityTestRule<MainActivity_> activityRule = new ActivityTestRule<>(
//          MainActivity_.class,
//      true,     // initialTouchMode
//      true);   // launchActivity. False so we can customize the intent per test method

  @Before
  public void setUp() {
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
//    DemoApplication app
//        = (DemoApplication) instrumentation.getTargetContext().getApplicationContext();
//    TestComponent component = (TestComponent) app.component();
//    component.inject(this);
  }
//
//  @Test
//  public void today() {
//    Mockito.when(clock.getNow()).thenReturn(new DateTime(2008, 9, 23, 0, 0, 0));
//
//    activityRule.launchActivity(new Intent());
//
//    onView(withId(R.id.date))
//        .check(matches(withText("2008-09-23")));
//  }

  @Test
  public void intent() {
//    DateTime dateTime = new DateTime(2014, 10, 15, 0, 0, 0);
//    Intent intent = new Intent();
//    mainActivityActivityTestRule.launchActivity(intent);

   /* try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }*/
//
//

//    onView(withId(R.id.txtSearchBox)).check(matches((isDisplayed()))).perform(req);
    onView(withId(R.id.txtSearchBox)).check(matches((isDisplayed()))).perform(click()).perform(typeText("asfaf"));

//    ChipsEditText ch=(ChipsEditText) mainActivityActivityTestRule.getActivity().findViewById(R.id.txtSearchBox);
//    ch.requestFocus();
//    ch.setText("Hardik");
//    final EditText titleInput = (EditText) mainActivityActivityTestRule(R.id.titleInput);



  }
}