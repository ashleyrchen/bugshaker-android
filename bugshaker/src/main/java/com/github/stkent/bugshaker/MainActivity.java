package com.github.stkent.bugshaker;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.stkent.bugshaker.flow.email.EmailCapabilitiesProvider;
import com.github.stkent.bugshaker.flow.email.FeedbackEmailFlowManager;
import com.github.stkent.bugshaker.flow.email.FeedbackEmailIntentProvider;
import com.github.stkent.bugshaker.flow.email.GenericEmailIntentProvider;
import com.github.stkent.bugshaker.flow.widget.DrawingView;
import com.github.stkent.bugshaker.utilities.LogcatUtil;
import com.github.stkent.bugshaker.utilities.Logger;
import com.github.stkent.bugshaker.utilities.Toaster;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	private DrawingView drawView;

	private FeedbackEmailFlowManager feedbackEmailFlowManager;
	public static final String PREFS_NAME3 = "name3";
	public static final String PREFS_KEY3 = "key3";

	private Activity activity;

	private float smallBrush, mediumBrush, largeBrush, smallEraser, mediumEraser, largeEraser;
	private ImageButton currPaint;
	public LinearLayout linearLayout;
	EditText editText;
	float dX, dY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		linearLayout = (LinearLayout) this.findViewById(R.id.ll);
		activity = this;

		drawView = (DrawingView)findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		smallEraser = getResources().getInteger(R.integer.small_eraser_size);
		mediumEraser = getResources().getInteger(R.integer.medium_eraser_size);
		largeEraser = getResources().getInteger(R.integer.large_eraser_size);

		ImageButton drawBtn = (ImageButton) findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);

		drawView.setBrushSize(smallBrush);

		ImageButton eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);

		ImageButton sendBtn = (ImageButton) findViewById(R.id.sendEmail);
		sendBtn.setOnClickListener(this);

		ImageButton speechBtn = (ImageButton) findViewById(R.id.speechBox);
		speechBtn.setOnClickListener(this);

		editText = (EditText) findViewById(R.id.textEditing);
		editText.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					dX = view.getX() - event.getRawX();
					dY = view.getY() - event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:

					view.animate()
						.x(event.getRawX() + dX)
						.y(event.getRawY() + dY)
						.setDuration(0)
						.start();

					break;
				default:
					return false;
				}
				return true;
			}

		});

		final GenericEmailIntentProvider genericEmailIntentProvider
			= new GenericEmailIntentProvider();

		Logger logger = new Logger(true);
		Application application = getApplication();

		EmailCapabilitiesProvider emailCapabilitiesProvider = new EmailCapabilitiesProvider(
			application.getPackageManager(), genericEmailIntentProvider, logger);

		feedbackEmailFlowManager = new FeedbackEmailFlowManager(
			application,
			emailCapabilitiesProvider,
			new Toaster(application),
			new ActivityReferenceManager(),
			new FeedbackEmailIntentProvider(application, genericEmailIntentProvider));

		String pathOfScreenshot = getIntent().getStringExtra("uri");
		File screenshotFile = new File(pathOfScreenshot);
		if(screenshotFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(pathOfScreenshot);

			drawView = (DrawingView) findViewById(R.id.drawing);
			Drawable temp = new BitmapDrawable(getResources(), myBitmap);
			drawView.setBackgroundDrawable(temp);
		}
		else {
			throw new RuntimeException();
		}
	}

	@Override
	public void onClick(View view){
		//respond to clicks
		if(view.getId()==R.id.draw_btn){
			//draw button clicked
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle(getApplicationContext().getString(R.string.brush_dialog_title));
			brushDialog.setContentView(R.layout.brush_chooser);

			ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setBrushSize(smallBrush);
					drawView.setLastBrushSize(smallBrush);
					drawView.setErase(false);
					brushDialog.dismiss();
				}
			});

			ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setBrushSize(mediumBrush);
					drawView.setLastBrushSize(mediumBrush);
					drawView.setErase(false);
					brushDialog.dismiss();
				}
			});

			ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setBrushSize(largeBrush);
					drawView.setLastBrushSize(largeBrush);
					drawView.setErase(false);
					brushDialog.dismiss();
				}
			});

			brushDialog.show();

		} else if (view.getId() == R.id.erase_btn){
			//switch to erase, choose size
			final Dialog brushDialog = new Dialog(this);
			brushDialog.setTitle(getApplicationContext().getString(R.string.brush_dialog_title));
			brushDialog.setContentView(R.layout.brush_chooser);
			ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setErase(true);
					drawView.setBrushSize(smallEraser);
					brushDialog.dismiss();
				}
			});

			ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumEraser);
					brushDialog.dismiss();
				}
			});

			ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setErase(true);
					drawView.setBrushSize(largeEraser);
					brushDialog.dismiss();
				}
			});
			brushDialog.show();
		}

		else if (view.getId()==R.id.sendEmail){
			saveDrawing();
		}
		else if (view.getId()==R.id.speechBox){
			editText.setVisibility(View.VISIBLE);
			editText.setText("Enter text here");

//			LinearLayout container = (LinearLayout) findViewById(R.id.ll);
//			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			final View addView = layoutInflater.inflate(R.layout.activity_main, null);
//			TextView textView = (TextView) addView.findViewById(R.id.textEditing);
//				textView.setText("TESTING");
//
//
//			save(getBaseContext(), true);
		}
	}

	public void save(Context context, boolean isTextButtonPressed){
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME3, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREFS_KEY3, isTextButtonPressed);
		editor.commit();
	}

