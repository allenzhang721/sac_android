package com.hl.android.view.gallary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hl.android.R;
import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.gallary.base.AbstractGalley;
import com.hl.android.view.gallary.base.ImageManager;

public class Galley3D extends AbstractGalley {
	private Camera mCamera = new Camera();// 相机类
	private int mMaxRotationAngle = 60;// 最大转动角度
	private int mMaxZoom = -300;// //最大缩放值
	private int mCoveflowCenter;// 半径值
	int width,height;
	public Galley3D(Context context) {
		super(context);
		// 支持转换 ,执行getChildStaticTransformation方法
		this.setStaticTransformationsEnabled(true);
	}

	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	// 控制gallery中每个图片的旋转(重写的gallery中方法)
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		// 取得当前子view的半径值
		final int childCenter = getCenterOfView(child);

		final int childWidth = child.getWidth();
		// 旋转角度
		int rotationAngle = 0;
		// 重置转换状态
		t.clear();
		// 设置转换类型
		t.setTransformationType(Transformation.TYPE_MATRIX);
		// 如果图片位于中心位置不需要进行旋转
		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, 0);
		} else {
			// 根据图片在gallery中的位置来计算图片的旋转角度
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
	
			// 如果旋转角度绝对值大于最大旋转角度返回（-mMaxRotationAngle或mMaxRotationAngle;）
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
						: mMaxRotationAngle;
			}
			transformImageBitmap((ImageView) child, t, rotationAngle);
		}
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(ImageView child, Transformation t,
			int rotationAngle) {
		// 对效果进行保存
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		// 图片高度
		final int imageHeight = child.getLayoutParams().height;
		// 图片宽度
		final int imageWidth = child.getLayoutParams().width;

		// 返回旋转角度的绝对值
		final int rotation = Math.abs(rotationAngle);

		// 在Z轴上正向移动camera的视角，实际效果为放大图片。
		// 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
		mCamera.translate(0.0f, 0.0f, 100.0f);
		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
		}
		// 在Y轴上旋转，对应图片竖向向里翻转。
		// 如果在X轴上旋转，则对应图片横向向里翻转。
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();
	}

	
//	@Override
//	public void setSnapshots(ArrayList<String> snapshots) {
//		Galley3DAdapter adapter = new Galley3DAdapter(this.getContext(), snapshots,
//				width, height);
//		adapter.createReflectedImages();
//		this.setAdapter(adapter);
//	}
	
	@Override
	protected Bitmap getBitmap(String resourceID, int width, int height) {
		Bitmap b = BitmapUtils.getBitMap(resourceID, mContext,width,(int)height);
		return ImageManager.createReflectionImageWithOrigin(b);
//		// 倒影图和原图之间的距离
//		final int reflectionGap = 4;
//
//		// 返回原图解码之后的bitmap对象
//		Bitmap originalImage = BitmapUtil.getBitMap(resourceID, mContext,
//				width, height);
//
//		int imageHeight = (int) (height * 0.6);
//		Bitmap resizeBmp = Bitmap.createScaledBitmap(originalImage, width,
//				imageHeight, true);
//
//		// 创建矩阵对象
//		Matrix matrix = new Matrix();
//
//		// 指定一个角度以0,0为坐标进行旋转
//		matrix.setRotate(30);
//
//		// 指定矩阵(x轴不变，y轴相反)
//		matrix.preScale(1, -1);
//
//		// 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
//		Bitmap reflectionImage = Bitmap.createBitmap(resizeBmp, 0,
//				(int) (imageHeight * 0.6), width, (int) (imageHeight * 0.3),
//				matrix, false);
//
//		// 创建一个宽度不变，高度为原图+倒影图高度的位图
//		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height,
//				Config.ARGB_8888);
//
//		// 将上面创建的位图初始化到画布
//		Canvas canvas = new Canvas(bitmapWithReflection);
//		canvas.drawBitmap(resizeBmp, 0, 0, null);
//
//		Paint deafaultPaint = new Paint();
//		deafaultPaint.setAntiAlias(false);
//		// canvas.drawRect(0, height, width, height +
//		// reflectionGap,deafaultPaint);
//		canvas.drawBitmap(reflectionImage, 0, imageHeight + reflectionGap, null);
//		Paint paint = new Paint();
//		paint.setAntiAlias(false);
//
//		/**
//		 * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式，
//		 * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
//		 */
//		LinearGradient shader = new LinearGradient(0, resizeBmp.getHeight(), 0,
//				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
//				0x00ffffff, TileMode.MIRROR);
//		// 设置阴影
//		paint.setShader(shader);
//		paint.setXfermode(new PorterDuffXfermode(
//				android.graphics.PorterDuff.Mode.DST_IN));
//		// 用已经定义好的画笔构建一个矩形阴影渐变效果
//		canvas.drawRect(0, imageHeight, width, bitmapWithReflection.getHeight()
//				+ reflectionGap, paint);
//
//		return bitmapWithReflection;
	}


	@Override
	public RelativeLayout.LayoutParams getGalleryLp() {
		RelativeLayout.LayoutParams galleryLp = new RelativeLayout.LayoutParams(
				BookSetting.SCREEN_WIDTH,
				BookSetting.SNAPSHOTS_HEIGHT);
		galleryLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		galleryLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		return galleryLp;
		
//		int height,width;
//		if (BookSetting.IS_HOR){
//			height = (int) (BookSetting.SCREEN_HEIGHT / 2) + 5;
//		}else{
//			height = (int) (BookSetting.SCREEN_WIDTH / 2) + 5;
//		}
//		width = BookSetting.SCREEN_WIDTH;
//		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(width,
//				height);
//		this.width = width;
//		this.height = height - 70;
//		layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		
//		return layoutParams1;
	}

	@Override
	public void playAnimation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setWaitLoad(ImageView img) {
		img.setImageResource(R.drawable.scene_ic_loading_invert_3d);
	}

	@Override
	protected float getSizeRatio() {
		return 0.5f;
	}

}
