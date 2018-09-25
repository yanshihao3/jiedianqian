package com.day.money.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.day.money.activity.DetailsActivity;
import com.day.money.activity.MainActivity;
import com.day.money.entity.ItemType;
import com.day.money.entity.MultiItemBean;
import com.day.money.utils.SPUtil;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.day.money.R;
import com.day.money.activity.HtmlActivity;
import com.day.money.activity.LoginActivity;
import com.day.money.adapter.ProductAdapter;
import com.day.money.entity.ProductEntity;
import com.day.money.net.Api;
import com.day.money.net.ApiService;
import com.day.money.net.Contacts;
import com.day.money.net.OnRequestDataListener;
import com.day.money.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author yanshihao
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.fragment_home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private ProductAdapter mProductAdapter;

    private List<MultiItemBean> mBeanList;

    private MainActivity mMainActivity;

    private ProductEntity mProductEntity;

    private KProgressHUD mKProgressHUD;
    private boolean isFirst = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mKProgressHUD = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);
        initView();
        initData();

    }


    private void start(ProductEntity product) {
        mProductEntity = product;
        String token = SPUtil.getString(getActivity(), Contacts.TOKEN);
        ApiService.apply(product.getId(), token);
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", product.getUrl());
            intent.putExtra("title", product.getP_name());
            startActivity(intent);
        }
    }


    private void initView() {
        mBeanList = new ArrayList<>();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mProductAdapter = new ProductAdapter(null, mRecyclerView);

        RecyclerViewDivider.with(getContext()).color(ContextCompat.getColor(getContext(), R.color.divider_background)).size(2).build().addTo(mRecyclerView);

        mProductAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ProductEntity product = mProductAdapter.getData().get(position).getProductEntity();
                int itemType = mProductAdapter.getData().get(position).getItemType();
                switch (itemType) {
                    case ItemType.HEAD:

                        break;
                    case ItemType.MORE:

                        break;
                    case ItemType.PRODUCT:
                        start(product);
                        break;
                    default:
                        break;
                }
            }
        });

        mProductAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_home_borrow:
                        launcher();
                        break;
                    case R.id.iv_home_card:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 3).putExtra("title","身份证贷"));
                        break;
                    case R.id.iv_home_credit:
                        mMainActivity.setSelect(1);
                        break;
                    case R.id.iv_home_cash:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 1).putExtra("title","现金贷款"));
                        break;
                    case R.id.iv_home_micorfinance:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 3).putExtra("title","小额贷款"));
                        break;
                    case R.id.iv_home_second:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 3).putExtra("title","分期贷款"));
                        break;
                    case R.id.iv_home_sesame:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 3).putExtra("title","芝麻分贷"));
                        break;
                    case R.id.iv_home_new:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 2).putExtra("title", "新品专区"));
                        break;
                    case R.id.iv_home_money1:
                    case R.id.iv_home_money2:
                    case R.id.iv_home_money3:
                    case R.id.iv_home_money4:
                        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("type", 4).putExtra("title","金额专区"));
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initData() {
        if (isFirst) {
            mKProgressHUD.show();
            isFirst = false;
        }
        mBeanList.clear();
        ApiService.GET_SERVICE(Api.HOT, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);
                mBeanList.add(0, new MultiItemBean().setItemType(ItemType.HEAD));
                mBeanList.add(1, new MultiItemBean().setItemType(ItemType.MORE));

                for (ProductEntity p : entities) {
                    mBeanList.add(new MultiItemBean().setItemType(ItemType.PRODUCT).setProductEntity(p));
                }
                mProductAdapter.setNewData(mBeanList);
                mRefreshLayout.finishRefresh();
                if (mKProgressHUD.isShowing()) {
                    mKProgressHUD.dismiss();
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                mRefreshLayout.finishRefresh();
                ToastUtils.showToast(msg);
                if (mKProgressHUD.isShowing()) {
                    mKProgressHUD.dismiss();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 1) {
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", mProductEntity.getUrl());
            intent.putExtra("title", mProductEntity.getP_name());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    public void launcher() {
        ApiService.GET_SERVICE(Api.ANWEI, null, new OnRequestDataListener() {

            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONObject object = json.getJSONObject("data");
                String url = object.getString("url");
                String name = object.getString("name");
                startActivity(new Intent(getContext(), HtmlActivity.class).putExtra("html", url)
                        .putExtra("title", "帮你借"));
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showToast(msg);
            }

        });
    }
}