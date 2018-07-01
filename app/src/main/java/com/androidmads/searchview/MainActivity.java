package com.androidmads.searchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout revealLayout;
    EditText searchBox;
    boolean hidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        revealLayout = findViewById(R.id.reveal_items);
        revealLayout.setVisibility(View.INVISIBLE);
        searchBox = findViewById(R.id.search_box);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handleKeyboard(searchBox);
                    searchBox.clearFocus();
                    Toast.makeText(MainActivity.this, textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    void handleKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reveal, menu);
        if (hidden) {
            menu.findItem(R.id.action_search).setIcon(R.drawable.ic_search);
        } else {
            menu.findItem(R.id.action_search).setIcon(R.drawable.ic_close);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                int cx = revealLayout.getRight();
                int cy = revealLayout.getTop();
                makeEffect(revealLayout, cx, cy);
                searchBox.setText(null);
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeEffect(final LinearLayout layout, int cx, int cy) {

        int radius = Math.max(layout.getWidth(), layout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);

            SupportAnimator animator_reverse = animator.reverse();

            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                animator.start();
                invalidateOptionsMenu();
                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        layout.setVisibility(View.INVISIBLE);
                        invalidateOptionsMenu();
                        hidden = true;
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();
            }
        } else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
                layout.setVisibility(View.VISIBLE);
                anim.start();
                invalidateOptionsMenu();
                hidden = false;

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                        invalidateOptionsMenu();
                    }
                });
                anim.start();
            }
        }
    }
}
