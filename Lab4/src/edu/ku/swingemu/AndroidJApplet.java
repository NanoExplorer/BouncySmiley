package edu.ku.swingemu;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

/**
 * Activity which expects the user to implement {@link #paint(Graphics)}
 * Similar to a JApplet. This is intended to make Android graphics easier
 * to use for someone familar with JApplets
 * 
 * @author Nathan Schwermann
 *
 */
public abstract class AndroidJApplet extends Activity {

	Graphics mGraphics;
	
	private static final int UPDATE = 1;
	private Handler mHandler;
	private static final double FRAMES_PER_SECOND = 30; //changing this value is encouraged!

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
 
        
        
        //create a new Graphics object and set it as the content of the screen.
        mGraphics = new Graphics(this);
        mGraphics.setSwingEmuActivity(this);
        mGraphics.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mGraphics);
        
       
        
        mHandler = new MyHandler(this);
		//start the simulation
		mHandler.sendEmptyMessage(UPDATE);
    }
    
    /**
     * Make changes to the screen buffer, this method must be overridden by
     * the user of this class but should never be directly called. Call {@link #repaint()} instead
     * if you want to force the screen to be drawn.
     * @param canvas
     */
    public abstract void paint(Graphics canvas);
    
    /**
     * Forces the screen to be redrawn.
     */
    final void repaint(){
    	mGraphics.invalidate();
    }
    
    /**
     * Handler designed to NOT memory leak
     * @author Nathan Schwermann modified (heavily, I'd say) by Christopher Rooney
     */
    private static class MyHandler extends Handler {
    	private final WeakReference<AndroidJApplet> jActivity;
    	
    	public MyHandler(AndroidJApplet activity) {
    		jActivity = new WeakReference<AndroidJApplet>(activity);
    	}
    	
    	@Override
		public void handleMessage(Message msg) {
    		AndroidJApplet activity = jActivity.get();
			activity.repaint();
			this.sendEmptyMessageDelayed(UPDATE, (long)(1000/FRAMES_PER_SECOND));//24fps
		}
    }
    
    
    /**
     * View to designed to mirror some of the java.awt.Graphics api
     * @author Nathan Schwermann
     */
    public static class Graphics extends View{
    	
    	Paint mOutline, mFill;   	
    	AndroidJApplet mActivity;
    	Bitmap mBitmap;
    	Canvas mCanvas;
    	
		public Graphics(Context context) {
			super(context);
			//initialize our Paint objects and set the background to be white like a jApplet.
			setBackgroundColor(Color.WHITE);
			mOutline = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
			mOutline.setColor(Color.BLACK);
			mOutline.setStyle(Paint.Style.STROKE);
			mOutline.setStrokeWidth(1f);
			mOutline.setTextAlign(Align.LEFT);
			mFill = new Paint(mOutline);
			mFill.setStyle(Paint.Style.FILL);
		}
		
		/**
		 * Sets the parent activity for callbacks in {@link #onDraw(Canvas)} so
		 * we can implement the same paint signature as an applet 
		 * @param activity
		 */
		final void setSwingEmuActivity(AndroidJApplet activity){
			mActivity = activity;
		}
				
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			//resize our offscreen canvas to match the screensize
			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(mActivity == null) throw new NullPointerException("You must assign an AndroidJApplet to the Graphics object.");
			//paint draws to our bitmap offscreen with the swing like api implemented by the student
			mActivity.paint(this);
			//commits the bitmap to our screen
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
		
		/**
		 * Clears the specified rectangle by filling it with the background color of the current drawing surface.
		 */
		public void clearRect(int x, int y, int w, int h){			
			mFill.setColor(Color.WHITE);
			fillRect(x, y, w, h);
			mFill.setColor(Color.WHITE);
		}
		
		//Applet APIs
		//All below APIs are made to have the same signature as
		//their swing counterpart.
		
		/**
		 * Draws an oval with just an outline of the current color no fill
		 */
		public void drawOval(int x, int y, int w, int h){
			mCanvas.drawOval(new RectF(x, y, x + w, y + h), mOutline);
		}
		
		/**
		 * Draws the outline of a circular or elliptical arc covering the specified rectangle.
			A positive value indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation. <br>
			<b>note: this behavior is backwards compared to Android's normal drawArc API which draws in a clockwise rotation.</b>
		 */
		public void drawArc(int x, int y, int w, int h, int start, int sweep){
			mCanvas.drawArc(new RectF(x, y, x + w, y + h), start, -sweep, false, mOutline);
		}
		
		/**
		 * Draws the text given by the specified string, using this graphics context's current font and color. 
		 * The baseline of the leftmost character is at position (x, y) in this graphics context's coordinate system. 
		 */
		public void drawString(String text, int x, int y){
			mCanvas.drawText(text, x, y, mOutline);
		}
		
		/**
		 * Draws a line, using the current color, between the points (x1, y1) and (x2, y2) in this graphics context's coordinate system.
		 */
		public void drawLine(int x1, int y1, int x2, int y2){
			final Path p = new Path();
			p.moveTo(x1, y1);
			p.lineTo(x2, y2);
			mCanvas.drawPath(p, mOutline);
		}

		/**
		 * Draws the outline of the specified rectangle. 
		 * The left and right edges of the rectangle are at x and x + width. 
		 * The top and bottom edges are at y and y + height. 
		 * The rectangle is drawn using the graphics context's current color. 
		 */
		public void drawRect(int x, int y, int width, int height){
			mCanvas.drawRect(new Rect(x, y, x+width, y+height), mOutline);
		}
		
		/**
		 * Draws an outlined round-cornered rectangle using this graphics context's current color. 
		 * The left and right edges of the rectangle are at x and x + width, respectively. 
		 * The top and bottom edges of the rectangle are at y and y + height. 
		 */
		public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight){
			mCanvas.drawRoundRect(new RectF(x, y, x+width, y+height), arcWidth, arcHeight, mOutline);
		}
		
		/**
		 * Draws an oval and fills it in with the current color
		 */
		public void fillOval(int x, int y, int w, int h){
			mCanvas.drawOval(new RectF(x, y, x + w, y + h), mFill);
		}
		
		/**
		 * Fills the specified rectangle. The left and right edges of the rectangle are at x and x + width. 
		 * The top and bottom edges are at y and y + height. 
		 * The resulting rectangle covers an area width pixels wide by height pixels tall. 
		 * The rectangle is filled using the graphics context's current color.
		 */
		public void fillRect(int x, int y, int width, int height){
			mCanvas.drawRect(new Rect(x, y, x+width, y+height), mFill);
		}
		
		/**
		 * Draws an filled round-cornered rectangle using this graphics context's current color. 
		 * The left and right edges of the rectangle are at x and x + width, respectively. 
		 * The top and bottom edges of the rectangle are at y and y + height. 
		 */
		public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight){
			mCanvas.drawRoundRect(new RectF(x, y, x+width, y+height), arcWidth, arcHeight, mFill);
		}
		
		/**
		 * Draws the fill of a circular or elliptical arc covering the specified rectangle.
			A positive value indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation. <br>
			<b>note: this behavior is backwards compared to Android's normal drawArc API which draws in a clockwise rotation.</b>
		 */
		public void fillArc(int x, int y, int width, int height, int startAngle, int sweep){
			mCanvas.drawArc(new RectF(x, y, x + width, y + height), startAngle, -sweep, false, mFill);
		}
		
		/**
		 * @param color select colors easily by
		 * the {@link Color} class static variables. <br>
		 * for example setColor(Color.RED);<br>
		 * This is the same API as swing except for in swing the static variable 
		 * is a class named Color rather than an int. <br>
		 * You can also use a HEX value 0xAARRGGBB the AA being alpha, to get the color
		 * red you would use setColor(0x00FF0000);<br>
		 */
		public void setColor(int color){
			mOutline.setColor(color);
			mFill.setColor(color);
		}
		
	}   
}