package com.valohyd.nextseries.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class OurProgressDialog extends ProgressDialog {

    public OurProgressDialog(Activity act) {
        super(act);
        setDialogCancelable(act, this);
    }

    public OurProgressDialog(Activity act, int theme) {
        super(act, theme);
        setDialogCancelable(act, this);
    }
    
    private static void setDialogCancelable(Activity parent, ProgressDialog dialogue){
        dialogue.setCancelable(true);
        final Activity hook = parent;
        dialogue.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // TODO peut peut etre poser pb (quitte l'activity en cours)
                hook.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK));
                hook.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK));
            }
        });
    }
    
    public static ProgressDialog showDialog(Activity a, CharSequence title, CharSequence message, Boolean indeterminate){
        // créer et afficher le dialog
        ProgressDialog p = new OurProgressDialog(a);
        p.setTitle(title);
        p.setMessage(message);
        p.setIndeterminate(indeterminate);
        
        p.show();
        return p;
    }
}
