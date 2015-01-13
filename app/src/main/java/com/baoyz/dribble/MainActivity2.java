package com.baoyz.dribble;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.baoyz.dribble.model.Shot;
import com.baoyz.dribble.network.DribbleClient;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity2 extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolBar;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    MaterialMenuIconToolbar mMenu;

    boolean isDrawerOpened;
    private View draw;

    @Inject
    DribbleClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        mToolBar.setLogo(R.drawable.img_toolbar_logo);

        mMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.toolbar;
            }
        };

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        draw = findViewById(R.id.navigation_drawer);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mMenu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpened ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (isDrawerOpened) mMenu.setState(MaterialMenuDrawable.IconState.ARROW);
                    else mMenu.setState(MaterialMenuDrawable.IconState.BURGER);
                }
            }
        });
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(draw))
                    drawerLayout.closeDrawers();
                else
                    drawerLayout.openDrawer(draw);
            }
        });

        ObjectGraph.create(AppModule.class).inject(this);

        mClient.shots("animated", 1, new Callback<List<Shot>>() {
            @Override
            public void success(List<Shot> shots, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isDrawerOpened = drawerLayout.isDrawerOpen(Gravity.START);
        mMenu.syncState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mMenu.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
