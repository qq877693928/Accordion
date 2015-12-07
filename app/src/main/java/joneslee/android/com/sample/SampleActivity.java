package joneslee.android.com.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import joneslee.android.com.library.widget.AccordionLayout;

public class SampleActivity extends AppCompatActivity {

    private AccordionLayout mAccordionLayout;
    private boolean isEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAccordionLayout = (AccordionLayout) findViewById(R.id.layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnter) {
                    mAccordionLayout.performEnterAnimation();
                    isEnter = false;
                }else {
                    mAccordionLayout.performOutAnimation();
                    isEnter = true;
                }
            }
        });
        initData();
    }

    private void initData() {

        for (int i = 0; i < 15; i++) {
            StretchItemView itemView = StretchItemView.newInstance(this);
            itemView.setTextString(String.valueOf(i + 1));
            itemView.setIcon(Color.rgb((int) (Math.random() * 1000 % 255), (int) (Math.random() * 1000 % 255), (int) (Math.random() * 1000 % 255)));

            mAccordionLayout.addView(itemView, i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
