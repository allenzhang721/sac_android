package com.hl.android.view.component.moudle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.DataUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.inter.Component;

public class HLCameraUIComponent extends RelativeLayout implements Component,SurfaceHolder.Callback{

	private ImageView  backOrFrontImage;
	public SurfaceView surface;
	private ImageView shutter;
	private ImageView showImage;
	
	public SurfaceHolder holder;
	public Camera camera;// 声明相机
	private boolean isBack = true;// false代表前置摄像头，true代表后置摄像头
	private Context mContext;
	private ComponentEntity mEntity;
	protected final int CAMERA_POSITION=0x10012;
	protected final int  CAMERA_SHUTTER=0x10013;
	private String sdCardPath;
	private String savePath;
	private String saveName;
	private int currentPictureId;
	private boolean canShutter;

	public HLCameraUIComponent(Context context,ComponentEntity entity){
		super(context);
		mEntity=entity;
		mContext=context;
	}

	// 响应点击事件
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case CAMERA_POSITION:
				removeView(showImage);
				shutter.setImageResource(R.drawable.scan_disable);
				canShutter=true;
				int cameraCount = 0;
				CameraInfo cameraInfo = new CameraInfo();
				cameraCount = Camera.getNumberOfCameras();
				for (int i = 0; i < cameraCount; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (isBack) {
						if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
							camera.stopPreview();
							camera.release();// 释放资源
							camera = null;// 取消原来摄像头
							camera = Camera.open(i);
							try {
								camera.setPreviewDisplay(holder);
							} catch (IOException e) {
								e.printStackTrace();
							}
							camera.startPreview();
							isBack = false;
							camera.autoFocus(null);
							break;
						}
					} else { 
						if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
							camera.stopPreview();
							camera.release();// 释放资源
							camera = null;// 取消原来摄像头
							camera = Camera.open(i);// 打开当前选中的摄像头
							try {
								camera.setPreviewDisplay(holder);
							} catch (IOException e) {
								e.printStackTrace();
							}
							camera.startPreview();// 开始预览
							isBack = true;
							camera.autoFocus(null);
							break;
						}
					}
				}
				if(isBack){
					backOrFrontImage.setImageResource(R.drawable.camer_b);
				}else{
					backOrFrontImage.setImageResource(R.drawable.camer_f);
				}
				break;
			case  CAMERA_SHUTTER:
				removeView(showImage);
				if(canShutter){
//					Parameters params = camera.getParameters();
//					params.setPictureFormat(ImageFormat.JPEG);
//					params.setPreviewSize(200, 200);
//					camera.setParameters(params);
					camera.takePicture(null, null, jpeg);
					shutter.setImageResource(R.drawable.scan_enable);
					canShutter=false;
				}else{
					camera.startPreview();// 数据处理完后继续开始预览
					shutter.setImageResource(R.drawable.scan_disable);
					canShutter=true;
				}
				break;
			}
		}
	};
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(holder);
				camera.startPreview();// 开始预览
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try{
			camera.stopPreview();
			camera.release();
			camera = null;
			holder = null;
			surface = null;
		}catch(Exception e){
			
		}
	}

	PictureCallback jpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
				currentPictureId++;
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath+File.separator+saveName+"_"+currentPictureId+".jpg"));
				DataUtils.savePreference((Activity)mContext, saveName, currentPictureId);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩的流里面
				bos.flush();// 刷新此缓冲区的输出流
				bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
				camera.stopPreview();// 关闭预览 处理数据
				bitmap.recycle();// 回收bitmap空间
				Toast.makeText(mContext, "Save picture success", Toast.LENGTH_SHORT).show();
				mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+sdCardPath+File.separator+"hlPicture")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	private Bitmap currentShowBitmap;	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity=entity;
	}

	@Override
	public void load() {
		if (camera == null) {
			try{
				camera = Camera.open();
				if(camera==null){
					Log.d("wdy", "无摄像头，不加载摄像头视图，返回！！");
					return;
				}
			}catch(Exception e){
				e.printStackTrace();
				Log.d("wdy", "打开摄像头失败，不加载摄像头视图，返回！！");
				return;
			}
			
		}
		sdCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
		savePath=sdCardPath+File.separator+mContext.getPackageName()+File.separator+"HLPicture";
		saveName=mContext.getPackageName()+BookSetting.BOOK_PATH.replaceAll("/", "")+"_"+BookController.getInstance().getViewPage().getEntity().getID()+"_"+mEntity.getComponentId();
		currentPictureId=DataUtils.getPreference((Activity)mContext, saveName, 0);
		InputStream inputStream=FileUtils.getInstance().getFileInputStreamFilePath(savePath+File.separator+saveName+"_"+currentPictureId+".jpg");
		currentShowBitmap=BitmapFactory.decodeStream(inputStream);
		File mfilepath=new File(savePath);
		if(!mfilepath.isDirectory()||!mfilepath.exists()){
			mfilepath.mkdirs();
		}
		RelativeLayout.LayoutParams surfaceOrImagelp=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		surface = new SurfaceView(mContext);
		holder = surface.getHolder();
		holder.addCallback(this);
		addView(surface,surfaceOrImagelp);
		if(currentShowBitmap!=null){
			showImage=new ImageView(mContext);
			showImage.setImageBitmap(currentShowBitmap);
			showImage.setScaleType(ScaleType.FIT_XY);
			addView(showImage,surfaceOrImagelp);
		}
		int cameraCount = Camera.getNumberOfCameras();
		if(cameraCount>=1){
			backOrFrontImage = new ImageView(mContext);
			backOrFrontImage.setId(CAMERA_POSITION);
			backOrFrontImage.setImageResource(R.drawable.camer_b);
			RelativeLayout.LayoutParams positionlp=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			positionlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			positionlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			addView(backOrFrontImage,positionlp);
			backOrFrontImage.setOnClickListener(listener);
		}
		shutter  =  new ImageView(mContext);
		shutter.setId(CAMERA_SHUTTER);
		if(currentShowBitmap!=null){
		shutter.setImageResource(R.drawable.scan_enable);
		canShutter=false;
		}else{
		shutter.setImageResource(R.drawable.scan_disable);
		canShutter=true;
		}
		RelativeLayout.LayoutParams shutterlp=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		shutterlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		shutterlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		addView(shutter,shutterlp);
		shutter.setOnClickListener(listener);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
}
