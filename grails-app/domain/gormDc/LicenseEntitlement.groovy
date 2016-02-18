package gormDc

/**
 * Created by robert.sanders on 2/17/2016.
 */
class LicenseEntitlement {

    Integer id;

    Licensor licensor;

    /** Legacy System's ID for a license. */
    Integer licenseId;

    /** Legacy System's grouping identifier. */
    Integer groupId;

    /** Licensed Asset. */
    Integer assetId;

    static belongsTo = [licensor:Licensor]

    static constraints = {
        licenseId nullable: true
        groupId nullable: true
    }


}
