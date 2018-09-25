package com.day.money.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.day.money.R;
import com.day.money.activity.HtmlActivity;
import com.day.money.activity.LoginActivity;
import com.day.money.adapter.LoanAdapter;
import com.day.money.entity.ProductEntity;
import com.day.money.net.Api;
import com.day.money.net.ApiService;
import com.day.money.net.Contacts;
import com.day.money.net.OnRequestDataListener;
import com.day.money.utils.SPUtil;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author yanshihao
 */
public class LoanFragment extends Fragment {

    @BindView(R.id.fragment_home_rv)
    RecyclerView mFragmentHomeRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private LoanAdapter mBankAdapter;

    private ProductEntity mBankEntity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void initData() {

        ApiService.GET_SERVICE(Api.PRODUCT_LSIT, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);
                mBankAdapter.setNewData(entities);

                if (mRefreshLayout != null) {
                    mRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                if (mRefreshLayout != null) {
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    private void initView() {
        mToolbar.setVisibility(View.VISIBLE);
        mTitle.setText("贷款大全");
        mBankAdapter = new LoanAdapter(null);
        mFragmentHomeRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBankAdapter.bindToRecyclerView(mFragmentHomeRv);
        RecyclerViewDivider.with(getContext()).color(ContextCompat.getColor(getContext(), R.color.divider_background)).size(10).build().addTo(mFragmentHomeRv);

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        mBankAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mBankEntity = mBankAdapter.getData().get(position);
                String token = SPUtil.getString(getActivity(), Contacts.TOKEN);
                if (TextUtils.isEmpty(token)) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    Intent intent = new Intent(getContext(), HtmlActivity.class);
                    intent.putExtra("html", mBankEntity.getUrl());
                    intent.putExtra("title", mBankEntity.getP_name());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 2) {
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", mBankEntity.getUrl());
            intent.putExtra("title", mBankEntity.getP_name());
            startActivity(intent);
        }
    }

}