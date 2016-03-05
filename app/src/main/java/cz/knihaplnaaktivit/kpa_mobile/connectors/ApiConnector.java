package cz.knihaplnaaktivit.kpa_mobile.connectors;

import android.content.Context;
import android.content.Intent;

import cz.knihaplnaaktivit.kpa_mobile.connectors.services.ServiceSendImage;

public class ApiConnector {

    public static void sendImage(Context ctx, String name, String mail, String description, String imagePath) {
        Intent intent = new Intent(ctx, ServiceSendImage.class);
        intent.putExtra(ServiceSendImage.NAME, name);
        intent.putExtra(ServiceSendImage.EMAIL, mail);
        intent.putExtra(ServiceSendImage.DESCRIPTION, description);
        intent.putExtra(ServiceSendImage.IMAGE, imagePath);

        ctx.startService(intent);
    }
}
