package gormDc

/**
 * Created by robert.sanders on 2/17/2016.
 */
class Licensor {

    Integer id;

    String email;

    Set licenseEntitlements;

    static  hasMany = [ licenseEntitlements : LicenseEntitlement ]

    static constraints = {
        email email: true
    }

}
