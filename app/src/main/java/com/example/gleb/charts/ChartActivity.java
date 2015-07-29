package com.example.gleb.charts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gleb.autoresationregistrator.Autoresation;
import com.example.gleb.dentistcard.R;
import com.example.gleb.fragments.SlidingTabLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.AbstractChartView;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * Created by gleb on 28.07.15.
 */
public class ChartActivity extends ActionBarActivity {
    public static final String TAG = "TAG";
    public Toolbar toolbar;
    public ViewPager pager;
    public SlidingTabLayout tabs;
    public ActionMode actionMode;
    public Drawer.Result drawerResult = null;
    public FragmentStatePagerAdapter adapter;

    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        listView = (ListView) findViewById(android.R.id.list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.Changes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChartSamplesAdapter adapter = new ChartSamplesAdapter(this, 0, generateSamplesDescriptions());
        listView.setAdapter(adapter);

        //Initialise Navigation Drawer
        Drawer drawer = new Drawer();
        drawer.withActivity(this);
        drawer.withToolbar(toolbar);
        drawer.withActionBarDrawerToggle(true);
        drawer.withHeader(R.layout.drawer_header);

        drawer.addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                new PrimaryDrawerItem().withName(R.string.ShowMail).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(2),
                new PrimaryDrawerItem().withName(R.string.SkillDoctor).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3),
                new SectionDrawerItem().withName(R.string.drawer_item_settings),
                new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(4),
                new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false).withIdentifier(5),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
        );

        drawer.withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                // Скрываем клавиатуру при открытии Navigation Drawer
                InputMethodManager inputMethodManager = (InputMethodManager) ChartActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(ChartActivity.this.getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        });

        drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            // Обработка клика
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                if (drawerItem instanceof Nameable) {
                    Toast.makeText(ChartActivity.this, ChartActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                if (drawerItem instanceof Badgeable) {
                    Badgeable badgeable = (Badgeable) drawerItem;
                    int item = drawerItem.getIdentifier();
                    switch(item){
                        case 2:
                            //new Loader("pop.yandex.ru", "pop3", "Makbluming@yandex.ua", "0954023873").execute();
                            Intent intent = new Intent(ChartActivity.this, Autoresation.class);
                            startActivity(intent);
                            Log.d(TAG, "RegistrationProfileActivity");
                            break;

                        case 3:
                            Intent chartIntent = new Intent(ChartActivity.this, ChartActivity.class);
                            startActivity(chartIntent);
                            break;
                    }

//                    if (badgeable.getBadge() != null) {
//                        // учтите, не делайте так, если ваш бейдж содержит символ "+"
//                        try {
//                            int badge = Integer.valueOf(badgeable.getBadge());
//                            if (badge > 0) {
//                                drawerResult.updateBadge(String.valueOf(badge - 1), position);
//                            }
//                        } catch (Exception e) {
//                            Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
//                        }
//                    }
                }
            }
        });

        drawer.withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
            @Override
            // Обработка длинного клика, например, только для SecondaryDrawerItem
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                if (drawerItem instanceof SecondaryDrawerItem) {
                    Toast.makeText(ChartActivity.this, ChartActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        drawer.build();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                switch (position) {
                    case 0:
                        // Line MainActivity;
                        intent = new Intent(getBaseContext(), LineChartActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        // Column MainActivity;
                        intent = new Intent(getBaseContext(), ColumnChartActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        // Pie MainActivity;
                        intent = new Intent(getBaseContext(), PieChartActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        // Bubble MainActivity;
                        intent = new Intent(getBaseContext(), BubbleChartActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        // Preview Line MainActivity;
                        intent = new Intent(getBaseContext(), PreviewLineChartActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        // Preview Column MainActivity;
                        intent = new Intent(getBaseContext(), PreviewColumnChartActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        // Combo MainActivity;
                        intent = new Intent(getBaseContext(), ComboLineColumnChartActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });


    }

    private List<ChartSampleDescription> generateSamplesDescriptions() {
        List<ChartSampleDescription> list = new ArrayList<ChartSampleDescription>();

        list.add(new ChartSampleDescription("Линейный график", "", ChartType.LINE_CHART));
        list.add(new ChartSampleDescription("Диаграмма", "", ChartType.COLUMN_CHART));
        list.add(new ChartSampleDescription("Круговая диаграмма", "", ChartType.PIE_CHART));
        list.add(new ChartSampleDescription("Шариковая диаграмма", "", ChartType.BUBBLE_CHART));
        list.add(new ChartSampleDescription("Линейный график",
                "С дополнительным скроллером", ChartType.PREVIEW_LINE_CHART));
        list.add(new ChartSampleDescription("Диаграмма",
                "С дополнительным скроллером", ChartType.PREVIEW_COLUMN_CHART));
        list.add(new ChartSampleDescription("Диаграмма", "С линейным графиком",
                ChartType.OTHER));

        return list;
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent;
//
//        switch (position) {
//            case 0:
//                // Line MainActivity;
//                intent = new Intent(this, LineChartActivity.class);
//                startActivity(intent);
//                break;
//            case 1:
//                // Column MainActivity;
//                intent = new Intent(this, ColumnChartActivity.class);
//                startActivity(intent);
//                break;
//            case 2:
//                // Pie MainActivity;
//                intent = new Intent(this, PieChartActivity.class);
//                startActivity(intent);
//                break;
//            case 3:
//                // Bubble MainActivity;
//                intent = new Intent(this, BubbleChartActivity.class);
//                startActivity(intent);
//                break;
//            case 4:
//                // Preview Line MainActivity;
//                intent = new Intent(this, PreviewLineChartActivity.class);
//                startActivity(intent);
//                break;
//            case 5:
//                // Preview Column MainActivity;
//                intent = new Intent(this, PreviewColumnChartActivity.class);
//                startActivity(intent);
//                break;
//            case 6:
//                // Combo MainActivity;
//                intent = new Intent(this, ComboLineColumnChartActivity.class);
//                startActivity(intent);
//                break;
//            case 7:
//                // Line Column Dependency;
//                intent = new Intent(this, LineColumnDependencyActivity.class);
//                startActivity(intent);
//                break;
//            case 8:
//                // Tempo line chart;
//                intent = new Intent(this, TempoChartActivity.class);
//                startActivity(intent);
//                break;
//            case 9:
//                // Speed line chart;
//                intent = new Intent(this, SpeedChartActivity.class);
//                startActivity(intent);
//                break;
//            case 10:
//                // Good Bad filled line chart;
//                intent = new Intent(this, GoodBadChartActivity.class);
//                startActivity(intent);
//                break;
//            case 11:
//                // Good Bad filled line chart;
//                intent = new Intent(this, ViewPagerChartsActivity.class);
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
//
//
//    }

    public static class ChartSamplesAdapter extends ArrayAdapter<ChartSampleDescription> {

        public ChartSamplesAdapter(Context context, int resource, List<ChartSampleDescription> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.list_item_sample, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);
                holder.chartLayout = (FrameLayout) convertView.findViewById(R.id.chart_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChartSampleDescription item = getItem(position);

            holder.chartLayout.setVisibility(View.VISIBLE);
            holder.chartLayout.removeAllViews();
            AbstractChartView chart;
            switch (item.chartType) {
                case LINE_CHART:
                    chart = new LineChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case COLUMN_CHART:
                    chart = new ColumnChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PIE_CHART:
                    chart = new PieChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case BUBBLE_CHART:
                    chart = new BubbleChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PREVIEW_LINE_CHART:
                    chart = new PreviewLineChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                case PREVIEW_COLUMN_CHART:
                    chart = new PreviewColumnChartView(getContext());
                    holder.chartLayout.addView(chart);
                    break;
                default:
                    chart = null;
                    holder.chartLayout.setVisibility(View.GONE);
                    break;
            }

            if (null != chart) {
                chart.setInteractive(false);// Disable touch handling for chart on the ListView.
            }
            holder.text1.setText(item.text1);
            holder.text2.setText(item.text2);

            return convertView;
        }

        private class ViewHolder {

            TextView text1;
            TextView text2;
            FrameLayout chartLayout;
        }

    }

    public static class ChartSampleDescription {
        String text1;
        String text2;
        ChartType chartType;

        public ChartSampleDescription(String text1, String text2, ChartType chartType) {
            this.text1 = text1;
            this.text2 = text2;
            this.chartType = chartType;
        }
    }

    public enum ChartType {
        LINE_CHART, COLUMN_CHART, PIE_CHART, BUBBLE_CHART, PREVIEW_LINE_CHART, PREVIEW_COLUMN_CHART, OTHER
    }
}
