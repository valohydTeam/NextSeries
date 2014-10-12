package com.valohyd.nextseries.models;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.valohyd.nextseries.R;

public class DialogInfo {

	private Dialog dialog;

	private String[] types = { "Info", "Attention", "Erreur" };

	private int[] pics = { R.drawable.info, R.drawable.alert, R.drawable.close };

	public DialogInfo(Context c, String texte, final int type) {
		dialog = new Dialog(c);

		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle(types[type]);
		
		TextView text = (TextView) dialog.findViewById(R.id.texteDialog);
		text.setText(texte);
		ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
		image.setImageResource(pics[type]);

		Button button2 = (Button) dialog.findViewById(R.id.ButtonDialog);
		if (type == 2) {
			button2.setText("Quitter");
		}
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (type == 2) {
					System.exit(0);
				}
				dialog.dismiss();
			}
		});

		dialog.setCancelable(true);
		dialog.show();
	}
	
}
