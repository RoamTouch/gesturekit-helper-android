package com.roamtouch.gesturekit.gesturekithelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;



import com.roamotuch.gesturekit.plugin.GKActionInterface;
import com.roamtouch.gesturekit.GestureKit;
import com.roamtouch.gesturekit.helper.R;

public class GestureActionHelpPopUp implements GKActionInterface {

	private boolean initialized = false;

	//private View view;
	
	private int gridViewId = 0;
	private int closeButtonId;
	private JSONArray help_array;
	private String help_array_string;
	
	private Activity activity;
	public static LinearLayout gesturesView;
	private GridView gridView;
	private Button closeHelpButton;
	private GestureKit gk;
	private Animation a;
	private String actionName;
	private static int flag = 0;
	public static int w;
	private static CountDownTimer c;
	private static Context context;
	private static int ANIMFLAG = 0;
	public GestureActionHelpPopUp(Context context, GestureKit gk, String actionName){
		
		this.activity = (Activity) context;
		this.gk = gk;	
		
		this.actionName = actionName;
		this.context = context;
		this.help_array_string = gk.getHelpString();	
		
		try {
			this.help_array = new JSONArray(help_array_string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public String getActionID() {
		return actionName;
	}

	@Override
	public void onGestureRecognized(Object... params) {
		try {
			loadHelp();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void loadHelp() throws JSONException{		
		if(!initialized){
			initSubviews();
		} else {
			try
			{
				
				int width = gk.getScreenWidth();
				int height = gk.getScreenHeight();
				
				int orientation = width>height? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				int cols = orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? 4 : 3;
				int gridPaddingX = 10;
				int colWidth = (int)(width/(float)cols) - gridPaddingX;
				if(this.help_array.length() >=3)
				{
					w=colWidth * 3;
				gridView.setNumColumns(3);
				}
					else
					{
						w=colWidth * this.help_array.length();
						gridView.setNumColumns(this.help_array.length());
					}
				activity.runOnUiThread(new Runnable() {		 
					@SuppressLint("NewApi")
					@Override
					public void run() { 
						gesturesView.setVisibility(View.INVISIBLE);
						ANIMFLAG =0;
						resetGridView();
						gesturesView.getContext().sendBroadcast(new Intent(GestureKit.ACTION_PLUGIN).putExtra("visor_action", "show_visor"));	
						gesturesView.setY((int)GestureKitHelper.rect.top);
						Log.e("left", GestureKitHelper.rect.left+"");
						if(GestureKitHelper.rect.left < 100)
						{
							
						expandHiddenPanelLeftToRight(gesturesView, true);
						gesturesView.setPadding((int)GestureKitHelper.rect.width(), 0, 0, 0);
						float diff =GestureKitHelper.rect.right -GestureKitHelper.rect.left ;
						 GestureKitHelper.rect.left = 0;
						 GestureKitHelper.rect.right = diff;
						}
						else
						{
							
							
							expandHiddenPanelRightToLeft(gesturesView, true);
							gesturesView.setPadding(0, 0, (int)GestureKitHelper.rect.width(), 0);
							 float diff =GestureKitHelper.rect.right -GestureKitHelper.rect.left ;
							 GestureKitHelper.rect.left = gk.getScreenWidth()-diff;
							 GestureKitHelper.rect.right =gk.getScreenWidth();
						}
					}
		        });	 							
				
			}  catch (Exception e){
				   Log.v("", "" + e.toString());
			 }
		}
	}
	public void resetGridView()
	{
		final int size = gridView.getChildCount();
		for(int i = 0; i < size; i++) {
		  ViewGroup gridChild = (ViewGroup) gridView.getChildAt(i);
		  int childSize = gridChild.getChildCount();
		  for(int k = 0; k < childSize; k++) {
		    if( gridChild.getChildAt(k) instanceof TextView ) {
		      gridChild.getChildAt(k).setVisibility(View.GONE);
		    }
		    if( gridChild.getChildAt(k) instanceof ImageView ) {
			      gridChild.getChildAt(k).setVisibility(View.VISIBLE);
			    }
		   
		  }
		}
	}
	@SuppressLint("NewApi")
	private void initSubviews()throws JSONException {		
		
		gesturesView = new LinearLayout(this.activity);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		gesturesView.setLayoutParams(lp);
		gesturesView.setOrientation(LinearLayout.VERTICAL);
		//gesturesView.setBackgroundColor(this.activity.getResources().getColor(android.R.color.transparent));
		
		//gesturesView.setBackgroundResource(R.drawable.rounded_corner);
		
		
		gridView = new GridView(activity);	
		int width = gk.getScreenWidth();
		int height = gk.getScreenHeight();
		
		int orientation = width>height? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		int cols = orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? 4 : 3;
		int textNameSize = width<height? width / 40 : height / 40;
		int gridPaddingX = 10;
		int gridPaddingY = 10;
		int colWidth = (int)(width/(float)cols) - gridPaddingX;
		
		//@SuppressWarnings("deprecation")
		//LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		
		//layout_params.weight = 1;
		
		
		gridView.setLayoutParams(layout_params);
		//gridView.setBackgroundColor(Color.parseColor("#b9b9b9"));	
		
		
		//gridView.setColumnWidth(GridView.AUTO_FIT);
		gridView.setVerticalSpacing(5);
		gridView.setHorizontalSpacing(5);
		//gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setGravity(Gravity.CENTER);	
		//gridView.setPadding(gridPaddingX, gridPaddingY, gridPaddingX, gridPaddingY);
		gridView.setSelector(android.R.color.transparent);
		gridView.setClickable(true);
		
		gesturesView.setVisibility(View.INVISIBLE);
		gridView.setAdapter(new HelpAdapter(gridView.getContext(), this.help_array, colWidth, textNameSize));
		if(this.help_array.length() >=3)
		{
			w=colWidth * 3;
		gridView.setNumColumns(3);
		}
			else
			{
				w=colWidth * this.help_array.length();
				gridView.setNumColumns(this.help_array.length());
			}
		gesturesView.addView(gridView);
		
		closeHelpButton = new Button(activity);
		closeHelpButton.setText("CLOSE");
		closeHelpButton.setVisibility(View.GONE);
		gesturesView.addView(closeHelpButton);
		gesturesView.setY((int)GestureKitHelper.rect.top);
		 
		gesturesView.setBackgroundResource(R.drawable.rounded_corner);
		//gesturesView.setAlpha(1.0F);
		gridView.getViewTreeObserver().addOnGlobalLayoutListener(
		    new ViewTreeObserver.OnGlobalLayoutListener() {
		    	@Override
		        public void onGlobalLayout() {	
		        	int[] grid_location = new int[4];
		        	grid_location[0] = gridView.getPaddingLeft();
		    		grid_location[1] = gridView.getPaddingTop();	    		
		    		grid_location[2] = gridView.getPaddingRight();
		    		grid_location[3] = gridView.getPaddingBottom();		    		    		 
		        }
	    });
		Log.e("left", GestureKitHelper.rect.left+"");
		if(GestureKitHelper.rect.left < 100)
		{
			ANIMFLAG =0;
			resetGridView();
		expandHiddenPanelLeftToRight(gesturesView, true);
		gesturesView.setPadding((int)GestureKitHelper.rect.width(), 0, 0, 0);
		float diff =GestureKitHelper.rect.right -GestureKitHelper.rect.left ;
		 GestureKitHelper.rect.left = 0;
		 GestureKitHelper.rect.right = diff;
		}
		else
		{
			ANIMFLAG =0;
			resetGridView();
			expandHiddenPanelRightToLeft(gesturesView, true);
			gesturesView.setPadding(0, 0, (int)GestureKitHelper.rect.width(), 0);
			 float diff =GestureKitHelper.rect.right -GestureKitHelper.rect.left ;
			 GestureKitHelper.rect.left = gk.getScreenWidth()-diff;
			 GestureKitHelper.rect.right =gk.getScreenWidth();
		}
		//View btnClose = view.findViewById(this.closeButtonId);
		
		closeHelpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gesturesView.setVisibility(View.GONE);
				//gk.getWindowsManager().removeView(gesturesView);	
				gesturesView.getContext().sendBroadcast(new Intent(GestureKit.ACTION_PLUGIN).putExtra("visor_action", "show_visor"));
			}
		});
		
		//gk.onPause();		
		try {
					
			activity.runOnUiThread(new Runnable() {		 
				@Override
				public void run() { 
					//gesturesView.setBackgroundColor(Color.parseColor("#b9b9b9"));
					gk.addView(gesturesView);
				 }
	        });	 			
				
		}  catch (Exception e){
			   Log.v("", "" + e.toString());
		 }
		
		initialized = true;
	}
	 @SuppressLint("NewApi")
	public Animation expandHiddenPanelLeftToRight(final View v, final boolean expand) {
	     boolean   panelExpanded = expand;
	        v.measure(MeasureSpec.makeMeasureSpec(500, MeasureSpec.EXACTLY), 
	                  MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	        GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
	        Log.e("Invisible6",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	        final int initialWidth = w; //gk.getScreenWidth(); //v.getMeasuredWidth();
	        v.setX(0);
	       if(a!= null)
	    	   v.clearAnimation();
	        a = new Animation() {
	            @Override
	            protected void applyTransformation(float interpolatedTime, Transformation t) {
	                int newWidth;
	                if(GestureActionHelpPopUp.gesturesView != null && ANIMFLAG != 8)
	                {
	                	
	        			GestureActionHelpPopUp.gesturesView.setVisibility(View.VISIBLE);
	        			Log.e("visible1",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                }
	                else
	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.GONE);
	                Log.e("Invisible7",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                if (expand) {
	                    newWidth = (int)(initialWidth * interpolatedTime);
	                    if(interpolatedTime  == 1.0)
	                {
	                    	if(c != null)
	                    		c.cancel();
	                    	c = new CountDownTimer(5000,1000) {
							
							@Override
							public void onTick(long millisUntilFinished) {
								// TODO Auto-generated method stub
								if(GestureActionHelpPopUp.gesturesView.getVisibility() == View.VISIBLE)
 								{
 									
									ANIMFLAG = 0;
 								}
								else
								{
									 GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
									 Log.e("Invisible8",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
									ANIMFLAG = 8;
								}
							}
							
							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
								Log.e("Invisible9",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
								expandHiddenPanelLeftToRight(gesturesView, false);
 								
							}
						};
						c.start();
	                }
	                    
	                }
	                else {
	                	if(GestureActionHelpPopUp.gesturesView != null && ANIMFLAG != 8)
	 	                {
	 	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.VISIBLE);
	 	                	Log.e("visible2",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	 	                }
	 	                else
	 	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.GONE);
	                	Log.e("Invisible10",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                    newWidth = (int)(initialWidth * (1 - interpolatedTime));
	                   
	                }

	                v.getLayoutParams().width = newWidth;
	                v.requestLayout();              
	            }

	            @Override
	            public boolean willChangeBounds() {
	                return true;
	            }
	        };
	        v.requestLayout();
	        a.setInterpolator(new AccelerateInterpolator());
	        a.setDuration(200);
	        v.startAnimation(a);

	        return a;
	    }
	
	 public Animation expandHiddenPanelRightToLeft(final View v, final boolean expand) {
	     boolean   panelExpanded = expand;
	        v.measure(MeasureSpec.makeMeasureSpec(500, MeasureSpec.EXACTLY), 
	                  MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	        GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
	        Log.e("Invisible1",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	        final int initialWidth = w;//gk.getScreenWidth(); //v.getMeasuredWidth();
	       
	        //v.getLayoutParams().width = 0;
	        //v.setVisibility(View.VISIBLE);
	       
	        a = new Animation() {
	            @SuppressLint("NewApi")
				@Override
	            protected void applyTransformation(float interpolatedTime, Transformation t) {
	            	if(GestureActionHelpPopUp.gesturesView != null && ANIMFLAG != 8)
	                {
	                	
	        			GestureActionHelpPopUp.gesturesView.setVisibility(View.VISIBLE);
	        			Log.e("visible3",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                }
	                else
	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.GONE);
	            	Log.e("Invisible2",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                int newWidth;
	                float pos = initialWidth; // 320
	                if (expand) {
	                	 newWidth = (int)(initialWidth * (interpolatedTime));
	                	 //pos = (int)(initialWidth * (1 - interpolatedTime));
	                	 pos = (int)(gk.getScreenWidth()-newWidth);
	                	 v.setX(pos);
	                	 
	        			 
	                	 if(interpolatedTime  == 1.0)
	 	                {c = new CountDownTimer(5000,1000) {
	 							
	 							@Override
	 							public void onTick(long millisUntilFinished) {
	 								// TODO Auto-generated method stub
	 								if(GestureActionHelpPopUp.gesturesView.getVisibility() == View.VISIBLE)
	 								{
	 									
										ANIMFLAG = 0;
	 								}
									else
									{
										 GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
										 Log.e("Invisible3",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
										ANIMFLAG = 8;
									}
	 							}
	 							
	 							@Override
	 							public void onFinish() {
	 								// TODO Auto-generated method stub
	 								
	 								/*a.cancel();
	 								a= null;*/
	 								 GestureActionHelpPopUp.gesturesView.setVisibility(View.INVISIBLE);
	 								Log.e("Invisible4",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	 								expandHiddenPanelRightToLeft(gesturesView, false);
	 								
	 								
	 							}
	 						};
	 						c.start();
	 	                }
	                   /* newWidth = (int)(initialWidth * interpolatedTime);
	                    pos = initialWidth;
	                    Log.i("test", "new Width = " + newWidth);*/
	                }
	                else {
	                	if(GestureActionHelpPopUp.gesturesView != null && ANIMFLAG != 8)
	 	                {
	 	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.VISIBLE);
	 	                	Log.e("visible4",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	 	                }
	 	                else
	 	                	GestureActionHelpPopUp.gesturesView.setVisibility(View.GONE);
	                	Log.e("Invisible5",GestureActionHelpPopUp.gesturesView.getVisibility() +"");
	                	 newWidth =(int)(initialWidth * (1 - interpolatedTime)); 
	                	// pos = (int)(initialWidth * (interpolatedTime));
	                	 pos = (int)(gk.getScreenWidth()-newWidth);
	                     v.setX(pos);
	                   
	                }
	                v.requestLayout();
	                
	                v.getLayoutParams().width = newWidth;
	                
	                           
	            }

	            @Override
	            public boolean willChangeBounds() {
	                return true;
	            }
	        };

	        a.setInterpolator(new AccelerateInterpolator());
	        a.setDuration(500);
	        v.requestLayout();
	        v.startAnimation(a);
	        v.requestLayout();
	        return a;
	    }
	
	 
	 public Bitmap getGestureImage(String gestureName){
		for(int i = 0; i < help_array.length(); i++){			
			try {			
				JSONObject gesture = help_array.getJSONObject(i);
				String cGestureName = gesture.optString("name");
				
				if (cGestureName.equals(gestureName)) {
					String gesture_image = gesture.getString("image");
					
					byte[] decodedString = Base64.decode(gesture_image, Base64.DEFAULT);
					Bitmap b = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	
					return b;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}
		
		return null;
	}
	
	
	public class HelpAdapter extends BaseAdapter {
	     
		  int array_length;
		  
		  private LinearLayout[] layouts;
		  private Bitmap[] bitmaps;
		  private String[] gesture_methods;
		  private String[] gesture_descriptions;
		    
		  private Context context;  	  
		  private int _textNameSize;
		  private int _imageSize;
		  
		  HelpAdapter(Context c, JSONArray help_array, int imageSize, int textNameSize) throws JSONException{
			  
			  this._textNameSize = textNameSize;
			  this._imageSize = imageSize;
			  
			  this.context = c;	
			  
			  this.array_length = help_array.length();
			  
			  this.layouts = new LinearLayout[array_length];
			  
			  this.bitmaps = new Bitmap[array_length];
			  this.gesture_methods = new String[array_length];
			  this.gesture_descriptions = new String[array_length];
		     	 
			  for(int i = 0; i < help_array.length(); i++){
				  
				  JSONObject gesture = help_array.getJSONObject(i);
				  String gesture_image = gesture.getString("image");
				  
				  byte[] decodedString = Base64.decode(gesture_image, Base64.DEFAULT);
				  bitmaps[i] = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				  
				  String gesture_method = gesture.getString("name");
				  gesture_methods[i] = gesture_method;
				  
				  String gesture_description = gesture.getString("description");
				  gesture_descriptions[i] = gesture_description;
				  
			  }
		  }
		  
		  @Override
		  public int getCount() {	   
			  return array_length;
		  }
		  
		  @Override
		  public Object getItem(int position) {
			  return layouts[position];
		  }
		  
		  @Override
		  public long getItemId(int position) {	   
			  return position;
		  }
		  
		 
		@SuppressWarnings("deprecation")
		@Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  		 
			  if(convertView==null){
				  
				  layouts[position] = new LinearLayout(context); 
				  layouts[position].setOrientation(LinearLayout.VERTICAL);
				  layouts[position].setClickable(true);
				  layouts[position].setMinimumHeight((int) GestureKitHelper.rect.height());
				  /*layouts[position].setBackgroundColor(Color.RED);
				  */
				  final Holder holder = new Holder();
				  holder.methodTextView = new TextView(context);
				
				  //RoundRectShape rs = new RoundRectShape(new float[] { 10, 10, 10, 10, 10, 10, 10, 10 }, null, null);			  
				  //ShapeDrawable sdOn = new CustomShapeDrawable(rs, 0xff8e8e93, Color.TRANSPARENT, 3);
				  //StateListDrawable stld = new StateListDrawable();			 
				  //stld.addState(new int[] { android.R.attr.state_pressed }, sdOn);
				  //layouts[position].setBackgroundDrawable(sdOn);
				  layouts[position].setClickable(true);
				
						

						
				  holder.imageView = new ImageView(context);
				  holder.imageView.setImageBitmap(bitmaps[position]);
				  
				  LinearLayout.LayoutParams lp =  new LinearLayout.LayoutParams((int) GestureKitHelper.rect.width(),(int) GestureKitHelper.rect.height());
				  
				  holder.imageView.setLayoutParams(lp);
				 
				  
				  
				  holder.descTextView = new TextView(context);
				  //imageView.setScaleType(ScaleType.MATRIX);
				  //imageView.setBackground(sdOn);
				  holder.imageView.setClickable(false);
				  /*LinearLayout.LayoutParams lp =  new LinearLayout.LayoutParams(this._imageSize,this._imageSize,Gravity.CENTER | Gravity.BOTTOM);
				  imageView.setLayoutParams(lp);
				  lp.setMargins(0, (int)GestureKitHelper.rect.top, 0, 0);*/
				  
				  layouts[position].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						if(holder.imageView.getVisibility() == View.VISIBLE)
						{
							holder.imageView.setVisibility(View.GONE);
							holder.methodTextView.setVisibility(View.VISIBLE);
							holder.descTextView.setVisibility(View.VISIBLE);
						}
						else if(holder.imageView.getVisibility() == View.GONE)
						{
							holder.imageView.setVisibility(View.VISIBLE);
							
							holder.methodTextView.setVisibility(View.GONE);
							holder.descTextView.setVisibility(View.GONE);
						}
					}
				});
				  layouts[position].addView(holder.imageView);
				 
				  holder.methodTextView.setGravity(Gravity.CENTER);
				  String methodText = gesture_methods[position];
				  holder.methodTextView.setTextSize(this._textNameSize);
				  holder.methodTextView.setText(methodText);
				  holder.methodTextView.setVisibility(View.GONE);
				  holder.methodTextView.setTextColor(Color.WHITE);
				  layouts[position].addView(holder.methodTextView);			  
				  
				 
				  String descText = gesture_descriptions[position];
				  holder.descTextView.setGravity(Gravity.CENTER);
				  holder.descTextView.setMaxLines(1);
				  holder.descTextView.setText(descText);
				  holder.descTextView.setVisibility(View.GONE);
				  holder.descTextView.setTextColor(Color.WHITE);
				  layouts[position].addView(holder.descTextView);
			  }
			  else
				  layouts[position] = (LinearLayout) convertView;
			  		 
			  return layouts[position];
			  		 
		   }
		  private class Holder
		  {
			  TextView methodTextView;
			  ImageView imageView;
			  TextView descTextView;
		  }
		  public class CustomShapeDrawable extends ShapeDrawable {
			    private final Paint fillpaint, strokepaint;
			 
			    public CustomShapeDrawable(Shape s, int fill, int stroke, int strokeWidth) {
			        super(s);
			        fillpaint = new Paint(this.getPaint());
			        fillpaint.setColor(fill);
			        strokepaint = new Paint(fillpaint);
			        strokepaint.setStyle(Paint.Style.STROKE);
			        strokepaint.setStrokeWidth(strokeWidth);
			        strokepaint.setColor(stroke);
			    }
			 
			    @Override
			    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
			        shape.draw(canvas, fillpaint);
			        shape.draw(canvas, strokepaint);
			    }
			}	  
		  
	 }


	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
