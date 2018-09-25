package com.day.money.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.day.money.R;
import com.day.money.entity.ItemType;
import com.day.money.entity.MultiItemBean;

import java.util.List;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午6:10
 * - @Email whynightcode@gmail.com
 */
public class ProductAdapter extends BaseMultiItemQuickAdapter<MultiItemBean, BaseViewHolder>
        implements BaseQuickAdapter.SpanSizeLookup {
    private final RequestOptions mRequestOptions =
            new RequestOptions()
                    .centerCrop()
                    .transform(new GlideRoundTransform(5))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    public ProductAdapter(@Nullable List<MultiItemBean> data, RecyclerView recyclerView) {
        super(data);
        // openLoadAnimation();
        bindToRecyclerView(recyclerView);
        //多次执行动画
        isFirstOnly(false);
        addItemType(ItemType.HEAD, R.layout.item_header);
        addItemType(ItemType.PRODUCT, R.layout.item_product);
        addItemType(ItemType.MORE, R.layout.item_more);
        setSpanSizeLookup(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemBean item) {
        switch (item.getItemType()) {
            case ItemType.HEAD:
                helper.addOnClickListener(R.id.iv_home_borrow)
                        .addOnClickListener(R.id.iv_home_card)
                        .addOnClickListener(R.id.iv_home_cash)
                        .addOnClickListener(R.id.iv_home_second)
                        .addOnClickListener(R.id.iv_home_sesame)
                        .addOnClickListener(R.id.iv_home_credit)
                        .addOnClickListener(R.id.iv_home_micorfinance)
                        .addOnClickListener(R.id.iv_home_new)
                        .addOnClickListener(R.id.iv_home_money1)
                        .addOnClickListener(R.id.iv_home_money2)
                        .addOnClickListener(R.id.iv_home_money3)
                        .addOnClickListener(R.id.iv_home_money4);

                break;
            case ItemType.PRODUCT:

                helper.setText(R.id.item_title, item.getProductEntity().getP_name())
                        .setText(R.id.item_person, item.getProductEntity().getApply());

                Glide.with(mContext)
                        .load(item.getProductEntity().getP_logo())
                        .apply(mRequestOptions)
                        .into((ImageView) helper.getView(R.id.item_logo));
                break;
            default:
                break;
        }
    }

    @Override
    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
        int itemType = getData().get(position).getItemType();
        return itemType == ItemType.PRODUCT ? 1 : 2;
    }
}
