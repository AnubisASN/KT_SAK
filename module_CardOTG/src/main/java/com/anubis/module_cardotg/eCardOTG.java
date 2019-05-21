package com.anubis.module_cardotg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;


import com.huashi.otg.sdk.HSIDCardInfo;
import com.huashi.otg.sdk.HandlerMsg;
import com.huashi.otg.sdk.HsOtgApi;
import com.huashi.otg.sdk.HsSerialPortSDK;
import com.huashi.otg.sdk.Test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class eCardOTG extends Activity {

	private TextView sam, tv_info, statu;
	private ImageView iv_photo;
	private Button conn, read,autoread, ComConn, ComRead,ComClose;
	boolean m_Auto = false;
	HsOtgApi api;
	HsSerialPortSDK ComApi;
	String filepath="";
	SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式

	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 99 || msg.what == 100) {
				statu.setText((String)msg.obj);
			}
			//第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
			if (msg.what ==HandlerMsg.CONNECT_SUCCESS) {
				statu.setText("连接成功");
				sam.setText(api.GetSAMID());
			}
			if (msg.what == HandlerMsg.CONNECT_ERROR) {
				statu.setText("连接失败");
			}
			if (msg.what == HandlerMsg.READ_ERROR) {
				//cz();
				//statu.setText("卡认证失败");
				statu.setText("请放卡...");
			}
			if (msg.what == HandlerMsg.READ_SUCCESS) {
				statu.setText("读卡成功");
				HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
				byte[] fp = new byte[1024];
				fp = ic.getFpDate();
				String m_FristPFInfo = "";
				String m_SecondPFInfo = "";

				if (fp[4] == (byte)0x01) {
					m_FristPFInfo = String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[5]), fp[6]);
				} else {
					m_FristPFInfo = "身份证无指纹 \n";
				}
				if (fp[512 + 4] == (byte)0x01) {
					m_SecondPFInfo = String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[512 + 5]),
							fp[512 + 6]);
				} else {
					m_SecondPFInfo = "身份证无指纹 \n";
				}
				if (ic.getcertType() == " ") {
					tv_info.setText("证件类型：身份证\n" + "姓名："
							+ ic.getPeopleName() + "\n" + "性别：" + ic.getSex()
							+ "\n" + "民族：" + ic.getPeople() + "\n" + "出生日期："
							+ df.format(ic.getBirthDay()) + "\n" + "地址："
							+ ic.getAddr() + "\n" + "身份号码：" + ic.getIDCard()
							+ "\n" + "签发机关：" + ic.getDepartment() + "\n"
							+ "有效期限：" + ic.getStrartDate() + "-"
							+ ic.getEndDate() + "\n" + m_FristPFInfo + "\n"
							+ m_SecondPFInfo);
				} else {
					if(ic.getcertType() == "J")
					{
						tv_info.setText("证件类型：港澳台居住证（J）\n"
								+ "姓名：" + ic.getPeopleName() + "\n" + "性别："
								+ ic.getSex() + "\n"
								+ "签发次数：" + ic.getissuesNum() + "\n"
								+ "通行证号码：" + ic.getPassCheckID() + "\n"
								+ "出生日期：" + df.format(ic.getBirthDay())
								+ "\n" + "地址：" + ic.getAddr() + "\n" + "身份号码："
								+ ic.getIDCard() + "\n" + "签发机关："
								+ ic.getDepartment() + "\n" + "有效期限："
								+ ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
								+ m_FristPFInfo + "\n" + m_SecondPFInfo);
					}
					else{
						if(ic.getcertType() == "I")
						{
							tv_info.setText("证件类型：外国人永久居留证（I）\n"
									+ "英文名称：" + ic.getPeopleName() + "\n"
									+ "中文名称：" + ic.getstrChineseName() + "\n"
									+ "性别：" + ic.getSex() + "\n"
									+ "永久居留证号：" + ic.getIDCard() + "\n"
									+ "国籍：" + ic.getstrNationCode() + "\n"
									+ "出生日期：" + df.format(ic.getBirthDay())
									+ "\n" + "证件版本号：" + ic.getstrCertVer() + "\n"
									+ "申请受理机关：" + ic.getDepartment() + "\n"
									+ "有效期限："+ ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
									+ m_FristPFInfo + "\n" + m_SecondPFInfo);
						}
					}

				}
				Test.test("/mnt/sdcard/test.txt4", ic.toString());
				try {
					int ret = api.Unpack(filepath, ic.getwltdata());// 照片解码
					Test.test("/mnt/sdcard/test3.txt", "解码中");
					if (ret != 0) {// 读卡失败
						return;
					}
					FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
					Bitmap bmp = BitmapFactory.decodeStream(fis);
					fis.close();
					iv_photo.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					Toast.makeText(getApplicationContext(), "头像不存在！", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					Toast.makeText(getApplicationContext(), "头像读取错误", Toast.LENGTH_SHORT).show();
				}catch (Exception e)
				{
					Toast.makeText(getApplicationContext(), "头像解码失败", Toast.LENGTH_SHORT).show();
				}

			}
			if (msg.what == HandlerMsg.ComREAD_SUCCESS) {
				statu.setText("读卡成功");
				HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
				byte[] fp = new byte[1024];
				fp = ic.getFpDate();
				String m_FristPFInfo = "";
				String m_SecondPFInfo = "";

				if (fp[4] == (byte)0x01) {
					m_FristPFInfo = String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[5]), fp[6]);
				} else {
					m_FristPFInfo = "身份证无指纹 \n";
				}
				if (fp[512 + 4] == (byte)0x01) {
					m_SecondPFInfo = String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[512 + 5]),
							fp[512 + 6]);
				} else {
					m_SecondPFInfo = "身份证无指纹 \n";
				}
				if (ic.getcertType() == " ") {
					tv_info.setText("证件类型：身份证\n" + "姓名："
							+ ic.getPeopleName() + "\n" + "性别：" + ic.getSex()
							+ "\n" + "民族：" + ic.getPeople() + "\n" + "出生日期："
							+ df.format(ic.getBirthDay()) + "\n" + "地址："
							+ ic.getAddr() + "\n" + "身份号码：" + ic.getIDCard()
							+ "\n" + "签发机关：" + ic.getDepartment() + "\n"
							+ "有效期限：" + ic.getStrartDate() + "-"
							+ ic.getEndDate() + "\n" + m_FristPFInfo + "\n"
							+ m_SecondPFInfo);
				} else {
					if(ic.getcertType() == "J")
					{
						tv_info.setText("证件类型：港澳台居住证（J）\n"
								+ "姓名：" + ic.getPeopleName() + "\n" + "性别："
								+ ic.getSex() + "\n"
								+ "签发次数：" + ic.getissuesNum() + "\n"
								+ "通行证号码：" + ic.getPassCheckID() + "\n"
								+ "出生日期：" + df.format(ic.getBirthDay())
								+ "\n" + "地址：" + ic.getAddr() + "\n" + "身份号码："
								+ ic.getIDCard() + "\n" + "签发机关："
								+ ic.getDepartment() + "\n" + "有效期限："
								+ ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
								+ m_FristPFInfo + "\n" + m_SecondPFInfo);
					}
					else{
						if(ic.getcertType() == "I")
						{
							tv_info.setText("证件类型：外国人永久居留证（I）\n"
									+ "英文名称：" + ic.getPeopleName() + "\n"
									+ "中文名称：" + ic.getstrChineseName() + "\n"
									+ "性别：" + ic.getSex() + "\n"
									+ "永久居留证号：" + ic.getIDCard() + "\n"
									+ "国籍：" + ic.getstrNationCode() + "\n"
									+ "出生日期：" + df.format(ic.getBirthDay())
									+ "\n" + "证件版本号：" + ic.getstrCertVer() + "\n"
									+ "申请受理机关：" + ic.getDepartment() + "\n"
									+ "有效期限："+ ic.getStrartDate() + "-" + ic.getEndDate() + "\n"
									+ m_FristPFInfo + "\n" + m_SecondPFInfo);
						}
					}

				}
				Test.test("/mnt/sdcard/test.txt4", ic.toString());
				try {
					int ret = ComApi.Unpack(ic.getwltdata());// 照片解码
					Test.test("/mnt/sdcard/test3.txt", "解码中");
					if (ret != 0) {// 读卡失败
						return;
					}
					FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
					Bitmap bmp = BitmapFactory.decodeStream(fis);
					fis.close();
					iv_photo.setImageBitmap(bmp);
				} catch (FileNotFoundException e) {
					Toast.makeText(getApplicationContext(), "头像不存在！", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					Toast.makeText(getApplicationContext(), "头像读取错误", Toast.LENGTH_SHORT).show();
				}catch (Exception e)
				{
					Toast.makeText(getApplicationContext(), "头像解码失败", Toast.LENGTH_SHORT).show();
				}

			}
		};
	};

	/**
	 * 判断是否拥有权限
	 *
//	 * @param permissions
	 * @return
	 */
