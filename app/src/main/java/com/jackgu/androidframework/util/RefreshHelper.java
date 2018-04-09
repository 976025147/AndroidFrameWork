package com.jackgu.androidframework.util;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jackgu.androidframework.R;
import com.jackgu.androidframework.view.WrapContentGridLayoutManager;
import com.jackgu.androidframework.view.WrapContentLinearLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 刷新的帮助类，需要传入SmartRefreshLayout和RecyclerView，设置总的数量，自动判断有没有更多
 * <p>
 * 建议：
 * </p>
 * <p>
 * 1. 将有无更多等逻辑交由本类处理，页面不需要次有实体数组，不需要持有adapter，全部由本类保管
 * </p>
 * <p>
 * 2. 设置total(setTotal方法)，保证加载更多逻辑的正确使用
 * <p>
 * 3. 建议分页的时候，通过本类获取当前的page(page的开始下标，默认是0，可设置PAGE_START来修改)，降低RecyclerView与activity的耦合度
 * </p>
 * <p>
 * 4. 建议使用RefreshHelper的setOnRefreshAndLoadMoreListener来设置监听，而不是直接设置SmartRefreshLayout
 * 的监听，因为这里面已经做了page的处理
 * </p>
 * <p>
 * 5. RecyclerView不用设置manger，本类会设置，两个构造方法，传入了number的为grid，但是间距需要自己设置
 * </p>
 * <p>
 * 6. 传入BaseQuickAdapter的实例的时候，只需要构造传入layoutRes就可以了
 * </p>
 * <p>
 * <p>
 * 其他（个人习惯）：
 * </p>
 * <p>
 * 设置监听事件和创建本类的实例可使用动态模板（前提是自己加了的）来进行，名字分别为：setOnRefreshAndLoadMoreListener和refreshHelper
 * </p>
 *
 * @Author: JACK-GU
 * @Date: 2018/3/30 10:03
 * @E-Mail: 528489389@qq.com
 */
public class RefreshHelper<T> {
    private static final int PAGE_START = 0;//第一页的下标
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private BaseQuickAdapter baseQuickAdapter;
    private List<T> dataList;
    private int total = 0; //总共的条数，用来判断是不是需要加载更多还是加载完成
    private OnRefreshAndLoadMoreListener onRefreshAndLoadMoreListener;
    private int page = PAGE_START;//页数

    /**
     * 获取adapter
     *
     * @Author: JACK-GU
     * @Date: 2018/4/9 15:26
     * @E-Mail: 528489389@qq.com
     */
    public BaseQuickAdapter getBaseQuickAdapter() {
        return baseQuickAdapter;
    }

    /**
     * 通过该方法自动获取当前的页数，前提是onRefreshAndLoadMoreListener这里面调用
     */
    public int getPage() {
        return page;
    }

    public OnRefreshAndLoadMoreListener getOnRefreshAndLoadMoreListener() {
        return onRefreshAndLoadMoreListener;
    }

    /**
     * 设置监听
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 14:01
     * @E-Mail: 528489389@qq.com
     */
    public void setOnRefreshAndLoadMoreListener(OnRefreshAndLoadMoreListener
                                                        onRefreshAndLoadMoreListener) {
        this.onRefreshAndLoadMoreListener = onRefreshAndLoadMoreListener;
    }

    public int getTotal() {
        return total;
    }

    /**
     * 设置总的数量
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * list类型的
     *
     * @param baseQuickAdapter   适配器
     * @param recyclerView       列表
     * @param context            上下文
     * @param smartRefreshLayout 刷新组件
     * @Author: JACK-GU
     * @Date: 2018/3/30 10:42
     * @E-Mail: 528489389@qq.com
     */
    public RefreshHelper(SmartRefreshLayout smartRefreshLayout, RecyclerView recyclerView,
                         BaseQuickAdapter baseQuickAdapter, Context context) {
        this.smartRefreshLayout = smartRefreshLayout;
        this.recyclerView = recyclerView;
        this.baseQuickAdapter = baseQuickAdapter;

        this.dataList = new ArrayList<>();
        this.baseQuickAdapter.setNewData(this.dataList);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        baseQuickAdapter.bindToRecyclerView(recyclerView);
        baseQuickAdapter.setEmptyView(R.layout.layout_empty);

        //设置不开启自动加载多多
        smartRefreshLayout.setEnableAutoLoadMore(false);
        //默认不开启加载更多，只有当刷新后手动开启
        smartRefreshLayout.setEnableLoadMore(false);
        //关闭不满一页关闭上拉
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);


        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            //刷新，变为0;
            this.page = PAGE_START;

