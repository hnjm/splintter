package com.ksss.splintter.ui.group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import com.ksss.splintter.R;
import com.ksss.splintter.data.model.Group;
import com.ksss.splintter.ui.group.view.ExpenseManager;
import com.ksss.splintter.ui.group.view.GroupExpensesFragment;
import com.ksss.splintter.ui.group.view.GroupExpensesSummaryFragment;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by Nahuel Barrios on 7/16/16.
 */
public class GroupActivity extends AppCompatActivity implements ExpenseManager {

    private Group group;

    private enum ViewMode {
        CREATE("create"), VIEW("view");

        private final String id;

        ViewMode(String action) {
            id = action;
        }

        /* default */
        static ViewMode from(final String action) {
            ViewMode result = VIEW;

            for (ViewMode each : values()) {
                if (each.id.equals(action)) {
                    result = each;
                    break;
                }
            }

            return result;
        }
    }

    @NonNull
    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewMode mode = ViewMode.from(getIntent().getData().getPathSegments().get(0));

        if (mode == ViewMode.CREATE) {
            // TODO: 7/17/16 What about creating a temporal name to let users write down what they really want?
            handleCreate();
        } else {
            mockView();
            getSupportActionBar().setTitle(group.getName());
        }

        handleView();
    }

    /**
     * TODO: 7/17/16 Stop mocking this!!
     */
    @DebugLog
    private void mockView() {
        final Realm db = Realm.getDefaultInstance();

        group = db.where(Group.class).findFirst();

        db.close();
    }

    private void handleView() {
        setContentView(R.layout.group_view);

        createViewPager();
    }

    @DebugLog
    private void createViewPager() {
        final GroupPagerAdapter pagerAdapter = new GroupPagerAdapter(getSupportFragmentManager());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
    }

    private void handleCreate() {
        final EditText input = new EditText(this);
        input.setSingleLine();
        input.setHint(R.string.create_group_dialog_hint);
        new AlertDialog.Builder(this)
                .setView(input)
                .setTitle(R.string.create_group_dialog_title)
                .setPositiveButton(R.string.create_group_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Timber.e("Creating group as: %s", input.getText());
                        group = new Group(input.getText().toString());
                        getSupportActionBar().setTitle(group.getName());
                    }
                })
                .setNegativeButton(R.string.create_group_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();
                    }
                }).show();
    }

    private class GroupPagerAdapter extends FragmentPagerAdapter {

        /* default */ GroupPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment result;

            switch (position) {
                case 0:
                    result = new GroupExpensesFragment();
                    break;
                case 1:
                    result = new GroupExpensesSummaryFragment();
                    break;
                default:
                    Timber.e("It's all wrong!!");
                    result = new GroupExpensesFragment();
            }

            return result;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int result;
            switch (position) {
                case 0:
                    result = R.string.view_group_expenses_pager_title;
                    break;
                case 1:
                    result = R.string.view_group_expenses_summary_pager_title;
                    break;
                default:
                    Timber.e("It's all wrong!!");
                    result = R.string.view_group_expenses_pager_title;
            }

            return getText(result);
        }
    }

    @Override
    public String toString() {
        return "GroupActivity{" +
            "group=" + group +
            '}';
    }
}