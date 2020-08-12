package schaubeck.eike.qrcreator.QRCode.decode;

import android.os.Build;
import android.support.annotation.RequiresApi;

public class QRDecodeException extends RuntimeException {

    public QRDecodeException() {
    }

    public QRDecodeException(String message) {
        super(message);
    }

    public QRDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public QRDecodeException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public QRDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
