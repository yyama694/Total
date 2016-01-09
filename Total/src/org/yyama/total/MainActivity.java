package org.yyama.total;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// adView を作成する
		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-2505812570403600/5350041373");
		adView.setAdSize(AdSize.BANNER);
		// AdRequest adRequest = new
		// AdRequest.Builder().build();
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"F3B1B2779DEF816F9B31AA6C6DC57C3F").build();
		LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayout1);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ll.addView(adView, lp);
		adView.loadAd(adRequest);
		// 全画面広告の初期化
		add2Init();

		Button btn = (Button) findViewById(R.id.btnAdd);
		btn.setOnClickListener(this);
		btn = (Button) findViewById(R.id.btnAllClear);
		btn.setOnClickListener(this);
		loadData();
		draw();
	}

	public InterstitialAd interstitial;

	// 全画面広告の初期化。
	private void add2Init() {
		// インタースティシャルを作成する。
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-2505812570403600/7972103770");

		// Set the AdListener.
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				add2Setting();
			}
		});
	}

	// 全画面広告の方。1回表示するたびにロードしなおす必要があるみたい。
	public void add2Setting() {
		if (!interstitial.isLoaded()) {
			Log.d("yyama", "未ロードのため、リクエストします。");
			// 広告リクエストを作成する。
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(
					"F3B1B2779DEF816F9B31AA6C6DC57C3F").build();
			// インタースティシャルの読み込みを開始する。
			interstitial.loadAd(adRequest);
		} else {
		}
	}

	public List<BigDecimal> costList = new ArrayList<BigDecimal>();

	private static final String FILE_NAME = "sumFile.csv";

	public boolean fullScroolFlg = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
			// 追加ボタン処理
			DialogHelper.makeDialog(this).show();
			break;
		case R.id.btnAllClear:
			// 全クリアボタン処理
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(R.string.confirmation);
			alertDialogBuilder.setMessage(R.string.delete_all_confirmation);
			alertDialogBuilder.setPositiveButton(R.string.ok,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteFile(FILE_NAME);
							costList.clear();
							draw();
						}
					});
			alertDialogBuilder.setNeutralButton(R.string.cancel,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			alertDialogBuilder.create().show();
			break;
		}
	}

	private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
	private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

	public void draw() {
		loadData();

		LinearLayout lv = (LinearLayout) findViewById(R.id.linerLayout2);
		while (lv.getChildCount() > 1) {
			// Log.d("yyama", String.valueOf(lv.getChildCount()));
			lv.removeViewAt(1);
		}
		BigDecimal sum = new BigDecimal("0");
		int cnt = 0;
		NumberFormat nfNum = NumberFormat.getNumberInstance();
		nfNum.setMaximumFractionDigits(Integer.MAX_VALUE);
		// DecimalFormat df1 = new DecimalFormat("#,###.##");
		for (BigDecimal i : costList) {
			final int cnt2 = cnt;
			cnt++;
			sum = sum.add(i);
			Log.d("yyama", "sum=" + sum.toString());
			TextView cost = new TextView(this);
			cost.setGravity(Gravity.RIGHT);
			cost.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
			cost.setText(nfNum.format(i));
			cost.setWidth(0);
			cost.setPadding(2, 2, 15, 2);
			// cost.setBackgroundColor(Color.RED);
			TextView tv = new TextView(this);
			tv.setWidth(0);
			tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			tv.setText(String.valueOf(cnt));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

			// tv.setBackgroundColor(Color.BLUE);

			Button btn = new Button(this);
			btn.setText(getString(R.string.del));
			btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			btn.setPadding(5, 5, 5, 5);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					costList.remove(cnt2);
					saveData();
					draw();
				}
			});
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					WRAP_CONTENT, WRAP_CONTENT);
			lp.setMargins(0, 0, 20, 0);
			lp.weight = 0.88f;
			LinearLayout lh = new LinearLayout(this);
			lh.addView(tv, new LinearLayout.LayoutParams(WRAP_CONTENT,
					MATCH_PARENT, 0.1f));
			lh.addView(cost, lp);

			lh.addView(btn, new LinearLayout.LayoutParams(150, MATCH_PARENT,
					0.01f));
			lv.addView(lh, new LinearLayout.LayoutParams(MATCH_PARENT,
					WRAP_CONTENT));

		}
		if (fullScroolFlg) {
			// スクロールビューを下段までスクロールさせる。
			ScrollView sc = (ScrollView) findViewById(R.id.scrollView1);
			sc.fullScroll(ScrollView.FOCUS_DOWN);
			fullScroolFlg = false;
		}
		((TextView) findViewById(R.id.tvSum)).setText(" " + nfNum.format(sum));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	public void saveData() {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			for (BigDecimal i : costList) {
				fos.write(String.valueOf(i).getBytes());
				fos.write(",".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadData() {
		FileInputStream fis = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			byte[] b = new byte[1024];
			fis = openFileInput(FILE_NAME);
			while (true) {
				int len = fis.read(b);
				if (len < 0) {
					break;
				}
				bout.write(b, 0, len);
			}
			Log.d("test", bout.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		costList.clear();

		String[] arr = bout.toString().split(",");
		for (String s : arr) {
			if (s == null || s.equals("")) {
				continue;
			}
			costList.add(new BigDecimal(s));
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.file_save:
			TaFile.fileSave(this);
			break;
		case R.id.file_open:
			TaFile.fileLoad(this);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TaFile.REQUEST_CD) {
			switch (resultCode) {
			case RESULT_OK:
				InputStream in = null;
				BufferedReader br = null;
				try {
					in = openFileInput(data.getStringExtra("file_name"));
					br = new BufferedReader(new InputStreamReader(in));
					costList.clear();
					String tmp = br.readLine();
					if (tmp != null) {
						String[] strArr = tmp.split(",");
						for (String s : strArr) {
							if (s != null && !s.equals("")) {
								costList.add(new BigDecimal(s));
							}
						}
					}
					saveData();
					draw();
					Toast.makeText(
							this,
							getString(R.string.opend_file)
									+ System.getProperty("line.separator")
									+ data.getStringExtra("file_title"),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, getString(R.string.file_open_error),
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
		}
	}

}
