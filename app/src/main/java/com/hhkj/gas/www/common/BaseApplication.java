package com.hhkj.gas.www.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseApplication extends Application {
	private static final boolean DEVELOPER_MODE = true;
	public static BaseApplication application;
	protected boolean isNeedCaughtExeption = true;// 是否捕获未知异常
	private MyUncaughtExceptionHandler uncaughtExceptionHandler;
	private static String packgeName;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		application = this;

		packgeName = getPackageName();
		if (isNeedCaughtExeption) {
			cauchException();
		}
		initImageLoader(application);
		P.c("启动");
//		mLocationClient = new LocationClient(application); // 声明LocationClient类
//		mLocationClient.registerLocationListener(myListener); // 注册监听函数
//		initLocation();
//		mLocationClient.start();

	}
	public static String getName(){
		return  packgeName;
	}
	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCacheSize(2 * 1024 * 1024) //缓存到内存的最大数据
				.memoryCacheSize(50 * 1024 * 1024) //设置内存缓存的大小
				.diskCacheFileCount(200)
				//.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	/**
	 * 获得应用版本
	 *
	 * @return
	 */
	public String tripLittle(String temp) {
		return temp.replaceAll("\\.", "");
	}

	public String getVersion() {

		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
	private PendingIntent restartIntent;

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}

	private void cauchException() {
		System.out
				.println("-----------------------------------------------------");
		// 程序崩溃时触发线程
		uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	private class MyUncaughtExceptionHandler implements
			UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// 保存错误日志
			saveCatchInfo2File(ex);
			// 如果报错就不进行重启
//			android.os.Process.killProcess(android.os.Process.myPid());
		//System.exit(0);
		}

	};


	/**
	 * 保存错误信息到文件中
	 *
	 * @return 返回文件名称
	 */
	private String saveCatchInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
	    P.c("日志"+sb);
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String time = formatter.format(new Date());
			String fileName = time + ".txt";
			System.out.println("fileName:" + fileName);
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String filePath = Common.BASE_DIR + Common.DIR
						+ packgeName + "/crash/";
				File dir = new File(filePath);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						// 创建目录失败: 一般是因为SD卡被拔出了
						return "";
					}
				}
				P.c("filePath + fileName:" + filePath + fileName);
				FileOutputStream fos = new FileOutputStream(filePath + fileName);
				fos.write(sb.getBytes());
				fos.close();
				// 文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
			}
			return fileName;
		} catch (Exception e) {
			P.c("an error occured while writing file..." + e.getMessage());
		}
		return null;
	}

	/**
	 * 百度服務
	 */
	/*public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000*6*5;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		mLocationClient.setLocOption(option);
		P.c("~~~~~~~~~~");
	}*/

	/*public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			P.c("---->&&");
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			P.c("BaiduLocationApiDem---" + sb.toString());
			//
			// load(location);
			try {
				val(location);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}
	private final String SEND_ = "http://api.map.baidu.com/geodata/v3/poi/create";
	private final String VAL_ = "http://api.map.baidu.com/geodata/v3/poi/list";
	private final String UPDATA_ = "http://api.map.baidu.com/geodata/v3/poi/update";
	private final String AK = "jQsqcYNYn1vFm6Dcs954fis3gFDoILZd";
	private final String geotable_id = "161478";
	private final String sn = "EDWU1RuimaUfRuGfZdt1xNQTrQLmvioQ";
	private final String TAG="基本版";
	private void load(BDLocation location) {
		requestCall = OkHttpUtils
				.post()
				.url(SEND_)
				.addParams("latitude", String.valueOf(location.getLatitude()))
				.addParams("longitude", String.valueOf(location.getLongitude()))
				.addParams("coord_type", "2")
				.addParams("geotable_id", geotable_id).addParams("ak", AK)
				.addParams("address", location.getAddrStr()!=null?location.getAddrStr():"定位异常")
				// .addParams("sn", sn)
				.addParams("tags", TAG)
				.addParams("version", BaseApplication.application.getVersion())
				.addParams("mac",FileUtils.getDeviceId() ).build();
		requestCall.execute(loginCallBack);
	}

	private synchronized void val(final BDLocation location) {
		P.c("时间+"+System.currentTimeMillis());

		valCall = OkHttpUtils.get().url(VAL_).addParams("ak", AK)
				.addParams("geotable_id", geotable_id)
				.addParams("mac", FileUtils.getDeviceId()).build();
		valCall.execute(new StringCallback() {

			@Override
			public void onResponse(String response, int id) {
				// TODO Auto-generated method stub
				P.c(response);
				try {
					JSONObject json = new JSONObject(response);
					if (json.getInt("status") == 0) {
						if (json.getInt("total") == 0) {
							// 增加
							if(location!=null){
								 load(location);
							 }
						} else  {
							// 修改
							JSONArray ob=  json.getJSONArray("pois");
							if(ob!=null){
								 for(int i=0;i<ob.length();i++){
									 JSONObject o = ob.getJSONObject(i);
									 P.c(ob.length()+"机器:"+o.getString("mac")+"----"+FileUtils.getDeviceId());
									 if( o.getString("mac").equals(FileUtils.getDeviceId())){
										 //表示有数据
										 P.c("修改---->");
										 P.c("数据"+o.getString("id"));
										 if(location!=null&&o!=null){
											 P.c("禁止执行");
											 updata(location,o.getString("id"));
										 }

										 return;
									 }
								 }
								 P.c("增加---->");
								 if(location!=null){
									 load(location);
								 }
							}


						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(valCall!=null){
					valCall.cancel();
					valCall = null;
				}
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				// TODO Auto-generated method stub

			}
		});
	}

	*//**
	 * 更新数据
	 *//*
	private void updata(BDLocation location,String id) {
		P.c(String.valueOf(location.getLatitude())+"---->"+String.valueOf(location.getLongitude())+"-->"+location.getAddrStr());
		updataCall = OkHttpUtils
				.post()
				.url(UPDATA_)
				//.addParams("mac", FileUtils.getDeviceId())
				.addParams("id", id)
				.addParams("latitude", String.valueOf(location.getLatitude()))
				.addParams("longitude", String.valueOf(location.getLongitude()))
				.addParams("geotable_id", geotable_id)
				.addParams("address", location.getAddrStr()!=null?location.getAddrStr():"定位异常")
				//
				.addParams("ak", AK)
				.addParams("tags", TAG)
				.addParams("version", BaseApplication.application.getVersion())
				.addParams("coord_type","2").build();
				 if(upCallback!=null){
					 updataCall.execute(upCallback);
				 }


	}

	private StringCallback upCallback = new StringCallback() {
		@Override
		public void onError(Call call, Exception e, int id) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onResponse(String response, int id) {
			// TODO Auto-generated method stub
			P.c("修改" + response);
			if(updataCall!=null){
				updataCall.cancel();
				updataCall = null;
			}

		}

	};
	private StringCallback loginCallBack = new StringCallback() {
		@Override
		public void onError(Call call, Exception e, int id) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onResponse(String response, int id) {
			// TODO Auto-generated method stub
			P.c("增加" + response);
			if(requestCall!=null){
				requestCall.cancel();
				requestCall = null;
			}
		}

	};

	private RequestCall requestCall, valCall, updataCall;*/
}
