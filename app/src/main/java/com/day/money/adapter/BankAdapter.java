package com.day.money.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.day.money.R;
import com.day.money.entity.BankEntity;

import java.util.List;
import java.util.Random;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午6:10
 * - @Email whynightcode@gmail.com
 */
public class BankAdapter extends BaseQuickAdapter<BankEntity, BaseViewHolder> {

    private boolean isFirst = true;

    public BankAdapter(@Nullable List<BankEntity> data) {
        super(R.layout.bank_list, data);
        //openLoadAnimation();
        //多次执行动画
    }


    @Override
    protected void convert(BaseViewHolder helper, BankEntity item) {
        ImageView view = helper.getView(R.id.bank_logo);


        Glide.with(mContext)
                .load(item.getPicture())
                .apply(new RequestOptions())
                .into((ImageView) helper.getView(R.id.bank_logo));

/*
        int width = view.getWidth();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = width / 5 * 3;

        view.setLayoutParams(layoutParams);*/


        int min = 1000;
        int max = 9999;
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;

        helper.setText(R.id.bank_title, item.getName())
                .setText(R.id.bank_content, item.getDesc());
        String string = "已有" + num + "人申请";
        SpannableString spannableString = new SpannableString(string);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#F2555F"));
        spannableString.setSpan(colorSpan, 2, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        helper.setText(R.id.bank_person, spannableString);
    }


}
