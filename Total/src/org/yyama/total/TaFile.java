package org.yyama.total;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TaFile {
	public static String selectedFileName;
	public static String selectedFileText;

	public static void fileSave(final MainActivity ma) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ma);
		final EditText edit = new EditText(ma);
		edit.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(edit);
		builder.setTitle(ma.getString(R.string.please_memo));

		builder.setNegativeButton(R.string.cancel,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.setPositiveButton(R.string.saveFile,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fileName = String.format(
								"TA_%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%2$s.txt",
								Calendar.getInstance(), edit.getText());
						PrintWriter pw = null;
						try {
							pw = new PrintWriter(ma.openFileOutput(fileName,
									Activity.MODE_PRIVATE));
							for (BigDecimal bd : ma.costList) {
								pw.write(bd.toString() + ",");
							}
							pw.flush();
							Toast.makeText(ma, R.string.has_been_saved,
									Toast.LENGTH_LONG).show();
							// 全画面広告の表示
							if (ma.interstitial.isLoaded()) {
								Random rnd = new Random();
								if (rnd.nextInt(3) != 0) {
									ma.interstitial.show();
								}
							} else {
								ma.add2Setting();
							}

						} catch (Exception e) {
							Toast.makeText(ma, R.string.failed_to_save,
									Toast.LENGTH_LONG).show();
						} finally {
							if (!(pw == null)) {
								pw.close();
							}
						}
					}
				});
		final AlertDialog dialog = builder.create();
		edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE)
							.performClick();
				}
				return true;
			}
		});
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// ソフトキーボードを出す
				InputMethodManager inputMethodManager = (InputMethodManager) ma
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(edit, 0);
			}
		});
		dialog.show();
	}

	public static final int REQUEST_CD = 100;

	public static void fileLoad(final MainActivity ma) {
		// ファイル選択アクティビティを表示
		Intent intent = new Intent(ma, LocalFileActivity.class);
		ma.startActivityForResult(intent, REQUEST_CD);
	}

}
