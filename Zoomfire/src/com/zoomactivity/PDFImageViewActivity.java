package com.zoomactivity;

import java.io.IOException;
import android.view.KeyEvent;

import com.zoomifier.imagedownload.PDImagedownloader;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zoomactivity.PDFImageViewActivity.LikeDocument;
import com.zoomactivity.PDFImageViewActivity.SendReview;
import com.zoomactivity.PDFImageViewActivity.ShareEmail;
import com.zoomactivity.PDFImageViewActivity.SharePdf;
import com.zoomactivity.PDFImageViewActivity.UnLikeDocumentDocument;
import com.zoomifier.adapter.AgendaTimeAdapNew;
import com.zoomifier.adapter.Book;
import com.zoomifier.adapter.PDFGridViewAdapter;
import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomactivity.*;

public class PDFImageViewActivity extends Activity {
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	public static PointF mid = new PointF();
	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static int mode = NONE;
	public static int i = 0;
	ProgressDialog progressDialog;
	Dialog settingDialog;
	float oldDist;
	int status = 0;
	String documentId, documentName, documentDiscription, documentOrginalId,
			documentContentType, documentContentWidth, documentContentHeight,
			documentOwnerId;
	Animation webViewZoomOutAnimation, webViewZoomInAnimation;
	TextView textHeader, discriptionText;
	Button starButton, sharebuton, heartButton, infoButton, searchbutton,
			redcross_button;
	ZoomifierDatabase database;
	Vector<Book> reviewList = new Vector<Book>();
	Button previousButton, nextButton;
	SeekBar seekbar, mainSeekbar;
	static int curretPostion;
	int position;
	String numberofpage;
	String userid, clientid;
	String shareResponse, retrunResponse;
	View view;
	PDImagedownloader imageloader;
	int numberofpages;
	// ImagePagerAdapter adapter;
	String categoryResponse;
	float pagedevision;
	static int currenpage;
	static int numberofstar;
	String review_time;
	String review_title;
	String revew_text;
	static boolean thumbview;
	static int pagenumber;
	static int pagenumber11;
	ArrayList<Integer> startingpostion = new ArrayList<Integer>();
	ArrayList<Integer> lastpostion = new ArrayList<Integer>();
	ProgressDialog progress;
	ImageView dialogimageView;
	private GalleryViewPager mViewPager;
	UrlPagerAdapter pagerAdapter;
	boolean seekbarTouch;
	int currentProgress;
	ArrayList<String> searchPages = new ArrayList<String>();
	EditText searchEditText;
	String searchDocumentId;
	RelativeLayout transparentlayout;
	TextView sharedBy, header;
	String sharedByName, ShardByDate;
	PageData pagedata;
	boolean documetnlikeactivity;
	public static RelativeLayout headerView, seekbarlayout;
	int horizontalline;
	int show_number_of_page;
	String startTime, endTime, pageStartTime, pageEndTime, searchString;
	int startdocminut, enddocumentminut, startpageminut, endpageminut,
			currentpage, previouspage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdfimageview);
		Bundle bundle = getIntent().getExtras();
		try {
			searchDocumentId = documentId = bundle.getString("documentId");
			documentName = bundle.getString("documentName");
			documentDiscription = bundle.getString("documentDiscription");
			documentOrginalId = bundle.getString("documentOrginalId");
			documentContentType = bundle.getString("documentContentType");
			documentContentWidth = bundle.getString("documentContentWidth");
			documentContentHeight = bundle.getString("documentContentHeight");
			documentOwnerId = bundle.getString("documentOwnerId");
			curretPostion = position = bundle.getInt("position");
			searchString = bundle.getString("searchstring");
			if (searchString == null)
				searchString = " ";
			currenpage = curretPostion;
			userid = bundle.getString("userid");

			clientid = bundle.getString("clientid");
			sharedByName = bundle.getString("sharedby");
			ShardByDate = bundle.getString("shareddate");
			documetnlikeactivity = bundle.getBoolean("lickeactivity");

			Intent intetnt = getIntent();
			pagedata = (PageData) intetnt.getSerializableExtra("pagedata");
			int pagesize = pagedata.page.size();
			init();
			sharedBy.setText("Shared by  " + sharedByName + " on "
					+ ShardByDate);
		
			seekbarTouch = false;
			searchEditText.setText("");
			searchEditText
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							// TODO Auto-generated method stub
							if (actionId == EditorInfo.IME_ACTION_SEARCH) {
								if (!searchEditText.getText().toString()
										.equals("")) {
									new SearchPDF().execute();
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											searchEditText.getWindowToken(), 0);
								}

								return true;
							}
							return false;
						}
					});
			transparentlayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
					heartlayout.setVisibility(View.GONE);
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
					heartlayout.setVisibility(View.GONE);
					layout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				}
			});

			try {
				database.openAndWriteDataBase();
				String like = database
						.openAndReadDocumentLikeUnlike(documentId);
				if (like.equals("0")) {
					heartButton.setBackgroundResource(R.drawable.heart_minus);
				} else if (like.equals("1")) {
					heartButton.setBackgroundResource(R.drawable.heart_plus);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				database.openAndWriteDataBase();
				database.openAndReadPageDocID();
				int exits = 0;
				for (int i = 0; i < ZoomifierDatabase.documentID.size(); i++) {
					if (documentId.equals(ZoomifierDatabase.documentID.get(i))) {
						exits = exits + 1;
						break;

					}
				}
				if (exits > 0) {
					String page = database.openAndReadPageTable(documentId);
					show_number_of_page = numberofpages = Integer
							.parseInt(page);

					List<String> items = new ArrayList<String>();
					int ot = getResources().getConfiguration().orientation;

					if (Configuration.ORIENTATION_PORTRAIT == ot) {

						for (int i = 0; i < numberofpages; i++) {
							String url = "http://ve1.zoomifier.net/" + clientid
									+ "/" + documentId + "/ipad/Page"
									+ Integer.toString(i + 1) + "-1x.jpg";
							items.add(url);
						}
						pagerAdapter = new UrlPagerAdapter(this, items,
								"portriate");

					} else {
						horizontalline = 0;
						if (numberofpages % 2 == 0) {
							for (int i = 0; i < numberofpages + 2; i = i + 2) {
								if (i == 0) {
									String url = " " + ","
											+ "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i + 1)
											+ "-1x.jpg";
									items.add(url);
									horizontalline++;
								} else if (numberofpages == i) {
									String url = "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i) + "-1x.jpg"
											+ "," + " ";
									horizontalline++;
									items.add(url);
								} else {
									String url = "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i) + "-1x.jpg"
											+ "," + "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i + 1)
											+ "-1x.jpg";
									horizontalline++;
									items.add(url);
								}

							}

						} else {
							horizontalline = 0;

							for (int i = 0; i < numberofpages + 1; i = i + 2) {
								if (i == 0) {
									String url = " " + ","
											+ "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i + 1)
											+ "-1x.jpg";
									items.add(url);
									horizontalline++;
								} else {
									String url = "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i) + "-1x.jpg"
											+ "," + "http://ve1.zoomifier.net/"
											+ clientid + "/" + documentId
											+ "/ipad/Page"
											+ Integer.toString(i + 1)
											+ "-2x.jpg";
									horizontalline++;
									items.add(url);
								}

							}

						}

						numberofpages = horizontalline;
						pagerAdapter = new UrlPagerAdapter(this, items,
								"landscape");

					}

					// List<String> urllist=items;
					pagedevision = 100 / numberofpages;
					mViewPager.setOffscreenPageLimit(3);
					mViewPager.setAdapter(pagerAdapter);
					float d = numberofpages - 1;
					final int progresss = 100 / (numberofpages - 1);
					final float po = 100.0f / d;
					try {

						if (numberofpages > 1) {
							drawseekbar();
							if (pagedata.page.size() != 0) {
								for (int i = 0; i < pagedata.page.size(); i++) {
									RelativeLayout relatives = (RelativeLayout) findViewById(R.id.webview_layout11);
									View v = relatives.findViewById(Integer
											.parseInt(pagedata.page.get(i)));
									v.setBackgroundColor(Color.rgb(128, 0, 128));

								}
							}

						}

						pagerAdapter
								.setOnItemChangeListener(new OnItemChangeListener() {
									@Override
									public void onItemChange(int currentPosition) {
										SimpleDateFormat dateFormat = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm:ss");
										Calendar cal = Calendar.getInstance();
										endpageminut = cal.get(Calendar.MINUTE);
										int timeDifference = endpageminut
												- startpageminut;
										if (timeDifference >= 1) {
											pageEndTime = dateFormat.format(cal
													.getTime());
											String timeDiff = Integer
													.toString(timeDifference * 60);
											database.openAndWriteDataBase();
											database.insertIntoPageAnaylytics(
													documentId,
													Integer.toString(currentpage),
													pageStartTime,
													timeDiff,
													"0",
													searchString,
													clientid,
													userid,
													" ",
													" ",
													"DOCUMENT",
													Integer.toString(currentpage),
													userid, "3");
										}

										if (!seekbarTouch) {
											int p1 = (int) (po * currentPosition);
											seekbarTouch = false;
											seekbar.setProgress(p1);
											mainSeekbar
													.setVisibility(View.GONE);

										} else {

											int p1 = (int) (po * currentPosition);

										}
										pageStartTime = dateFormat.format(cal
												.getTime());
										startpageminut = cal
												.get(Calendar.MINUTE);
										currentpage = currentPosition + 1;

									}
								});
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {

					new LoadDocument().execute();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			textHeader.setText(documentName);
			discriptionText.setText(documentDiscription);
		} catch (Exception e) {

		}

		Button button = (Button) findViewById(R.id.menubutton);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Bundle bundle = new Bundle();
					bundle.putString("documentId", documentId);
					bundle.putString("documentName", documentName);
					bundle.putString("documentDiscription", documentDiscription);
					bundle.putString("documentOrginalId", documentOrginalId);
					bundle.putString("documentContentType", documentContentType);
					bundle.putString("documentContentWidth",
							documentContentWidth);
					bundle.putString("documentContentHeight",
							documentContentHeight);
					bundle.putString("documentOwnerId", documentOwnerId);
					bundle.putInt("position", position);
					bundle.putString("clientid", clientid);
					bundle.putString("userid", userid);
					bundle.putInt("noofpage", show_number_of_page);

					// bundle.putString("favorit", book.like_document);

					Intent intent = new Intent(PDFImageViewActivity.this,
							PdfReaderGridView.class);
					if (searchPages.size() != 0) {

						intent.putExtra("pagedata", new PageData(searchPages));
					} else {
						intent.putExtra("pagedata", pagedata);
					}
					intent.putExtras(bundle);
					startActivity(intent);
					// finish();
				} catch (Exception e)

				{
					e.printStackTrace();
				}
				//

			}
		});

		starButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stardialog();
			}
		});
		Button authorizedButton = (Button) findViewById(R.id.authorized_button);
		authorizedButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.email_field);
				if (emailField.getText().toString().equals("")) {
					displayAlert("Please enter email address");
				} else if (!checkEmail(emailField.getText().toString())) {
					displayAlert("Please enter valid email address");
				} else {
					new ShareEmail().execute();
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
					layout.setVisibility(View.GONE);
					emailField.setText("");
				}
			}
		});

		sharebuton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
				heartlayout.setVisibility(View.GONE);
				transparentlayout.setVisibility(View.GONE);
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				if (layout.getVisibility() == View.VISIBLE) {
					layout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					Display display = ((WindowManager) getSystemService(PDFImageViewActivity.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
							|| orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout
								.getLayoutParams();
						params.width = LayoutParams.FILL_PARENT;
						layout.setLayoutParams(params);
					} else {
						int pixel = PDFImageViewActivity.this
								.getWindowManager().getDefaultDisplay()
								.getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout
								.getLayoutParams();
						int wid = pixel / 2;
						params.width = wid;
						layout.setLayoutParams(params);
					}
					layout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);
				}

			}
		});

		heartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				database.openAndWriteDataBase();
				String like = database
						.openAndReadDocumentLikeUnlike(documentId);
				if (like.equals("0")) {
					new LikeDocument().execute();
					/*
					 * database.updateDocumentLikes(documentId);
					 * heartButton.setBackgroundResource(R.drawable.heart_plus);
					 */

				} else if (like.equals("1")) {

					new UnLikeDocumentDocument().execute();
					/*
					 * database.updateDocumentUnlikes(documentId);
					 * heartButton.setBackgroundResource
					 * (R.drawable.heart_minus);
					 */
				}

			}
		});
		infoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

				RelativeLayout infolayout = (RelativeLayout) findViewById(R.id.infodialog);
				if (infolayout.getVisibility() == View.VISIBLE) {
					infolayout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					Display display = ((WindowManager) getSystemService(PDFImageViewActivity.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
							|| orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout
								.getLayoutParams();
						params.width = LayoutParams.FILL_PARENT;
						infolayout.setLayoutParams(params);
					} else {
						int pixel = PDFImageViewActivity.this
								.getWindowManager().getDefaultDisplay()
								.getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout
								.getLayoutParams();
						int wid = pixel / 2;
						params.width = wid;
						infolayout.setLayoutParams(params);
					}
					infolayout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);
				}
			}
		});
		try {
			mViewPager.setCurrentItem(currenpage);
			float d = numberofpages - 1;
			final int progresss = 100 / (numberofpages - 1);
			final float po = 100.0f / d;
			int p4 = (int) (po * currenpage);
			seekbar.setProgress(p4);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void init() {

		textHeader = (TextView) findViewById(R.id.document_header_text);
		starButton = (Button) findViewById(R.id.button1);
		sharebuton = (Button) findViewById(R.id.button2);
		heartButton = (Button) findViewById(R.id.button3);
		infoButton = (Button) findViewById(R.id.button4);
		database = new ZoomifierDatabase(this);
		headerView = (RelativeLayout) findViewById(R.id.popup_header);
		seekbarlayout = (RelativeLayout) findViewById(R.id.seekbar_layout);
		discriptionText = (TextView) findViewById(R.id.discription_field);
		header = (TextView) findViewById(R.id.header_text);
		dialogimageView = (ImageView) findViewById(R.id.dialgoimage);
		searchEditText = (EditText) findViewById(R.id.search_speaker_edit_text);
		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		transparentlayout = (RelativeLayout) findViewById(R.id.transparentlayout);
		sharedBy = (TextView) findViewById(R.id.share_field);

		imageloader = new PDImagedownloader(this);
		searchbutton = (Button) findViewById(R.id.searchbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		redcross_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchEditText.setVisibility(View.GONE);
				redcross_button.setVisibility(View.GONE);
				searchbutton.setVisibility(View.VISIBLE);
				starButton.setVisibility(View.VISIBLE);
				sharebuton.setVisibility(View.VISIBLE);
				heartButton.setVisibility(View.VISIBLE);
				infoButton.setVisibility(View.VISIBLE);
			}
		});
		searchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEditText.setVisibility(View.VISIBLE);
				redcross_button.setVisibility(View.VISIBLE);
				searchbutton.setVisibility(View.GONE);
				starButton.setVisibility(View.GONE);
				sharebuton.setVisibility(View.GONE);
				heartButton.setVisibility(View.GONE);
				infoButton.setVisibility(View.GONE);
			}

		});

	}

	public void settingDialog() {
		settingDialog = new Dialog(PDFImageViewActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settingDialog.setContentView(R.layout.setting_dialog);
		settingDialog.setCanceledOnTouchOutside(false);
		// settingDialog.getWindow().setLayout(300, 600);

	}

	public void stardialog() {
		RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
		heartlayout.setVisibility(View.GONE);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
		layout.setVisibility(View.GONE);
		final Dialog userInfoDialog = new Dialog(PDFImageViewActivity.this);
		userInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		userInfoDialog.setContentView(R.layout.document_review_dialog);
		userInfoDialog.setCanceledOnTouchOutside(false);
		// userInfoDialog.getWindow().setLayout(500, 600);
		userInfoDialog.show();
		final ListView listview = (ListView) userInfoDialog
				.findViewById(R.id.reviewlist);
		reviewList.clear();

		ImageView reImage1 = (ImageView) userInfoDialog
				.findViewById(R.id.restar1);
		ImageView reImage2 = (ImageView) userInfoDialog
				.findViewById(R.id.restar2);
		ImageView reImage3 = (ImageView) userInfoDialog
				.findViewById(R.id.restar3);
		ImageView reImage4 = (ImageView) userInfoDialog
				.findViewById(R.id.restar4);
		ImageView reImage5 = (ImageView) userInfoDialog
				.findViewById(R.id.restar5);
		TextView startTextView = (TextView) userInfoDialog
				.findViewById(R.id.rating_text);
		int totalstar = 0;

		database.openAndReadReviewTable(documentId);
		for (int i = 0; i < ZoomifierDatabase.documentreviewtitle.size(); i++) {
			reviewList.add(new Book(ZoomifierDatabase.documentreviewtitle
					.get(i), ZoomifierDatabase.documentreview.get(i),
					ZoomifierDatabase.time.get(i),
					ZoomifierDatabase.noforeviewstar.get(i)));
			totalstar = totalstar
					+ Integer.parseInt(ZoomifierDatabase.noforeviewstar.get(i));
		}
		int avragestar = 0;
		if (ZoomifierDatabase.documentreviewtitle.size() != 0) {
			avragestar = totalstar
					/ ZoomifierDatabase.documentreviewtitle.size();

			if (avragestar == 1) {
				reImage1.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 2) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 3) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 4) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
				reImage4.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 5) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
				reImage4.setImageResource(R.drawable.golden_star);
				reImage5.setImageResource(R.drawable.golden_star);
			}
		}

		startTextView.setText(Integer.toString(reviewList.size()) + " Rating");

		AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(
				PDFImageViewActivity.this, 112, reviewList);
		listview.setAdapter(adapter);
		Button writereviewButton = (Button) userInfoDialog
				.findViewById(R.id.review_button);

		writereviewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog reveiewDialgo = new Dialog(
						PDFImageViewActivity.this);
				reveiewDialgo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				reveiewDialgo.setContentView(R.layout.riview_dialog);
				reveiewDialgo.setCanceledOnTouchOutside(false);
				// reveiewDialgo.getWindow().setLayout(500, 700);
				reveiewDialgo.show();
				numberofstar = 0;
				final ImageView imageview1 = (ImageView) reveiewDialgo
						.findViewById(R.id.star1);
				final EditText title = (EditText) reveiewDialgo
						.findViewById(R.id.title_edit);
				final EditText reviewtext = (EditText) reveiewDialgo
						.findViewById(R.id.review_edit);
				imageview1.setTag(R.drawable.black_star);
				final ImageView imageview2 = (ImageView) reveiewDialgo
						.findViewById(R.id.star2);
				imageview2.setTag(R.drawable.black_star);
				final ImageView imageview3 = (ImageView) reveiewDialgo
						.findViewById(R.id.star3);
				imageview3.setTag(R.drawable.black_star);
				final ImageView imageview4 = (ImageView) reveiewDialgo
						.findViewById(R.id.star4);
				imageview4.setTag(R.drawable.black_star);
				final ImageView imageview5 = (ImageView) reveiewDialgo
						.findViewById(R.id.star5);
				imageview5.setTag(R.drawable.black_star);
				final Button submitButton = (Button) reveiewDialgo
						.findViewById(R.id.submit_button);
				submitButton.setBackgroundResource(R.drawable.black_submit);
				submitButton.setTag(R.drawable.black_submit);
				TextWatcher filterTextWatcher = new TextWatcher() {

					public void afterTextChanged(Editable s) {
					}

					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}

				};
				title.addTextChangedListener(filterTextWatcher);
				reviewtext.addTextChangedListener(filterTextWatcher);

				imageview1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						/*
						 * if(imageview2.getTag().equals(R.drawable.black_star))
						 * {
						 */
						if (imageview1.getTag().equals(R.drawable.black_star)) {
							imageview1.setImageResource(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							imageview1.setTag(R.drawable.golden_star);
						} else {

							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.golden_star)) {
								imageview3
										.setImageResource(R.drawable.black_star);
								imageview3.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview2.getTag().equals(
									R.drawable.golden_star)) {
								imageview2
										.setImageResource(R.drawable.black_star);
								imageview2.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview1.setImageResource(R.drawable.black_star);
							imageview1.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}
						// }

					}
				});

				imageview2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview1.getTag().equals(R.drawable.golden_star)&&
						 * imageview3.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview2.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.golden_star)) {
								imageview3
										.setImageResource(R.drawable.black_star);
								imageview3.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview2.setImageResource(R.drawable.black_star);
							imageview2.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}

					// }
				});

				imageview3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview2.getTag().equals(R.drawable.golden_star)&&
						 * imageview4.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview3.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview3.setImageResource(R.drawable.black_star);
							imageview3.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;

						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

						// }
					}
				});

				imageview4.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview3.getTag().equals(R.drawable.golden_star)&&
						 * imageview5.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview4.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.black_star)) {
								imageview4
										.setImageResource(R.drawable.golden_star);
								imageview4.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview4.setImageResource(R.drawable.black_star);
							imageview4.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}

						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}
					// }
				});

				imageview5.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview4.getTag().equals(R.drawable.golden_star))
						 * {
						 */
						if (imageview5.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.black_star)) {
								imageview4
										.setImageResource(R.drawable.golden_star);
								imageview4.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview5.getTag().equals(
									R.drawable.black_star)) {
								imageview5
										.setImageResource(R.drawable.golden_star);
								imageview5.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {

							imageview5.setImageResource(R.drawable.black_star);
							imageview5.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

						// }

					}
				});

				submitButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (submitButton.getTag().equals(R.drawable.red_submit)) {
							Calendar calendar = Calendar.getInstance();
							int mYear = calendar.get(Calendar.YEAR);
							int mMonth = calendar.get(Calendar.MONTH);
							int mDay = calendar.get(Calendar.DAY_OF_MONTH);
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat month_date = new SimpleDateFormat(
									"MMMMMMMMM");
							String month_name = month_date.format(cal.getTime());
							int date = calendar.get(Calendar.DATE);
							review_title = title.getText().toString();

							String completDate = Integer.toString(date) + ", "
									+ month_name + " "
									+ Integer.toString(mYear);
							review_time = completDate;
							revew_text = reviewtext.getText().toString();
							database.insertIntoReviewTable(documentId, title
									.getText().toString(), reviewtext.getText()
									.toString(),
									Integer.toString(numberofstar), completDate);
							new SendReview().execute();
							reviewList.clear();
							ImageView reImage1 = (ImageView) userInfoDialog
									.findViewById(R.id.restar1);
							ImageView reImage2 = (ImageView) userInfoDialog
									.findViewById(R.id.restar2);
							ImageView reImage3 = (ImageView) userInfoDialog
									.findViewById(R.id.restar3);
							ImageView reImage4 = (ImageView) userInfoDialog
									.findViewById(R.id.restar4);
							ImageView reImage5 = (ImageView) userInfoDialog
									.findViewById(R.id.restar5);
							TextView startTextView = (TextView) userInfoDialog
									.findViewById(R.id.rating_text);
							int totalstar = 0;

							database.openAndReadReviewTable(documentId);
							for (int i = 0; i < ZoomifierDatabase.documentreviewtitle
									.size(); i++) {
								reviewList.add(new Book(
										ZoomifierDatabase.documentreviewtitle
												.get(i),
										ZoomifierDatabase.documentreview.get(i),
										ZoomifierDatabase.time.get(i),
										ZoomifierDatabase.noforeviewstar.get(i)));
								totalstar = totalstar
										+ Integer
												.parseInt(ZoomifierDatabase.noforeviewstar
														.get(i));
							}
							int avragestar = 0;
							if (ZoomifierDatabase.documentreviewtitle.size() != 0) {
								avragestar = totalstar
										/ ZoomifierDatabase.documentreviewtitle
												.size();
								if (avragestar == 1) {
									reImage1.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 2) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 3) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 4) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
									reImage4.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 5) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
									reImage4.setImageResource(R.drawable.golden_star);
									reImage5.setImageResource(R.drawable.golden_star);
								}
							}
							startTextView.setText(Integer.toString(reviewList
									.size()) + " Rating");
							AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(
									PDFImageViewActivity.this, 112, reviewList);
							listview.setAdapter(adapter);
							reveiewDialgo.dismiss();

						}

					}
				});
				Button cancelButton = (Button) reveiewDialgo
						.findViewById(R.id.cancel_text);
				cancelButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						reveiewDialgo.dismiss();

					}
				});
			}
		});
	}

	public class SharePdf extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"SHARE_EMAIL\">"
					+ "<params email=\"" + emailField.getText().toString()
					+ "\" docid=\"" + documentId + "\" clientid=\"" + clientid
					+ "\" readerid=\"" + userid + "\"/>" + "</operation>"
					+ "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							/*
							 * grid_data.add(new GridData(
							 * xpp.getAttributeValue(0), xpp
							 * .getAttributeValue(1), readerId));
							 */
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
							try {

								database.openAndWriteDataBase();
								// database.insertIntoCompanyTable(xpp.getAttributeValue(0),
								// xpp.getAttributeValue(1));
							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				displayAlert("Done Successfully");
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

			} /*
			 * else { Toast.makeText(PDFImageViewActivity.this,
			 * "Done Successfully", Toast.LENGTH_LONG).show(); RelativeLayout
			 * layout = (RelativeLayout)
			 * findViewById(R.id.setting_dialog_layout);
			 * layout.setVisibility(View.GONE); }
			 */

		}

	}

	public class ShareEmail extends AsyncTask<Void, Void, Void> {
		EditText emailField = (EditText) findViewById(R.id.email_field);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"FORWARD_DOCUMENT\">"
					+ "<params email=\"" + emailField.getText().toString()
					+ "\" docid=\"" + documentId + "\" clientid=\"" + clientid
					+ "\" readerid=\"" + userid + "\"/>" + "</operation>"
					+ "</request>";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							/*
							 * grid_data.add(new GridData(
							 * xpp.getAttributeValue(0), xpp
							 * .getAttributeValue(1), readerId));
							 */
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
							try {

								database.openAndWriteDataBase();
								// database.insertIntoCompanyTable(xpp.getAttributeValue(0),
								// xpp.getAttributeValue(1));
							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				EditText emailField = (EditText) findViewById(R.id.email_field);
				String email = emailField.getText().toString();
				database.insertIntoPageAnaylytics(documentId, "", startTime,
						"0", "0", searchString, clientid, userid, " ", email,
						"DOCUMENT", documentId, userid, "6");
				displayAlert("Done Successfully");
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

			}
		}

	}

	public void displayAlert(String msg) {
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setTitle("Shared")
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	public class LikeDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Data is Liked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"ADD_READER_FAVORITE\">"
					+ "<params readerid=\"" + userid + "\" clientid=\""
					+ clientid + "\"/>" + "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							try {

							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				database.openAndWriteDataBase();
				database.updateDocumentLikes(documentId);
				database.insertIntoPageAnaylytics(documentId, "", startTime,
						"0", "0", searchString, clientid, userid, "", "",
						"DOCUMENT", documentId, userid, "7");
				heartButton.setBackgroundResource(R.drawable.heart_plus);
				if (documetnlikeactivity) {
					Bundle bundle = new Bundle();
					bundle.putString("readerId", userid);
					bundle.putString("likesignal", "");
					Intent intent = new Intent(PDFImageViewActivity.this,
							DocumetntLikes.class);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}
			}

		}

	}

	public class UnLikeDocumentDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Data is Unliked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"DELETE_READER_FAVORITE\">"
					+ "<params readerid=\"" + userid + "\" clientid=\""
					+ clientid + "\"/>" + "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							/*
							 * grid_data.add(new GridData(
							 * xpp.getAttributeValue(0), xpp
							 * .getAttributeValue(1), readerId));
							 */
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
							try {

								database.openAndWriteDataBase();
								// database.insertIntoCompanyTable(xpp.getAttributeValue(0),
								// xpp.getAttributeValue(1));
							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				database.openAndWriteDataBase();
				database.updateDocumentUnlikes(documentId);
				database.insertIntoPageAnaylytics(documentId, "", startTime,
						"0", "0", searchString, clientid, userid, "", "",
						"DOCUMENT", documentId, userid, "8");
				heartButton.setBackgroundResource(R.drawable.heart_minus);
				if (documetnlikeactivity) {
					Bundle bundle = new Bundle();
					bundle.putString("readerId", userid);

					bundle.putString("likesignal", "");
					Intent intent = new Intent(PDFImageViewActivity.this,
							DocumetntLikes.class);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}

			}
		}

	}

	public class LoadDocument extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();

			numberofpage = "";
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				String url = "http://ve1.zoomifier.net/" + clientid + "/"
						+ documentId + "/" + documentId + ".xml";
				HttpPost post = new HttpPost(url);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				categoryResponse = EntityUtils.toString(entity);
				Log.i("Response for get All Category=========",
						categoryResponse);
				String strsss = categoryResponse;
				strsss = strsss + "s";
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(categoryResponse));
				Log.i("document id========================", categoryResponse);
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {
					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("Document")) {
							numberofpage = xpp.getAttributeValue(6);
						} else if (xpp.getName().equals("document")) {

						}

					}

					else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {

				progressDialog.dismiss();
				database.openAndWriteDataBase();
				database.insertIntoPageTable(documentId, numberofpage,"","");
				int ot = getResources().getConfiguration().orientation;
				show_number_of_page = numberofpages = Integer
						.parseInt(numberofpage);
				List<String> items = new ArrayList<String>();
				if (Configuration.ORIENTATION_PORTRAIT == ot) {

					for (int i = 0; i < numberofpages; i++) {
						String url = "http://ve1.zoomifier.net/" + clientid
								+ "/" + documentId + "/ipad/Page"
								+ Integer.toString(i + 1) + "-1x.jpg";
						items.add(url);
					}
					if (items.size() != 0) {
						pagerAdapter = new UrlPagerAdapter(
								PDFImageViewActivity.this, items, "portriate");
					}

					String str = "sfsf";

				} else {
					horizontalline = 0;
					if (numberofpages % 2 == 0) {
						for (int i = 0; i < numberofpages + 2; i = i + 2) {
							if (i == 0) {
								String url = " " + ","
										+ "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page"
										+ Integer.toString(i + 1) + "-1x.jpg";
								items.add(url);
								horizontalline++;
							} else if (numberofpages == i) {
								String url = "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page" + Integer.toString(i)
										+ "-1x.jpg" + "," + " ";
								horizontalline++;
								items.add(url);
							} else {
								String url = "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page" + Integer.toString(i)
										+ "-1x.jpg" + ","
										+ "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page"
										+ Integer.toString(i + 1) + "-1x.jpg";
								horizontalline++;
								items.add(url);
							}

						}

					} else {
						horizontalline = 0;

						for (int i = 0; i < numberofpages + 1; i = i + 2) {
							if (i == 0) {
								String url = " " + ","
										+ "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page"
										+ Integer.toString(i + 1) + "-1x.jpg";
								items.add(url);
								horizontalline++;
							} else {
								String url = "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page" + Integer.toString(i)
										+ "-1x.jpg" + ","
										+ "http://ve1.zoomifier.net/"
										+ clientid + "/" + documentId
										+ "/ipad/Page"
										+ Integer.toString(i + 1) + "-1x.jpg";
								horizontalline++;
								items.add(url);
							}

						}

					}

					numberofpages = horizontalline;
					pagerAdapter = new UrlPagerAdapter(
							PDFImageViewActivity.this, items, "landscape");

				}
				numberofpages = Integer.parseInt(numberofpage);
				if (numberofpages > 1) {
					drawseekbar();
					if (pagedata.page.size() != 0) {
						for (int i = 0; i < pagedata.page.size(); i++) {
							RelativeLayout relatives = (RelativeLayout) findViewById(R.id.webview_layout11);
							View v = relatives.findViewById(Integer
									.parseInt(pagedata.page.get(i)));
							v.setBackgroundColor(Color.rgb(128, 0, 128));

						}
					}

				}
				if (numberofpages == 0) {
					displayAlert("Data is caching");
				}

				pagedevision = 100 / numberofpages;

				mViewPager.setOffscreenPageLimit(3);
				mViewPager.setAdapter(pagerAdapter);

				float d = numberofpages - 1;
				final int progresss = 100 / (numberofpages - 1);
				final float po = 100.0f / d;

				pagerAdapter
						.setOnItemChangeListener(new OnItemChangeListener() {
							@Override
							public void onItemChange(int currentPosition) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"yyyy/MM/dd HH:mm:ss");
								Calendar cal = Calendar.getInstance();
								endpageminut = cal.get(Calendar.MINUTE);
								int timeDifference = endpageminut
										- startpageminut;
								if (timeDifference >= 1) {
									pageEndTime = dateFormat.format(cal
											.getTime());
									String timeDiff = Integer
											.toString(timeDifference * 60);
									database.openAndWriteDataBase();
									database.insertIntoPageAnaylytics(
											documentId,
											Integer.toString(currentpage),
											pageStartTime, timeDiff, "0",
											searchString, clientid, userid,
											" ", " ", "DOCUMENT",
											Integer.toString(currentpage),
											userid, "3");
								}

								if (!seekbarTouch) {
									int p1 = (int) (po * currentPosition);
									seekbarTouch = false;
									seekbar.setProgress(p1);
									mainSeekbar.setVisibility(View.GONE);

								} else {

									int p1 = (int) (po * currentPosition);

								}
								pageStartTime = dateFormat.format(cal.getTime());
								startpageminut = cal.get(Calendar.MINUTE);
								currentpage = currentPosition + 1;

							}
						});

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {

			}
		}
	}

	public class SendReview extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PDFImageViewActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"SET_CONTENT_REVIEW\">"
					+ "<params clientid=\"" + clientid + "\" contentid=\""
					+ documentId + "\" readerid=\"" + userid + "\" title=\""
					+ review_title + "\" rating=\""
					+ Integer.toOctalString(numberofstar) + "\" review=\""
					+ revew_text + "\"/>" + "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							/*
							 * grid_data.add(new GridData(
							 * xpp.getAttributeValue(0), xpp
							 * .getAttributeValue(1), readerId));
							 * 
							 * y String id = xpp.getAttributeValue(0); String
							 * contenttype = xpp.getAttributeValue(1); String
							 * clintId = xpp.getAttributeValue(2);
							 */
							try {

								// database.openAndWriteDataBase();
								// database.insertIntoCompanyTable(xpp.getAttributeValue(0),
								// xpp.getAttributeValue(1));
							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				/*
				 * database.openAndWriteDataBase();
				 * database.updateDocumentLikes(documentId);
				 * heartButton.setBackgroundResource(R.drawable.heart_plus);
				 */
			}

		}

	}

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	public void drawseekbar() {
		currentpage = 1;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		pageStartTime = startTime = dateFormat.format(cal.getTime());
		startpageminut = startdocminut = cal.get(Calendar.MINUTE);
		int pixel = this.getWindowManager().getDefaultDisplay().getWidth();
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.webview_layout11);
		int menubuttonsize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
						.getDisplayMetrics());
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
				pixel - menubuttonsize, 10);
		relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
		// relativeParams.setMargins(10, 30, 10, 0);
		View lineView = new View(this);
		lineView.setBackgroundResource(R.drawable.linee);
		lineView.setLayoutParams(relativeParams);
		relative.addView(lineView);
		RelativeLayout linear = new RelativeLayout(this);
		float density = this.getResources().getDisplayMetrics().density;
		float px = 40 * density;
		int il = (int) px;
		int linesize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
						.getDisplayMetrics());
		RelativeLayout.LayoutParams relativeParamss = new RelativeLayout.LayoutParams(
				pixel - (linesize + 40), 30);
		relativeParamss.setMargins(25, 0, 25, 0);
		relativeParamss.addRule(RelativeLayout.CENTER_VERTICAL);
		linear.setLayoutParams(relativeParamss);
		int align = (pixel - (linesize + 50)) / (numberofpages - 1);
		for (int i = 0; i < numberofpages; i++) {
			if (numberofpages == 2) {
				if (i == 0) {
					RelativeLayout.LayoutParams relativepa = new RelativeLayout.LayoutParams(
							1, 50);
					relativepa.setMargins(align * i, 0, 0, 0);
					View verticalView = new View(this);
					verticalView.setId(i + 1);
					verticalView.setBackgroundColor(Color.WHITE);
					verticalView.setLayoutParams(relativepa);
					linear.addView(verticalView);

				} else {
					RelativeLayout.LayoutParams relativepa = new RelativeLayout.LayoutParams(
							1, 50);
					int alignmargin = align * i;
					alignmargin = alignmargin - 4;

					relativepa.setMargins(alignmargin, 0, 0, 0);
					View verticalView = new View(this);
					verticalView.setBackgroundColor(Color.WHITE);
					verticalView.setId(i + 1);
					verticalView.setLayoutParams(relativepa);
					linear.addView(verticalView);
				}

			} else {
				if (i == 0) {
					RelativeLayout.LayoutParams relativepa = new RelativeLayout.LayoutParams(
							1, 50);
					relativepa.setMargins(align * i, 0, 0, 0);
					View verticalView = new View(this);
					verticalView.setBackgroundColor(Color.WHITE);
					verticalView.setId(i + 1);
					verticalView.setLayoutParams(relativepa);
					linear.addView(verticalView);
				} else {

					RelativeLayout.LayoutParams relativepa = new RelativeLayout.LayoutParams(
							1, 50);
					int alignmargin = align * i;
					if ((alignmargin * i) >= (pixel - (linesize + 20))) {
						alignmargin = alignmargin - 2;
					}
					relativepa.setMargins(alignmargin, 0, 0, 0);
					View verticalView = new View(this);
					verticalView.setId(i + 1);
					verticalView.setBackgroundColor(Color.WHITE);
					verticalView.setLayoutParams(relativepa);
					linear.addView(verticalView);
				}

			}

		}
		relative.addView(linear);
		seekbar = new SeekBar(this);
		RelativeLayout.LayoutParams seekbarpar = new RelativeLayout.LayoutParams(
				pixel - (linesize + 15),
				android.app.ActionBar.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams mainseekbarpar = new RelativeLayout.LayoutParams(
				pixel - (linesize + 15),
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		seekbarpar.addRule(RelativeLayout.CENTER_VERTICAL);
		seekbarpar.setMargins(10, 0, 10, 0);
		mainseekbarpar.setMargins(10, 0, 10, 0);
		Drawable drawable = getResources().getDrawable(
				R.drawable.progress_greytransparent);
		Drawable drawable1 = getResources().getDrawable(R.drawable.circle);
		seekbar.setLayoutParams(seekbarpar);
		seekbar.setPadding(20, 0, 20, 0);
		seekbar.setProgressDrawable(drawable);
		// seekbar.setPadding(20, 0, 20, 0);
		seekbar.setThumb(drawable1);
		relative.addView(seekbar);
		final RelativeLayout mainseekbarlayout = (RelativeLayout) findViewById(R.id.webview_layout111);
		mainSeekbar = new SeekBar(this);
		seekbarpar.addRule(RelativeLayout.CENTER_VERTICAL);
		mainseekbarlayout.setVisibility(View.INVISIBLE);
		mainSeekbar.setLayoutParams(mainseekbarpar);
		mainSeekbar.setPadding(20, 0, 20, 0);
		mainSeekbar.setProgressDrawable(drawable);
		mainseekbarlayout.addView(mainSeekbar);
		mainSeekbar.setVisibility(View.GONE);
		float d = numberofpages - 1;
		final float pro = 100 / (numberofpages - 1);
		final int progresss = 100 / (numberofpages - 1);
		final float p = 100.0f / d;
		startingpostion.clear();
		lastpostion.clear();
		seekbar.setProgress(curretPostion * progresss);
		for (int i = 0; i < numberofpages; i++) {
			if (i == 0) {
				startingpostion.add(i * progresss);
				int last = ((i * progresss) + (i + 1) * progresss) / 2;
				lastpostion.add(last + 1);
			} else {
				int startting = (((i - 1) * progresss) + (i * progresss)) / 2;
				startingpostion.add(startting);
				int last = ((i * progresss) + ((i + 1) * progresss)) / 2;
				lastpostion.add(last + 1);
			}

		}
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			int seekbarprogress;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				// viewPager.setCurrentItem(curretPostion);
				if (numberofpages <= 15) {

					for (int l = 0; l < startingpostion.size(); l++) {
						if (seekbarprogress >= startingpostion.get(l)
								&& seekbarprogress <= lastpostion.get(l)) {

							i = l;
							pagenumber11 = l;
							break;
						}
					}

					mainSeekbar.setVisibility(View.GONE);
					int p1 = (int) (p * pagenumber11);
					seekBar.setProgress(p1);
					seekbarTouch = false;
					currentProgress = p1;
					mViewPager.setCurrentItem(pagenumber11);

					pagenumber11 = 0;
					thumbview = false;
				} else {
					for (int l = 0; l < startingpostion.size(); l++) {
						if (seekbarprogress >= startingpostion.get(l)
								&& seekbarprogress <= lastpostion.get(l)) {

							i = l;
							pagenumber11 = l;
							break;
						}
					}

					mainSeekbar.setVisibility(View.GONE);
					int p1 = (int) (p * pagenumber11);
					seekBar.setProgress(seekbarprogress);
					seekbarTouch = false;
					currentProgress = p1;
					mViewPager.setCurrentItem(pagenumber11);

					pagenumber11 = 0;
					thumbview = false;
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				thumbview = true;
				seekbarTouch = true;
				mainseekbarlayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				if (thumbview) {
					// int i=(int) (progress/(numberofpages-1));
					/*
					 * if(i>=numberofpages) { i=numberofpages-1; }
					 */
					for (int l = 0; l < startingpostion.size(); l++) {
						if (progress >= startingpostion.get(l)
								&& progress <= lastpostion.get(l)) {

							i = l;
							pagenumber11 = l;
							break;
						}
					}

					seekbarprogress = progress;

					curretPostion = i;
					String url1 = "http://ve1.zoomifier.net/" + clientid + "/"
							+ documentId + "/ipad/Page"
							+ Integer.toString(i + 1) + "Thb.jpg";
					String url = "http://ve1.zoomifier.net/" + clientid + "/"
							+ documentId + "/ipad/Page"
							+ Integer.toString(i + 1) + "-2x.jpg";

					LayoutInflater inflater = (LayoutInflater) PDFImageViewActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View bubbleView = (LinearLayout) inflater.inflate(
							R.layout.map_layout, null);
					TextView textView = (TextView) bubbleView
							.findViewById(R.id.text1);
					textView.setText(Integer.toString(i + 1));
					ImageView imagedrawable = (ImageView) bubbleView
							.findViewById(R.id.frieds_images);
					imageloader.DisplayImage(url1, imagedrawable);
					bubbleView.setDrawingCacheEnabled(true);
					bubbleView.measure(MeasureSpec.makeMeasureSpec(0,
							MeasureSpec.UNSPECIFIED), MeasureSpec
							.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					bubbleView.layout(0, 0, bubbleView.getMeasuredWidth(),
							bubbleView.getMeasuredHeight());
					bubbleView.buildDrawingCache(true);
					Bitmap b = Bitmap.createBitmap(bubbleView.getDrawingCache());
					Drawable drawable = new BitmapDrawable(
							PDFImageViewActivity.this.getResources(), b);

					mainSeekbar.setThumb(drawable);
					int p1 = (int) (p * curretPostion);
					mainSeekbar.setProgress(progress);
					mainSeekbar.setVisibility(View.VISIBLE);
					if (seekbarTouch) {
						mainSeekbar.setVisibility(View.VISIBLE);
					}
					// seekbar.setProgress(progress);

				}

				else {
					mainSeekbar.setVisibility(View.GONE);
				}

			}
		});
		mainSeekbar.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return true;
			}
		});

	}

	public class SearchPDF extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"READER_QUERY\">"
					+ "<params text=\"" + searchEditText.getText().toString()
					+ "\" catid=\"" + "" + "\" docid=\"" + documentId
					+ "\" clientid=\"" + clientid + "\" readerid=\"" + userid
					+ "\"/>" + "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/textsearchws/rest/search");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {
					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("Document")) {
							searchDocumentId = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("SearchResults")) {
							searchString = xpp.getAttributeValue(0);
						} else if (xpp.getName().equals("Page")) {

							try {
								if (searchDocumentId.equals(documentId)) {

									searchPages.add(xpp.getAttributeValue(0));

								}

							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			database.insertIntoPageAnaylytics(documentId, "", startTime, "0",
					"0", searchString, clientid, userid, " ", " ", "DOCUMENT",
					userid, "0", "9");
			for (int i = 1; i <= numberofpages; i++) {
				RelativeLayout relative = (RelativeLayout) findViewById(R.id.webview_layout11);
				View v = relative.findViewById(i);
				v.setBackgroundColor(Color.WHITE);
			}
			for (int i = 0; i < searchPages.size(); i++) {
				RelativeLayout relative = (RelativeLayout) findViewById(R.id.webview_layout11);
				View v = relative.findViewById(Integer.parseInt(searchPages
						.get(i)));
				v.setBackgroundColor(Color.rgb(128, 0, 128));

			}
		}

	}

	@Override
	public void onBackPressed() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			endTime = dateFormat.format(cal.getTime());
			enddocumentminut = cal.get(Calendar.MINUTE);
			int timedefferenc = enddocumentminut - startdocminut;
			if (timedefferenc >= 1) {
				String timeDifference = Integer.toString(timedefferenc * 60);
				database.openAndWriteDataBase();
				database.insertIntoPageAnaylytics(documentId, "", startTime,
						timeDifference, "0", searchString, clientid, userid,
						" ", " ", "DOCUMENT", documentId, userid, "1");
			}
			super.onBackPressed();
		} catch (Exception e) {
			super.onBackPressed();
		}

	}

}
