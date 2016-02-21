package gormDc

import grails.orm.HibernateCriteriaBuilder
import org.springframework.beans.factory.annotation.Autowired
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

import grails.gorm.DetachedCriteria

import org.hibernate.SessionFactory


@Integration
@Rollback
class DetCritToDetCritIntSpec extends Specification {

    @Autowired
    SessionFactory sessionFactory

    /* This fails with NPE inside the current GORM code */
    void "should be able to get raw hibernate objects from gorm"()
    {

        given:
        // generates some test data:
        (new LicensorIntSpec()).setupData();

        DetachedCriteria gormDetachedCriteria = new DetachedCriteria(Licensor).build {
            like('email', '%-1%')
            not {
                like('email', 'donot%')
            }
        }

        // Now we would like to get a paged set, for instance if the anticipation is for a moderatly large list to process:
        // org.hibernate.criterion.DetachedCriteria
        def hibernateDetachedCriteria = HibernateCriteriaBuilder.getHibernateDetachedCriteria(null, gormDetachedCriteria)
        def criteria = hibernateDetachedCriteria.getExecutableCriteria( sessionFactory.currentSession )

        if (!criteria.projection) criteria.projection = null
        def scrollableResults = criteria.scroll(ScrollMode.FORWARD_ONLY)

        expect:
        scrollableResults.next();
        scrollableResults.get(0);

    }

    void "check that the proposed gorm query works"()
    {

        given:
        // generates some test data:
        (new LicensorIntSpec()).setupData();

        DetachedCriteria detachedCriteria = new DetachedCriteria(Licensor).build {
            like('email', '%-1%')
            not {
                like('email', 'donot%')
            }
        }

        def results = detachedCriteria.list();      // should be non-null

        expect:
        null != results
        results.size() > 0

    }

}
