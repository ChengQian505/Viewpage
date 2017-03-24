package cq.my.viewpage;

import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    // 图片都存放在这里
    private List<ImageView> imageViewlist;
    // 存放小点的布局文件
    private LinearLayout layoutPGroup;
    private String[] imageDescription = { "淮左名都，竹西佳处，解鞍少驻初程。", "过春风十里。尽荠麦青青。",
            "自胡马窥江去后，废池乔木，犹厌言兵。"};
    // 线程开关，当activity销毁后，线程也应该停止运行
    private boolean isStop = false;
    private ImageView iv;
    private int previousPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置图片自动滚动
        new Thread(new Runnable() {

            @Override
            public void run() {
                //如果activity未销毁则一直执行
                while (!isStop) {
                    //先休息5秒钟
                    SystemClock.sleep(3000);
                    //以下代码发送到主线程中执行
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager
                                    .getCurrentItem() + 1);
                        }
                    });
                }
            }
        }).start();
        //设置标题栏--end
        mViewPager = (ViewPager) this.findViewById(R.id.viewpager);
        imageViewlist = new ArrayList<ImageView>();
        layoutPGroup = (LinearLayout) this.findViewById(R.id.show_pointer);
        // 先拿到图片id
        int[] ivIDs = new int[] { R.drawable.jpg1, R.drawable.jpg2, R.drawable.jpg3
        };
        // 遍历图片id
        for (int id : ivIDs) {
            // 构造新的图片对象，并根据id给图片设置背景
            iv = new ImageView(this);
            iv.setBackgroundResource(id);
            // 所有的图片存放在imageViewlist中
            imageViewlist.add(iv);

            // 构造小点
            View v = new View(this);
            // 设置小点的宽和高
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            // 设置小点的左边距
            params.leftMargin=12;
            v.setLayoutParams(params);
            // 设置小点是否可用，默认都不可用，当不可用时，小点是透明的，否则是白色的
            v.setEnabled(false);
            // 设置小点的背景，这个背景是使用xml文件画的一个小圆点
            v.setBackgroundResource(R.drawable.pointer_selector);
            // 把小点添加到它的布局文件中
            layoutPGroup.addView(v);
        }
        // 计算应用打开时显示的第一项 Integer.MAX_VALUE /2 - 3=0
        int index = Integer.MAX_VALUE / 2 - 3;
        // 给mViewPager设置数据
        mViewPager.setAdapter(new MyPagerAdapter());
        // 给mViewPager设置页面滑动事件
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        // 设置应用打开时显示的第一项，index的值为0
        // 使用这种方式得到的0，和直接写0有什么区别呢？
        // 直接写0，应用打开后不能直接向右滑动，因为viewpager中存image位置不能为负值，只能先向左滑动
        // 这种方式得到的0，可以实现应用一打开，就可以向右滑动
        mViewPager.setCurrentItem(index);
    }
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        // 开始
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        // 正在进行时
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 结束
        @Override
        public void onPageSelected(int position) {
            // 当页面滑动结束时，先对页面位置取模
            position = position % imageViewlist.size();
            // 将上一个点的可用性设置为false
            layoutPGroup.getChildAt(previousPoint).setEnabled(false);
            // 把当前点的可用性设置为true
            layoutPGroup.getChildAt(position).setEnabled(true);
            // 把当前位置值赋值给previousPoint
            previousPoint = position;
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        /**
         * 返回图片总数，Integer.MAX_VALUE的值为 2147483647这个数有21亿，也就是说我们的viewpager
         * 理论上在每次使用应用的时候可以滑动21亿次,当然，实际上是没人要去这么做的，我们这样做是为了实现实现viewpager循环滑动的效果
         * 即当滑动到viewpager的最后一页时，继续滑动就可以回到第一页
         *
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 当某一页滑出去的时候，将其销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        // 向容器中添加图片，由于我们要实现循环滑动的效果，所以要对position取模
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.removeView(imageViewlist.get(position%imageViewlist.size()));
            container.addView(imageViewlist.get(position%imageViewlist.size()));
            return imageViewlist.get(position%imageViewlist.size());
        }
    }
}
