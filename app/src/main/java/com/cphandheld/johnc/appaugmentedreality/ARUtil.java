package com.cphandheld.johnc.appaugmentedreality;

import android.location.Criteria;

/**
 * Created by jcobb on 4/6/17.
 */

public class ARUtil {

    public static Criteria getFineCriteria() {
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);

        return c;
    }

    public static Criteria getCoarseCriteria() {
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);

        return c;
    }
}
