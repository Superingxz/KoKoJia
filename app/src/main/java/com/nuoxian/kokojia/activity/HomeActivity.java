package com.nuoxian.kokojia.activity;

/**
 * Created by 陈思龙 on 2016/6/17.
 * 首页
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.fragment.ClassificationFragment;
import com.nuoxian.kokojia.fragment.DownLoadFragment;
import com.nuoxian.kokojia.fragment.IndexFragment;
import com.nuoxian.kokojia.fragment.MineFragment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {
    private long exitTime = 0;
    private int urlId = 2;
    private int urlPage = 1;
    private RequestQueue mQueue;
    public static RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<Fragment> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this, R.color.titlebar);

        mQueue = MyApplication.getRequestQueue();
        //注册微信
        registWX();
        //注册QQ
        registQQ();
        //判断是否授权
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请授权
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    CommonValues.READ_AND_WRITE);
        } else {
            //已授权,创建文件夹
            makeDir();
        }

        initFragments();
        initView();

        //判断是否有更新
        isNeedUpdata(Urls.UPDATA_URL);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new IndexFragment());
        fragments.add(new ClassificationFragment());
        fragments.add(new MineFragment());
        fragments.add(new DownLoadFragment());
    }

    /**
     * 创建文件夹
     */
    private void makeDir() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "kokojia");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 注册微信
     */
    private void registWX() {
        MyApplication.wx = WXAPIFactory.createWXAPI(this, CommonValues.APP_ID_WEIXIN, true);
        MyApplication.wx.registerApp(CommonValues.APP_ID_WEIXIN);
    }

    /**
     * 注册QQ
     */
    private void registQQ() {
        MyApplication.mTencent = Tencent.createInstance(CommonValues.APP_ID_TENCENT, this.getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 申请权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == CommonValues.READ_AND_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                makeDir();
            } else {
                CommonMethod.toast(this, "请同意此项权限，否则视频无法下载和播放");
                //申请授权
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        CommonValues.READ_AND_WRITE);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_home);
        mViewPager = (ViewPager) findViewById(R.id.vp_home);
        //加载ViewPager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);
        //模块切换监听
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeFragment(checkedId);
            }
        });
        //Viewpager滑动切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //改变模块的选项
                RadioButton rb = (RadioButton) mRadioGroup.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        initFragments();
//
//        //加载fragment
//        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, MyApplication.fragments,
//                R.id.fl_home, mRadioGroup);
    }

    /**
     * 切换fragment
     * @param checkedId
     */
    private void changeFragment(int checkedId){
        switch (checkedId) {
            case R.id.rb_home://首页
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_all://分类
                mViewPager.setCurrentItem(1);
                break;
            case R.id.rb_mine://我的
                mViewPager.setCurrentItem(2);
                break;
            case R.id.rb_download://下载
                mViewPager.setCurrentItem(3);
                break;
            default:
                break;
        }
    }

    /**
     * 是否需要更新
     *
     * @param url
     * @return
     */
    private void isNeedUpdata(String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {
                        JSONObject j2 = j1.getJSONObject("data");
                        //如果服务器的版本和当前的版本不对就更新
                        if (!CommonMethod.getVersionName(HomeActivity.this).equals(j2.getString("version"))) {
                            //弹出更新对话框
                            showUpdataDialog(j2.getString("url"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mQueue.add(request);
    }

    /**
     * 显示版本更新弹窗
     */
    private void showUpdataDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage("发现有新的版本是否进行版本更新?");
        builder.setCancelable(false);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载APK
                if (CommonMethod.isWifiConnected(HomeActivity.this)) {
                    //如果连接了wifi，直接下载
                    downLoad(url);
                } else {
                    //弹出提示框，提醒没有连接wifi
                    showNotConnectWifiDialog(url);
                }
            }
        });
        builder.create().show();
    }

    /**
     * 显示没有连接wifi对话框
     */
    private void showNotConnectWifiDialog(final String url) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告");
        builder.setCancelable(false);
        builder.setMessage("检测到您没有连接wifi,更新时会消耗流量是否继续?");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //更新
                downLoad(url);
            }
        });
        builder.create().show();
    }

    /**
     * 安装APK
     *
     * @param file
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    //实现再按一次退出
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            // ToastUtil.makeToastInBottom("再按一次退出应用", MainMyselfActivity);
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    /**
     * 下载APK
     *
     * @param url
     */
    private void downLoad(String url) {
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "kokojia.apk");
//        if (file.exists()) {
//            //删除原来的apk
//            file.delete();
//        }
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setMessage("正在下载更新");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.show();
        HttpUtils http = new HttpUtils();
        HttpHandler handler = http.download(url, Environment.getExternalStorageDirectory() + File.separator + "kokojia.apk",
                true,
                true,
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        pd.setProgress((int) (current / (double) total * 100));
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        pd.dismiss();
                        installApk(new File(Environment.getExternalStorageDirectory() + File.separator + "kokojia.apk"));
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if ("maybe the file has downloaded completely".equals(msg)) {
                            //如果安装包已存在
                            installApk(new File(Environment.getExternalStorageDirectory() + File.separator + "kokojia.apk"));
                        } else {
                            Toast.makeText(HomeActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