//	private TextView createNewTextView(String text) {
//		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(5,5);
//		final TextView textView = new TextView(getApplicationContext());
//		textView.setLayoutParams(layoutParams);
//		textView.setText("New text Edit: " + text);
//		return textView;
//	}

	private void createTextEdit(){
		//get x and y

		View.OnTouchListener onTouchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				float x = motionEvent.getX();
				float y = motionEvent.getY();

				final EditText editText;
				editText = new EditText(getApplicationContext());
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
				ll.addView(editText);

				//editText = (EditText)findViewById(R.id.editText);
				//drawView.add(editText);

				return false;
			}
		};


	}

	public void paintClicked(View view){
		//use chosen color
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			//update color
			ImageButton imgView = (ImageButton)view;
			String clickedButtonColor = view.getTag().toString();
			drawView.setColor(clickedButtonColor);

			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	 }

	private Uri getImageUri(String path, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		return Uri.parse(path);
	}

	private void saveDrawing() {
		AlertDialog.Builder sendDialog = new AlertDialog.Builder(this);

		sendDialog.setTitle(getApplicationContext().getString(R.string.send_annotated_screenshot));
		sendDialog.setMessage(getApplicationContext().getString(R.string.attach_annotated_screenshot_to_email));
		sendDialog.setPositiveButton(getApplicationContext().getString(R.string.yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				drawView.setDrawingCacheEnabled(true);
				drawView.buildDrawingCache();
				Bitmap bm = drawView.getDrawingCache();

				EditText editText = (EditText) findViewById(R.id.textEditing);
				editText.setDrawingCacheEnabled(true);
				editText.buildDrawingCache();
				float x = editText.getX();
				float y = editText.getY();
				Bitmap bm2 = editText.getDrawingCache();

				Bitmap screenshotBitmap = drawView.combineImages(bm, bm2, x, y);

				//Saves image in phone Gallery
				String imgSaved = MediaStore.Images.Media.insertImage(
					getContentResolver(), screenshotBitmap,
					ScreenshotUtil.getImageFileName(), ScreenshotUtil.getImageDescription());

				Uri bitmapUri = getImageUri(imgSaved, screenshotBitmap);

				if (imgSaved != null) {
					Toast savedToast = Toast.makeText(getApplicationContext(),
						"Drawing saved to Gallery!", Toast.LENGTH_SHORT);


					File log = LogcatUtil.saveLogcatToFile(getApplicationContext());
					Uri logUri = Uri.fromFile(log);
					feedbackEmailFlowManager.sendEmailWithScreenshot(activity, bitmapUri, logUri);

					savedToast.show();


				} else {
					Toast unsavedToast = Toast.makeText(getApplicationContext(),
						"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
					unsavedToast.show();
				}

				drawView.destroyDrawingCache();
				File file = new File(bitmapUri.getPath());
				File deleteFile = new File (file.getAbsolutePath());

				if(deleteFile.exists())
					deleteFile.delete();
			}
		});
		sendDialog.setNegativeButton(getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				dialog.cancel();
			}
		});
		sendDialog.show();

	}
}