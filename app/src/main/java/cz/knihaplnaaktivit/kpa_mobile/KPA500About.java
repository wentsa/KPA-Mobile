package cz.knihaplnaaktivit.kpa_mobile;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.knihaplnaaktivit.kpa_mobile.connectors.ApiConnector;
import cz.knihaplnaaktivit.kpa_mobile.utilities.Utils;

public class KPA500About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpa500_about);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_up);
    }

    public void onWebClicked(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://knihaplnaaktivit.cz")));
    }

    public void onMailInfoClicked(View view) {
        sendMail("info@knihaplnaaktivit.cz");
    }

    public void onMailObjednavkyClicked(View view) {
        sendMail("objednavky@knihaplnaaktivit.cz");
    }

    private void sendMail(String to) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        try {
            startActivity(Intent.createChooser(i, null));
        } catch (android.content.ActivityNotFoundException ex) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email", to);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, getString(R.string.no_mail_client), Toast.LENGTH_SHORT).show();
        }
    }

    public void onFbPageClicked(View view) {
        Utils.visitFacebook(this, "www.knihaplnaaktivit.cz", "1552685675006861", true);
    }

    public void onFbGroupClicked(View view) {
        Utils.visitFacebook(this, "groups/909307232454767/", "909307232454767", false);
    }
}
