package gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapView extends SurfaceView implements OnTouchListener {
	
	/**
	 * The canvas
	 */
	Canvas canvas;
	
	/**
	 * Here we draw our map 
	 */
	Bitmap bitmap; 
	
	/**
	 * The Paint objects helps us to define attributes for drawing operations
	 */
	Paint  paint;         	

	int width;        		
	int height;        		

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		init();
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void init() {
		 width         = getWidth();
	     height        = getHeight();
	     
	     bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);	    
	     canvas = new Canvas();
	     canvas.setBitmap(bitmap);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
	}

}
