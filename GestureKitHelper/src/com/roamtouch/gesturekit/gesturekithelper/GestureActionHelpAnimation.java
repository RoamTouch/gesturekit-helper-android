package com.roamtouch.gesturekit.gesturekithelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;




import com.roamotuch.gesturekit.plugin.GKActionInterface;
import com.roamtouch.gesturekit.GestureKit;

public class GestureActionHelpAnimation implements GKActionInterface {

	private boolean initialized = false;

	//private View view;
	
	private int gridViewId = 0;
	private int closeButtonId;
	private JSONArray help_array;
	private String help_array_string;
	
	private Activity activity;
	private LinearLayout gesturesView;
	private GridView gridView;
	private Button closeHelpButton;
	private GestureKit gk;
	
	public GestureActionHelpAnimation(Context context, /*View view, int gridId, int closeButtonId,*/ GestureKit gk){
		
		this.activity = (Activity) context;
		this.gk = gk;
		
		//this.view = view;
		
		this.help_array_string = gk.getHelpString();
		
		//this.gridViewId = gridId;
		//this.closeButtonId = closeButtonId;
		
		try {
			this.help_array = new JSONArray(help_array_string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getActionID() {
		return "HELP";
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
			gesturesView.setVisibility(View.VISIBLE);		
			gesturesView.getContext().sendBroadcast(new Intent(GestureKit.ACTION_PLUGIN).putExtra("visor_action", "hide_visor"));	
		}
	}
	
	private void initSubviews()throws JSONException {		
		
		gesturesView = new LinearLayout(this.activity);
		gesturesView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		gesturesView.setOrientation(LinearLayout.VERTICAL);
		gesturesView.setWeightSum(1.1f);		
		
		
		gridView = new GridView(activity);	
					
		@SuppressWarnings("deprecation")
		int width = gk.getWindowsManager().getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = gk.getWindowsManager().getDefaultDisplay().getHeight();		
		int orientation = width>height? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		int cols = orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? 4 : 3;
		int textNameSize = width<height? width / 50 : height / 50;
		int gridPaddingX = 10;
		int gridPaddingY = 10;
		int colWidth = (int)(width/(float)cols) - gridPaddingX;
		
		//@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		layout_params.weight = 1;
		
		gridView.setLayoutParams(layout_params);
				
		gridView.setNumColumns(cols);
		gridView.setColumnWidth(GridView.AUTO_FIT);
		gridView.setVerticalSpacing(5);
		gridView.setHorizontalSpacing(5);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setGravity(Gravity.CENTER);	
		gridView.setPadding(gridPaddingX, gridPaddingY, gridPaddingX, gridPaddingY);
		gridView.setSelector(R.color.transparent);
		gridView.setClickable(false);
		gridView.setAdapter(new HelpAdapter(gridView.getContext(), this.help_array, colWidth, textNameSize));
		
		gesturesView.addView(gridView);
		
		closeHelpButton = new Button(activity);
		closeHelpButton.setText("CLOSE");
		gesturesView.addView(closeHelpButton);
		
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
		gk.getWindowsManager().addView(gesturesView, gk.getLayoutParams());	
		gesturesView.setBackgroundColor(Color.parseColor("#000000"));
		
		initialized = true;
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
				  layouts[position].setClickable(false);
				  
				  //RoundRectShape rs = new RoundRectShape(new float[] { 10, 10, 10, 10, 10, 10, 10, 10 }, null, null);			  
				  //ShapeDrawable sdOn = new CustomShapeDrawable(rs, 0xff8e8e93, Color.TRANSPARENT, 3);
				  //StateListDrawable stld = new StateListDrawable();			 
				  //stld.addState(new int[] { android.R.attr.state_pressed }, sdOn);
				  //layouts[position].setBackgroundDrawable(sdOn);
				  
				  ImageView imageView = new ImageView(context);
				  imageView.setImageBitmap(bitmaps[position]);	
				  //imageView.setBackground(sdOn);
				  imageView.setClickable(false);
				  imageView.setLayoutParams(new LinearLayout.LayoutParams(this._imageSize, this._imageSize, Gravity.CENTER | Gravity.BOTTOM));
				  layouts[position].addView(imageView);
				  
				  TextView methodTextView = new TextView(context);
				  methodTextView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
				  String methodText = gesture_methods[position];
				  methodTextView.setTextSize(this._textNameSize);
				  methodTextView.setText(methodText);	
				  layouts[position].addView(methodTextView);			  
				  
				  TextView descTextView = new TextView(context);
				  String descText = gesture_descriptions[position];
				  descTextView.setGravity(Gravity.CENTER | Gravity.BOTTOM);
				  descTextView.setMaxLines(1);
				  descTextView.setText(descText);
				  layouts[position].addView(descTextView);
			  }
			  else
				  layouts[position] = (LinearLayout) convertView;
			  
			  return layouts[position];
			  
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