            if (onRefreshAndLoadMoreListener != null) {
                onRefreshAndLoadMoreListener.onRefresh(refreshLayout);
            }
        });

        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            //否则++
            this.page++;

            if (onRefreshAndLoadMoreListener != null) {
                onRefreshAndLoadMoreListener.onLoadMore(refreshLayout);
            }
        });
    }


    /**
     * grid类型的
     *
     * @param baseQuickAdapter   适配器
     * @param recyclerView       列表
     * @param smartRefreshLayout 刷新组件
     * @param number             一行的个数
     * @param context            上下文
     * @Author: JACK-GU
     * @Date: 2018/3/30 10:42
     * @E-Mail: 528489389@qq.com
     */
    public RefreshHelper(SmartRefreshLayout smartRefreshLayout, RecyclerView recyclerView,
                         BaseQuickAdapter baseQuickAdapter, Context context, int number) {
        this.smartRefreshLayout = smartRefreshLayout;
        this.recyclerView = recyclerView;
        this.baseQuickAdapter = baseQuickAdapter;

        this.dataList = new ArrayList<>();
        this.baseQuickAdapter.setNewData(this.dataList);


        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(context, number,
                GridLayoutManager.VERTICAL, false));

        baseQuickAdapter.bindToRecyclerView(recyclerView);
        baseQuickAdapter.setEmptyView(R.layout.layout_empty);

        //设置不开启自动加载多多
        smartRefreshLayout.setEnableAutoLoadMore(false);
        //默认不开启加载更多，只有当刷新后手动开启
        smartRefreshLayout.setEnableLoadMore(false);
    }

    /**
     * 添加一条数据
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 10:27
     * @E-Mail: 528489389@qq.com
     */
    public void add(T t) {
        baseQuickAdapter.addData(t);

        //加入新的数据，判断一下是不是满了
        haveLoad();
    }

    /**
     * 添加多条数据
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 10:27
     * @E-Mail: 528489389@qq.com
     */
    public void add(List<T> dataS) {
        baseQuickAdapter.addData(dataS);

        //加入新的数据，判断一下是不是满了
        haveLoad();
    }

    /**
     * 适合刷新的时候调用,不会修改Adapter的list引用
     *
     * @param dataS 新的数据
     * @Author: JACK-GU
     * @Date: 2018/3/30 11:19
     * @E-Mail: 528489389@qq.com
     */
    public void reSet(List<T> dataS) {
        baseQuickAdapter.replaceData(dataS);

        //加入新的数据，判断一下是不是满了
        haveLoad();
    }


    /**
     * 移除一个数据
     *
     * @param index 下标
     * @Author: JACK-GU
     * @Date: 2018/3/30 10:30
     * @E-Mail: 528489389@qq.com
     */
    public void removeData(int index) {
        baseQuickAdapter.remove(index);

        //加入新的数据，判断一下是不是满了
        haveLoad();
    }

    /**
     * 没有更多数据了
     *
     * @param loadAll 是否已经加载全部数据
     * @Author: JACK-GU
     * @Date: 2018/3/30 11:04
     * @E-Mail: 528489389@qq.com
     */
    public void loadAll(boolean loadAll) {
        smartRefreshLayout.setNoMoreData(loadAll);
    }


    /**
     * 判断是不是还有数据
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 11:13
     * @E-Mail: 528489389@qq.com
     */
    public void haveLoad() {
        //开启上拉
        smartRefreshLayout.setEnableLoadMore(true);

        loadAll(total <= this.dataList.size());
    }

    /**
     * 完成就刷新
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 11:42
     * @E-Mail: 528489389@qq.com
     */
    public void finishRefresh() {
        smartRefreshLayout.finishRefresh();
    }


    /**
     * 完成加载更多
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 11:42
     * @E-Mail: 528489389@qq.com
     */
    public void finishLoadMore() {
        smartRefreshLayout.finishLoadMore();
    }


    /**
     * 自动的调用下拉的东西
     *
     * @Author: JACK-GU
     * @Date: 2018/3/30 14:12
     * @E-Mail: 528489389@qq.com
     */
    public void autoRefresh() {
        smartRefreshLayout.autoRefresh();
    }

    public interface OnRefreshAndLoadMoreListener {
        void onRefresh(RefreshLayout refreshLayout);

        void onLoadMore(RefreshLayout refreshLayout);
    }

}
