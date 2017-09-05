package api;

import dao.TagDao;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;
import io.dropwizard.jersey.validation.Validators;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class ReceiptTagTest {
    public static org.jooq.Configuration setupJooq() {
        // For now we are just going to use an H2 Database.  We'll upgrade to mysql later
        // This connection string tells H2 to initialize itself with our schema.sql before allowing connections
        final String jdbcUrl = "jdbc:h2:mem:test;MODE=MySQL;INIT=RUNSCRIPT from 'classpath:schema.sql'";
        JdbcConnectionPool cp = JdbcConnectionPool.create(jdbcUrl, "sa", "sa");

        // This sets up jooq to talk to whatever database we are using.
        org.jooq.Configuration jooqConfig = new DefaultConfiguration();
        jooqConfig.set(SQLDialect.MYSQL);   // Lets stick to using MySQL (H2 is OK with this!)
        jooqConfig.set(cp);
        return jooqConfig;
    }

    org.jooq.Configuration jooqConfig = setupJooq();

    private final Validator validator = Validators.newValidator();
    private final TagDao tagDao = new TagDao(jooqConfig);
    private final ReceiptDao receiptDao = new ReceiptDao(jooqConfig);

    @Test
    public void testValid() {
        Integer receiptID1 = receiptDao.insert("Wholesome Foods", new BigDecimal(2.50));
        Integer receiptID2 = receiptDao.insert("Fuji Japanese", new BigDecimal(26.47));
        Integer receiptID3 = receiptDao.insert("Duane Reade", new BigDecimal(63.78));

        tagDao.insert("Meals", receiptID1);
        tagDao.insert("Meals", receiptID2);
        tagDao.insert("Pharmacy", receiptID3);

        List<ReceiptsRecord> receipts1 = tagDao.getAllReceiptsForTag("Meals");
        List<ReceiptsRecord> receipts2 = tagDao.getAllReceiptsForTag("Pharmacy");
        Assert.assertEquals(receipts1.size(), 2);
        Assert.assertEquals(receipts2.size(), 1);
        Assert.assertTrue(tagDao.exists(receiptID1, "Meals"));
        Assert.assertTrue(tagDao.exists(receiptID2, "Meals"));
        Assert.assertTrue(tagDao.exists(receiptID3, "Pharmacy"));

    }

}