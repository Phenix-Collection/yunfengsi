package com.yunfengsi.ErWeiMa;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 作者：因陀罗网 on 2017/8/18 19:04
 * 公司：成都因陀罗网络科技有限公司
 */

public class QRActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private ZXingView zXingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.qr_activity);
        zXingView = (ZXingView) findViewById(R.id.zxingview);
        zXingView.setDelegate(this);
//        zXingView.changeToScanBarcodeStyle();//切换到条形码扫描
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void vibrator() {
        //获取系统震动服务
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);

    }

    @Override
    protected void onStop() {
        zXingView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        zXingView.startCamera();
        zXingView.showScanRect();
        zXingView.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
//扫描成功后调用震动器
        vibrator();
        //显示扫描结果
        LogUtil.e(result);
        result = result.substring(16, result.length() - 16);
        LogUtil.e("获取的最终结果：：" + result);
        postSign(result);


//        Intent intent =new Intent();
//        intent.putExtra("code",result);
//        setResult(0,intent);
//        finish();
        //再次延时1.5秒后启动
//        zXingView.startSpot();

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "相机打开失败,请稍后重试", Toast.LENGTH_SHORT).show();
    }

    /**
     * 活动签到
     *
     * @param id 活动的id  识别结果中提取
     */
    private void postSign(String id) {
        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("act_id", getIntent().getStringExtra("id"));
            js.put("user_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        LogUtil.e("活动签到：：；" + js);
        OkGo.post(Constants.Signact).params("key", m.K())
                .params("msg", m.M())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String, String> map = AnalyticalJSON.getHashMap(s);
                        if (map != null) {
                            if ("000".equals(map.get("code"))) {
                                ToastUtil.showToastShort("签到成功");
                            } else if ("002".equals(map.get("code"))) {
                                ToastUtil.showToastShort("该用户已经签过到了,请到个人活动产看");
                            } else if ("003".equals(map.get("code"))) {
                                ToastUtil.showToastShort("未查到用户报名信息，该用户尚未报名");
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(QRActivity.this, "", "正在签到，请稍等");
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                        finish();
                    }
                });
    }
//    @OnClick({R.id.btn_openligtht, R.id.btn_closeligtht, R.id.btn_photo,R.id.btn_openBarcode, R.id.btn_openQRcode})
//    public void onClick(View view) {
//        switch (view.getCode()) {
//            case R.id.btn_openligtht:
//                //打开闪关灯
//                zxingview.openFlashlight();
//                break;
//            case R.id.btn_closeligtht:
//                //关闭闪光灯
//                zxingview.closeFlashlight();
//                break;
//            case R.id.btn_photo:
//                //参数1 应用程序上下文
//                //参数2 拍照后图片保存的目录。如果传null表示没有拍照功能，如果不为null则具有拍照功能，
//                //参数3 图片选择张数的最大值
//                //参数4 当前已选中的图片路径集合，可以传null
//                //参数5 滚动列表时是否暂停加载图片
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
//            case R.id.btn_openBarcode:
//                //切换到条形码扫描
//                zxingview.changeToScanBarcodeStyle();
//                break;
//            case R.id.btn_openQRcode:
//                //切换到二维码扫描
//                zxingview.changeToScanQRCodeStyle();
//                break;
//
//        }
//    }


}
