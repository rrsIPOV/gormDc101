package gormDc

import grails.gorm.DetachedCriteria
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class LicensorIntSpec extends Specification {

    /** The following is working. */
    void "Test Lookup for Licensor By group"() {

        given:
        setupData();

        def dc = new DetachedCriteria(LicenseEntitlement);
        dc.inList('groupId', [101,102,104]);
        dc.eq('assetId', 2);
        dc.projections {
            distinct('licensor')
        }

        def criteria = new DetachedCriteria(Licensor);
        criteria.inList('id', dc);

        def result = criteria.list(offset:0, max:5)

        expect:
        result.size() <= 5
    }

    /** The following is failing with an NPE on when list is called on the 2nd  DetachedCriteria. */
    void "Test Lookup for Licensor using OR with direct data"() {
        given:
        setupData();

        def dc = new DetachedCriteria(LicenseEntitlement);
        dc.eq('assetId', 2);
        dc.or {
            inList('groupId', [101,102,104]);
            inList('licenseId', [1,2,4]);
        }
        dc.projections {
            distinct('licensor')
        }

        def criteria = new DetachedCriteria(Licensor);
        criteria.inList('id', dc);

        def result = criteria.list(offset:0, max:5)

        expect: "The following is never reached due to NPE on the list()"
        result.size() <= 5
    }

    /** The following is failing with an NPE on when list is called on the 2nd  DetachedCriteria. */
    void "Test Lookup for Licensor using where with direct data"() {
        given:
        setupData();

        def dc = LicenseEntitlement.where { assetId == 2 };
        dc = dc.where { groupId in [101,102,104] || licenseId in [1,2,4] }
        dc.eq('assetId', 2);
        dc.projections {
            distinct('licensor')
        }

        def criteria = new DetachedCriteria(LicenseEntitlement);
        criteria.inList('id', dc);

        def result = criteria.list(offset:0, max:5)

        expect: "The following is never reached due to NPE on the list()"
        result.size() <= 5
    }

    /** The following is failing with an NPE on when list is called on the 2nd  DetachedCriteria.
    void "Test Lookup for Licensor using OR with referenced data"() {
        given:
        setupData();

        List groups = [101,102,104];
        List lics = [1,2,4];

        def dc = new DetachedCriteria(LicenseEntitlement);
        dc.eq('assetId', 2);
        dc.or {
            inList('groupId', groups);
            inList('licenseId', lics);
        }
        dc.projections {
            distinct('licensor')
        }

        def criteria = new DetachedCriteria(Licensor);
        criteria.inList('id', dc);

        def result = criteria.list(offset:0, max:5)

        expect: "The following is never reached due to NPE on the list()"
        result.size() <= 5
    } */

    protected void setupData() {

        Random random = new Random();

        for (int i in 1..100) {
            Licensor lic = new Licensor(email: "email-${i}@address.no");

            for (int j in 1..5) {
                int r = random.nextInt(20);
                LicenseEntitlement ent = new LicenseEntitlement(
                    groupId: (100 + r),
                    licenseId: r,
                    assetId: i
                );

                lic.addToLicenseEntitlements(ent);
            }

            lic.save(failOnError:true,flush: true);
        }
    }

}
