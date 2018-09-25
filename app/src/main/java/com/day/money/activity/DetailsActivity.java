package com.day.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.day.money.R;
import com.day.money.adapter.LoanAdapter;
import com.day.money.entity.ProductEntity;
import com.day.money.net.Api;
import com.day.money.net.ApiService;
import com.day.money.net.Contacts;
import com.day.money.net.OnRequestDataListener;
import com.day.money.utils.SPUtil;
import com.day.money.utils.StatusBarUtil;
import com.day.money.utils.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yanshihao
 */
public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.recylerview)
    RecyclerView mRecylerview;

    @BindView(R.id.title)
    TextView mTextView;

    private ProductEntity mProductEntity;

    private LoanAdapter mAdapter;

    private KProgressHUD mKProgressHUD;

    private int mInt;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.theme_color), 40);
        ButterKnife.bind(this);
        mKProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(ContextCompat.getColor(this, R.color.monsoon))
                .setDimAmount(0.1f);
        Intent intent = getIntent();
        mInt = intent.getIntExtra("type", 0);
        title = intent.getStringExtra("title");
        mTextView.setText(title);
        initView();
        switch (mInt) {
            case 0:
                ininData(Api.PRODUCT_LSIT);
                break;
            case 1:
                ininData(Api.PRODUCT_LSIT);
                break;
            case 2:
                ininData(Api.HOT);
                break;
            case 3:
                ininData(Api.LIKE);
                break;
            case 4:
                ininData(Api.HOT);
                break;
            case 5:
                ininData(Api.RECOMMEND);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mRecylerview.setLayoutManager(new LinearLayoutManager(this));

        mRecylerview.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(this)
                .colorResId(R.color.divider_background)
                .size(20)
                .build()
        );
        mAdapter = new LoanAdapter(null);
        mAdapter.bindToRecyclerView(mRecylerview);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mProductEntity = mAdapter.getData().get(position);
                start(mProductEntity);
            }
        });

    }


    private void ininData(String api) {
        mKProgressHUD.show();
        ApiService.GET_SERVICE(api, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);

                mKProgressHUD.dismiss();

                mAdapter.setNewData(entities);
            }

            @Override
            public void requestFailure(int code, String msg) {
                mAdapter.setEmptyView(R.layout.empty);
                ToastUtils.showToast(msg);
                mKProgressHUD.dismiss();

            }
        });

    }

    private void start(ProductEntity product) {
        String token = SPUtil.getString(this, Contacts.TOKEN);
        ApiService.apply(product.getId(), token);
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this, HtmlActivity.class);
            intent.putExtra("html", product.getUrl());
            intent.putExtra("title", product.getP_name());
            startActivity(intent);
        }
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 1) {
            Intent intent = new Intent(this, HtmlActivity.class);
            intent.putExtra("html", mProductEntity.getUrl());
            intent.putExtra("title", mProductEntity.getP_name());
            startActivity(intent);
        }
    }
}
