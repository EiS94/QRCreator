package schaubeck.eike.qrcreator.QRCode;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

public class Utils {

    static void checkNullParamater(Object object) {
        if (object == null) {
            throw new NullPointerException("invalid parameter");
        }
    }

    static boolean isNull(Object object){
        boolean ret = false;
        if (object == null) ret = true;
        return ret;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void checkNullParamaters(Object... objects) {
        Arrays.asList(objects).stream().forEach(o -> checkNullParamater(o));
    }

}
