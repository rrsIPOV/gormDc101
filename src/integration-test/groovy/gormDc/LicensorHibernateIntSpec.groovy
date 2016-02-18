package gormDc


import org.hibernate.Criteria
import org.hibernate.SessionFactory
import org.hibernate.criterion.DetachedCriteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Property
import org.hibernate.criterion.Restrictions

import org.springframework.beans.factory.annotation.Autowired
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class LicensorHibernateIntSpec extends Specification {

    @Autowired
    SessionFactory sessionFactory

    void "querying directly using hibernate works"() {
        given:
        
        LicensorIntSpec other = new LicensorIntSpec();
        other.setupData();

        DetachedCriteria dc = DetachedCriteria.forClass(LicenseEntitlement, '_ent');
        dc.add(Restrictions.eq('assetId', 2));
        dc.add( Restrictions.or(
                Restrictions.in('groupId', [101,102,104]),
                Restrictions.in('licenseId', [1,2,4]) ) );

        dc.createAlias("licensor", "_lic");
        dc.setProjection( Projections.distinct(Projections.property('_lic.id')) );


        DetachedCriteria dc2 = DetachedCriteria.forClass(Licensor);
        dc2.add( Property.forName("id").in(dc) );
        dc2.addOrder( Order.asc('email') );

        Criteria criteria = dc2.getExecutableCriteria( sessionFactory.getCurrentSession() );
        criteria.setFirstResult(0);
        criteria.setMaxResults(5);

        def results = criteria.list();

        expect:
        results.size() <= 5;
    }

}