//    public boolean hasPermission(String... permissions) {
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
//                return false;
//        }
//        return true;
//    }
//
//    /**
//     * 请求权限
//     */
//    protected void requestPermission(int code, String... permissions) {
//        ActivityCompat.requestPermissions(this, permissions, code);
//        ToastUtil.showMessage(this, "如果拒绝授权,会导致应用无法正常使用", Toast.Length_SHORT);
//    }
//
//    /**
//     * 请求权限的回调
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case Constants.CODE_CAMERA:
//            //例子：请求相机的回调
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    ToastUtil.showMessage(this, "现在您拥有了权限");
//                    // 这里写你需要的业务逻辑
//                    doYourNeedDo();
//                } else {
//                    ToastUtil.showMessage(this, "您拒绝授权,会导致应用无法正常使用，可以在系统设置中重新开启权限", Toast.Length_SHORT);
//                }
//                break;
//            case Constants.CODE_READ_EXTERNAL_STORAGE:
//            //另一个权限的回调
//                break;
//        }
//    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
		//	filepath = "/mnt/sdcard/wltlib";// 授权目录
		Log.e("LJFDJ", filepath);
		initView();
		setEnven();
	}

	private void setEnven() {
		conn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				copy(eCardOTG.this, "base.dat", "base.dat", filepath);
				copy(eCardOTG.this, "license.lic", "license.lic", filepath);
				api = new HsOtgApi(h, eCardOTG.this);
				int ret = api.init();// 因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
				if (ret == 1) {
					statu.setText("连接成功");
					sam.setText(api.GetSAMID());
				} else {
					statu.setText("连接失败");
				}
			}
		});
		read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cz();
				if (api.Authenticate(200, 200) != 1) {
					statu.setText("卡认证失败");
					return;
				}
				HSIDCardInfo ici = new HSIDCardInfo();
				if (api.ReadCard(ici, 200, 1300) == 1) {
					Message msg = Message.obtain();
					msg.obj = ici;
					msg.what = HandlerMsg.READ_SUCCESS;
					h.sendMessage(msg);
				}
			}

		});
		autoread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cz();
				if (m_Auto) {
					m_Auto = false;
					autoread.setText("自动读卡");
				}
				else{
					m_Auto = true;
					new Thread(new CPUThread()).start();
					autoread.setText("停止读卡");
				}
			}
		});
		ComConn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					ComApi = new HsSerialPortSDK(eCardOTG.this,filepath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int ret = ComApi.init("/dev/ttyS3",115200,0);;// 因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
				if (ret == 0) {
					SystemClock.sleep(1000);
					statu.setText("连接成功");
					sam.setText(ComApi.GetSAM());
				} else {
					statu.setText("连接失败");
				}
			}
		});
		ComRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cz();
				if (ComApi.Authenticate(200) != 0) {
					statu.setText("卡认证失败");
					return;
				}
				HSIDCardInfo ici = new HSIDCardInfo();
				if (ComApi.Read_Card(ici, 2300) == 0) {
					Message msg = Message.obtain();
					msg.obj = ici;
					msg.what = HandlerMsg.ComREAD_SUCCESS;
					h.sendMessage(msg);
				}
				else
				{
					statu.setText("读卡失败");
					return;
				}
			}
		});
		ComClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					ComApi.close();
					statu.setText("串口断开成功");
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					statu.setText("串口断开失败");
				}
			}
		});
	}
	private void cz() {
		// TODO Auto-generated method stub
		tv_info.setText("");
		iv_photo.setImageBitmap(null);
	}

	public class CPUThread extends Thread {
		public CPUThread() {
			super();
		}
		@Override
		public void run() {
			super.run();
			HSIDCardInfo ici;
			Message msg;
			while (m_Auto) {
				/////////////////循环读卡，不拿开身份证
				if (api.NotAuthenticate(200, 200) != 1) {
					//////////////////循环读卡，需要重新拿开身份证
					//if (api.Authenticate(200, 200) != 1) {
					msg = Message.obtain();
					msg.what = HandlerMsg.READ_ERROR;
					h.sendMessage(msg);
				} else {
					ici = new HSIDCardInfo();
					if (api.ReadCard(ici, 200, 1300) == 1) {
						msg = Message.obtain();
						msg.obj = ici;
						msg.what = HandlerMsg.READ_SUCCESS;
						h.sendMessage(msg);
					}
				}
				SystemClock.sleep(300);
				msg = Message.obtain();
				msg.what = HandlerMsg.READ_ERROR;
				h.sendMessage(msg);
				SystemClock.sleep(300);
			}

		}
	}


	private void copy(Context context, String fileName, String saveName,
					  String savePath) {
		File path = new File(savePath);
		if (!path.exists()) {
			path.mkdir();
		}

		try {
			File e = new File(savePath + "/" + saveName);
			if (e.exists() && e.length() > 0L) {
				Log.i("LU", saveName + "存在了");
				return;
			}

			FileOutputStream fos = new FileOutputStream(e);
			InputStream inputStream = context.getResources().getAssets()
					.open(fileName);
			byte[] buf = new byte[1024];
			boolean len = false;

			int len1;
			while ((len1 = inputStream.read(buf)) != -1) {
				fos.write(buf, 0, len1);
			}

			fos.close();
			inputStream.close();
		} catch (Exception var11) {
			Log.i("LU", "IO异常");
		}

	}

	private void initView() {
		setContentView(R.layout.activity_otg);
		sam = (TextView) findViewById(R.id.sam);
		tv_info = (TextView) findViewById(R.id.tv_info);
		statu = (TextView) findViewById(R.id.statu);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		conn = (Button) findViewById(R.id.conn);
		read = (Button) findViewById(R.id.read);
		ComConn = (Button) findViewById(R.id.ComConn);
		ComRead = (Button) findViewById(R.id.ComRead);
		autoread = (Button) findViewById(R.id.autoread);
		ComClose = (Button) findViewById(R.id.ComClose);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (api == null) {
			return;
		}
		api.unInit();
	}

	/**
	 * 指纹 指位代码
	 *
	 * @param FPcode
	 * @return
	 */
	String GetFPcode(int FPcode) {
		switch (FPcode) {
			case 11:
				return "右手拇指";
			case 12:
				return "右手食指";
			case 13:
				return "右手中指";
			case 14:
				return "右手环指";
			case 15:
				return "右手小指";
			case 16:
				return "左手拇指";
			case 17:
				return "左手食指";
			case 18:
				return "左手中指";
			case 19:
				return "左手环指";
			case 20:
				return "左手小指";
			case 97:
				return "右手不确定指位";
			case 98:
				return "左手不确定指位";
			case 99:
				return "其他不确定指位";
			default:
				return "未知";
		}
	}

    /*
     * HsOtgApi api = new HsOtgApi(h, eCardOTG.this);初始化
     * api.init()连接
     * api.Authenticate(200, 200) 卡认证  1为成功然后才可以读卡
     * api.ReadCard(ici, 200, 1300) ici为身份证类   "姓名：" + ic.getPeopleName() + "\n" + "性别：" + ic.getSex() + "\n" + "民族：" + ic.getPeople()
                + "\n" + "出生日期：" + df.format(ic.getBirthDay()) + "\n" + "地址：" + ic.getAddr() + "\n" + "身份号码："
                + ic.getIDCard() + "\n" + "签发机关：" + ic.getDepartment() + "\n" + "有效期限：" + ic.getStrartDate()
                + "-" + ic.getEndDate()
                200 为发送数据时长  1300为接收数据时长
                返回1为正确
        api.Unpack(filepath, ic.getwltdata())在读卡成功后调用  filepath 为解码库的绝对路径
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
          ic.getwltdata()为身份证的照片数据
        返回1为解码数据成功照片存在 filepath + "/zp.bmp"
     */

}
