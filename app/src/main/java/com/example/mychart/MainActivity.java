package com.example.mychart;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private View view1;
    private View view2;
    private View view3;
    private List<String> mTitleList = new ArrayList<>(); //页卡标题集合
    private List<View> mViewList = new ArrayList<>();//页卡视图集合


    private float[] money = {640.27f,779.41f,4327.20f,588f};
    private String[] yue = {"一月","二月","三月","四月"};



    //线性图
    private int[] temperature = {25, 45, 78, 56, 12, 56, 43};//图标数据点
    //x轴的坐标
    private String[] lineData = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private List<PointValue> pointValues = new ArrayList<>();
    private List<AxisValue> axisValues = new ArrayList<>();
    private LineChartView lineChartView;

    //饼状图
    private PieChartView pieChartView;
    private PieChartData pieChartData;
    private List<SliceValue> sliceValues = new ArrayList<>();
    private int[] pieData = {8,24,34,10};
    private int[] color = {Color.parseColor("#ff00ff"),Color.parseColor("#ff0000"),Color.parseColor("#0000ff"),Color.parseColor("#ff00ff")};
    private String[] stateChar = {"高等教育","职业教育","语言配型","ki教育","其他"};

    //柱状图
    private String[] year = new String[]{"2013","2014","2015","2016","2017"}; //柱状图x轴 字
    private ColumnChartView columnChartView;
    private ColumnChartData columnChartData;
    private int[] columnY = {500,1000,1500,2000,2500,3000}; //柱状图y轴 字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mTabLayout = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.vp_view);
        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(R.layout.layout_line_chart, null);
        view2 = mInflater.inflate(R.layout.layout_pie_chart,null);
        view3 = mInflater.inflate(R.layout.layout_column_chart,null);


        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);


        mTitleList.add("线性图");
        mTitleList.add("饼状图");
        mTitleList.add("柱状图");

        //设置Tab模式，当前为系统默认模式

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        //添加选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(viewPagerAdapter);    //给viewpager设置适配器
        mTabLayout.setTabIndicatorFullWidth(false);
        mTabLayout.setupWithViewPager(mViewPager);  //给tabLayout设置关联

        //线形图
        lineChartView = view1.findViewById(R.id.lv_chart);
        setAxisXLables();   //获取x轴的标注
        setAxisPoints();    //设置坐标点
        initLineChar();     //初始化线形图

        //饼状图
        pieChartView = view2.findViewById(R.id.pv_chart);
        pieChartView.setOnValueTouchListener(selectListener);
        setPieChartData();
        initPieChart();


        //柱状图
        columnChartView = view3.findViewById(R.id.cv_chart);
        initColumnChart();


    }

    /**
     * 初始化线形图
     */
    private void initLineChar() {
        //设置线的颜色、形状等属性
        Line line = new Line();
        line.setColor(Color.parseColor("#33b5e5"));
        line.setShape(ValueShape.CIRCLE);//线形图上的数据点的形状为圆形
        line.setCubic(false);//曲线是否平滑，即曲线还是折线
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否显示线条，如果false则没有曲线只显示点
        line.setHasPoints(true);//是否显示圆点，如果false则没有圆点只显示线条

        line.setValues(pointValues);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //x轴
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true); //x轴字体是倾斜还是正体，true是倾斜
        axisX.setTextColor(Color.parseColor("#00ff00"));//设置字体颜色
        axisX.setMaxLabelChars(5);//设置坐标轴标签显示的最大字符数
        axisX.setValues(axisValues);//填充x轴的坐标名称
        data.setAxisXBottom(axisX);//设置x轴在底部
        axisX.setHasLines(true); //x轴分割线

        //y轴
        Axis axisY = new Axis();
        data.setAxisXBottom(axisY); //设置y轴在左侧
        axisY.setTextColor(Color.parseColor("#00ff00"));//设置字体颜色
        axisY.setMaxLabelChars(5);//设置坐标标签显示的最大字符数

        //设置线形图的行为属性，如支持缩放、滑动、以及平移
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL); //设置缩放类型为水平缩放
        lineChartView.setMaxZoom(2);//最大缩放比例
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setLineChartData(data);
        lineChartView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置线形图中的每个数据点
     */
    private void setAxisPoints() {
        for (int i = 0; i < temperature.length; i++) {
            pointValues.add(new PointValue(i, temperature[i]));
        }
    }

    /**
     * 设置x轴的标注
     */
    private void setAxisXLables() {
        for (int i = 0; i < lineData.length; i++) {
            axisValues.add(new AxisValue(i).setLabel(lineData[i]));
        }
    }

    /**
     * 设置饼状图中的数据
     */
    private void setPieChartData(){
        for (int i=0;i<pieData.length;i++){
            SliceValue sliceValue = new SliceValue(pieData[i],color[i]);
            sliceValues.add(sliceValue);
        }
    }

    /**
     * 初始化饼状图
     */
    private void initPieChart(){
        pieChartData = new PieChartData();
        pieChartData.setHasLabels(true);//显示标题
        pieChartData.setHasLabelsOnlyForSelected(false);//不用点击显示占得百分比
        pieChartData.setHasLabelsOutside(false);//数据是否显示在饼状图外侧
        pieChartData.setValues(sliceValues);//填充数据
        pieChartData.setHasCenterCircle(true);//是否显示中心圆
        pieChartData.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        pieChartData.setCenterCircleScale(0.5f);//设置中心圆所占饼状图的比例
        pieChartData.setCenterText1("数据");//设置中心园默认显示的文字
       // pieChartData.setCenterText1FontSize(90);
        pieChartView.setPieChartData(pieChartData);//为饼图设置数据
        pieChartView.setValueSelectionEnabled(true);//选中饼状图中的块会变大
        pieChartView.setAlpha(0.9f);//设置透明度
        pieChartView.setCircleFillRatio(1f);//设置饼图占整个视图的比例


    }

    /**
     * 数据占百分比
     * @param i
     * @return
     */
    private String calPercent(int i){
        String result="";
        int sum = 0;
        for (int j = 0;j<pieData.length;j++){
            sum += pieData[j];
        }
        result = String.format("%.2f",(float)pieData[i] * 100/sum) + "%";
        return result;

    }

    PieChartOnValueSelectListener selectListener = new PieChartOnValueSelectListener() {
        @Override
        public void onValueSelected(int i, SliceValue sliceValue) {
            //选择对应图形后 在中间部分显示相应的信息
            pieChartData.setCenterText1(stateChar[i]);
            pieChartData.setCenterText2(sliceValue.getValue() + "(" + calPercent(i) + ")");
        }

        @Override
        public void onValueDeselected() {

        }
    };


    /**
     * 初始化柱状图
     */
    private void initColumnChart(){
        List<AxisValue> axesValues = new ArrayList<>();
        List<AxisValue> axesYValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> subcolumnValues;

        for (int k=0;k<columnY.length;k++){
            axesYValues.add(new AxisValue(k).setValue(columnY[k]));
        }

        for (int i =0;i<yue.length;i++){
            subcolumnValues = new ArrayList<>();
//            for (int j=0;j<1;j++){
//                switch (i+1){
//                    case 1:
//                        subcolumnValues.add(new SubcolumnValue(money[i], ChartUtils.COLOR_BLUE));
//                        break;
//                    case 2:
//                        subcolumnValues.add(new SubcolumnValue(money[i],ChartUtils.COLOR_GREEN));
//                        break;
//                    case 3:
//                        subcolumnValues.add(new SubcolumnValue(money[i],ChartUtils.COLOR_RED));
//                        break;
//                    case 4:
//                        subcolumnValues.add(new SubcolumnValue(money[i],ChartUtils.COLOR_ORANGE));
//                        break;
//
//
//                }
            subcolumnValues.add(new SubcolumnValue(money[i], color[i]));
//                subcolumnValues.add(new SubcolumnValue(money[i], ChartUtils.COLOR_BLUE));
//                subcolumnValues.add(new SubcolumnValue(money[i], ChartUtils.COLOR_BLUE));
//                subcolumnValues.add(new SubcolumnValue(money[i], ChartUtils.COLOR_BLUE));

//            }
            //点击柱状图就可展示数据量
            axesValues.add(new AxisValue(i).setLabel(yue[i]));
            columns.add(new Column(subcolumnValues).setHasLabelsOnlyForSelected(true));

        }

        //x轴
        Axis axisX = new Axis(axesValues);
        axisX.setHasLines(false);
        axisX.setTextColor(Color.BLACK);

        //Y轴
        Axis axisY = new Axis(axesYValues);
        axisY.setHasLines(true); //设置显示线条
        axisY.setTextColor(Color.BLACK);
        axisY.setMaxLabelChars(5);

        columnChartData = new ColumnChartData(columns);
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);
        columnChartView.setColumnChartData(columnChartData);
        columnChartView.setValueSelectionEnabled(true);
        columnChartView.setZoomType(ZoomType.HORIZONTAL);

    }






}
