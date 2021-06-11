package kush.example.contactsdemo

import android.content.Context
import android.graphics.Color
import com.kaopiz.kprogresshud.KProgressHUD

class ProgressUtil {
    companion object {
        private var hud: KProgressHUD? = null

        fun showProgressDialog(context: Context) {
            if (hud == null) {
                hud = KProgressHUD(context)
                hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setBackgroundColor(Color.parseColor("#00acc1"))
                    .setLabel("Please wait...")
                    .setCancellable(false)
                    .show();
            }
        }

        fun dismissDialog() {
            if (hud != null) {
                if (hud!!.isShowing) {
                    hud!!.dismiss();
                    hud = null;
                }
            }
        }

    }
}