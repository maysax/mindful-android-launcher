package minium.co.contacts.ui;

import android.content.Intent;
import android.os.Bundle;

import com.android.contacts.activities.PeopleActivity;

import org.androidannotations.annotations.EActivity;

import minium.co.contacts.R;
import minium.co.core.ui.CoreActivity;


@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, PeopleActivity.class));
    }
}
