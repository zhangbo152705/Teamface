package kankan.wheel.widget.regionselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.mrwujay.cascade.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kankan.wheel.widget.adapters.AreaListAdapter;
import kankan.wheel.widget.regionselect.model.WheelModel;
import kankan.wheel.widget.regionselect.service.XmlAreaParserHandler;


/**
 * @author Administrator
 */
public class ProvinceSelectActivity2 extends Activity {

    public static final int SUCESS = 100;
    private int areaType = 0;
    private String district = "";

    public static final String RESULT_KEY = "result_key";

    public static final String DEFAULT_SELECT_KEY = "default_select_key";
    public static final String DEFAULT_SELECT_TYPE = "default_select_type";


    private boolean init = true;
    private boolean isMulti = false;

    private XmlAreaParserHandler handler;
    private AreaListAdapter mAreaListAdapter;
    private List<WheelModel> originDatas = new ArrayList<>();
    private List<WheelModel> areaDatas = new ArrayList<>();
    private List<WheelModel> searchDatas = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private View searchLayout1;
    private View searchLayout2;
    private EditText et;
    private View clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_select_lay);
        setUpViews();
        setUpListener();
        String id = getIntent().getStringExtra(DEFAULT_SELECT_KEY);
        areaType = getIntent().getIntExtra(DEFAULT_SELECT_TYPE, 0);
        isMulti = getIntent().getBooleanExtra("type", false);
        district = getIntent().getStringExtra("data");
        setUpData(id);
    }

    private void setUpViews() {
        searchLayout1 = findViewById(R.id.rl_fake);
        searchLayout2 = findViewById(R.id.rl_search);
        et = findViewById(R.id.et);
        clean = findViewById(R.id.iv_clear);
        mRecyclerView = findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAreaListAdapter = new AreaListAdapter(areaDatas);
        mRecyclerView.setAdapter(mAreaListAdapter);
    }

    private void setUpListener() {
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (isMulti) {
                    mAreaListAdapter.getData().get(position).setCheck(!mAreaListAdapter.getData().get(position).isCheck());
                } else {
                    List<WheelModel> data = mAreaListAdapter.getData();
                    final boolean check = data.get(position).isCheck();
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setCheck(false);
                    }
                    data.get(position).setCheck(!check);
                }
                mAreaListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isMulti) {
                    mAreaListAdapter.getData().get(position).setCheck(!mAreaListAdapter.getData().get(position).isCheck());
                } else {
                    List<WheelModel> data = mAreaListAdapter.getData();
                    final boolean check = data.get(position).isCheck();
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setCheck(false);
                    }
                    data.get(position).setCheck(!check);
                }
                mAreaListAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<WheelModel> data = mAreaListAdapter.getData();
                StringBuilder stringBuilder1 = new StringBuilder();
                StringBuilder stringBuilder2 = new StringBuilder();
                for (int i = 0; i < data.size(); i++) {
                    final WheelModel wheelModel = data.get(i);
                    if (wheelModel.isCheck()) {
                        stringBuilder1.append(data.get(i).getId() + ":" + data.get(i).getName() + ",");
                        stringBuilder2.append(data.get(i).getName() + ",");
                    }
                }
                String str1 = "";
                String str2 = "";
                if (!TextUtils.isEmpty(stringBuilder1)) {
                    str1 = stringBuilder1.subSequence(0, stringBuilder1.length() - 2).toString();
                }
                if (!TextUtils.isEmpty(stringBuilder1)) {
                    str2 = stringBuilder2.subSequence(0, stringBuilder2.length() - 2).toString();
                }
                Intent intent = new Intent();
                intent.putExtra("data1", str1);
                intent.putExtra("data2", str2);
                setResult(ProvinceSelectActivity.SUCESS, intent);
                finish();
            }
        });
        searchLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout1.setVisibility(View.GONE);
                searchLayout2.setVisibility(View.VISIBLE);
                et.requestFocus();
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
                searchData("");
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchData(s.toString());
            }
        });

    }

    private void searchData(String s) {
        if (TextUtils.isEmpty(s)) {
            clean.setVisibility(View.GONE);
            final List<WheelModel> data = mAreaListAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setShow(false);
            }
            mAreaListAdapter.setSearchState(false);
            mAreaListAdapter.notifyDataSetChanged();
        } else {
            clean.setVisibility(View.VISIBLE);
            mAreaListAdapter.setSearchState(true);
            final List<WheelModel> data = mAreaListAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getName() != null && data.get(i).getName().contains(s)) {
                    data.get(i).setShow(true);
                } else {
                    data.get(i).setShow(false);
                }

            }
            mAreaListAdapter.notifyDataSetChanged();
        }

    }

    private void setUpData(String id) {

        handler = XmlUtils.getInstance().parseAreaData(this, id);
        switch (areaType) {
            case 2:
                prepareProvinceData();
                break;
            case 3:
                prepareCitysData();
                break;
            case 4:
                prepareDistrictData();
                break;
        }
        if (!TextUtils.isEmpty(district)) {
            final String[] split = district.split(",");
            final List<String> strings = Arrays.asList(split);
            for (int i = 0; i < strings.size(); i++) {
                if (!TextUtils.isEmpty(strings.get(i))) {
                    final String[] split1 = strings.get(i).split(":");
                    final String s = split1[0];
                    if (!TextUtils.isEmpty(s)) {
                        for (int j = 0; j < areaDatas.size(); j++) {
                            if (s.equals(areaDatas.get(j).getId())) {
                                areaDatas.get(j).setCheck(true);
                            }
                        }
                    }
                }


            }
        }

        init = false;
        mAreaListAdapter.notifyDataSetChanged();

    }

    /**
     * 准备数据
     *
     * @param
     */
    protected void prepareProvinceData() {

        // 省的数据
        originDatas.clear();
        areaDatas.clear();
        originDatas.addAll(XmlUtils.getInstance().getProvinceData(this, ""));
        areaDatas.addAll(XmlUtils.getInstance().getProvinceData(this, ""));


    }


    /**
     * 当前选中的所有城市下面的所有地区
     */
    private void prepareDistrictData() {
        originDatas.clear();
        areaDatas.clear();
        originDatas.addAll(XmlUtils.getInstance().getDistrictData(this, ""));
        areaDatas.addAll(XmlUtils.getInstance().getDistrictData(this, ""));
    }

    /**
     * 当前选中的省市下面的所有城市
     */
    private void prepareCitysData() {
        originDatas.clear();
        areaDatas.clear();
        originDatas.addAll(XmlUtils.getInstance().getCityData(this, ""));
        areaDatas.addAll(XmlUtils.getInstance().getCityData(this, ""));

    }


    // 重写finish（）方法，加入关闭时的动画
    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, R.anim.dialog_exit);
    }
}
