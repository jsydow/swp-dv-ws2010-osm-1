package gui;

import core.data.DataStorage;
import core.data.DataTrack;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapView extends View {

	/**
	 * The canvas
	 */
	Canvas my_canvas;

	/**
	 * Here we draw our map
	 */
	Bitmap bitmap;

	/**
	 * The Paint objects helps us to define attributes for drawing operations
	 */
	Paint paint;

	/**
	 * Height and width variable of the actual view size.
	 */
	int width;
	int height;

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		init();
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		this.invalidate();
		return true;
	}

	private void init() {
		/*
		 * width = getWidth(); height = getHeight();
		 * 
		 * bitmap = Bitmap.createBitmap(width, height, Config.RGB_565); canvas =
		 * new Canvas(); canvas.setBitmap(bitmap);
		 */

	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		
	DataTrack track = DataStorage.getInstance().getCurrentTrack();
		
	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setTextSize(25);
		 
	canvas.drawColor(Color.BLACK);
		 
	if (track != null) {
		
		canvas.drawText("Anzahl der POI's:" + track.getNodes().size() ,10,25,paint);
		canvas.drawText("Anzahl der Wege: " + track.getWays().size() ,10,55,paint);
		
		if(  track.getCurrentWay() != null  ){
			
			canvas.drawText("Anzahl der Punkte im aktuellen Weg: " + track.getWays().size() ,10,80,paint);
			
		}
		
		//canvas.drawText("Anzahl der Flächen: ", 10, 110, paint);
		//int length = track.getCurrentWay().getNodes().size();
		//canvas.drawText("Anzahl der TrackPoints " + length, 10, 140, paint);
	}

	}

}
