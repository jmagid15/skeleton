package dao;

import generated.tables.records.ReceiptsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public void insert(String tagName, Integer receiptID) {
        dsl.insertInto(TAGS, TAGS.NAME, TAGS.RECEIPT_ID)
                .values(tagName, receiptID)
                .returning()
                .fetchOne();

    }


    public boolean exists(Integer receiptID, String tagName) {

        return (dsl.selectCount()
                .from(TAGS)
                .where(TAGS.RECEIPT_ID.equal(receiptID))
                .and(TAGS.NAME.equal(tagName))
                .fetchOne(0, Integer.class) > 0);

    }

    public void delete(Integer receiptID, String tagName) {

        dsl.delete(TAGS)
                .where(TAGS.RECEIPT_ID.equal(receiptID))
                .and(TAGS.NAME.equal(tagName)).execute();

    }

    public List<ReceiptsRecord> getAllReceiptsForTag(String tagName) {

        return dsl.select()
                .from(TAGS)
                .join(RECEIPTS)
                .on(TAGS.RECEIPT_ID.eq(RECEIPTS.ID))
                .where(TAGS.NAME.equal(tagName))
                .fetchInto(RECEIPTS);
    }

}
