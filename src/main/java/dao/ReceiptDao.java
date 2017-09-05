package dao;

import api.ReceiptResponse;
import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.Tables.TAGS;

public class ReceiptDao {
    DSLContext dsl;

    public ReceiptDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String merchantName, BigDecimal amount) {
        ReceiptsRecord receiptsRecord = dsl
                .insertInto(RECEIPTS, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT)
                .values(merchantName, amount)
                .returning(RECEIPTS.ID)
                .fetchOne();

        checkState(receiptsRecord != null && receiptsRecord.getId() != null, "Insert failed");

        return receiptsRecord.getId();
    }

    public List<ReceiptsRecord> getAllReceipts() {

        return dsl.selectFrom(RECEIPTS).fetch();
    }

//    public boolean idExists(Integer receiptID) {
//        return dsl.fetchExists(RECEIPTS, RECEIPTS.ID.eq(receiptID));
//    }
//
//    public void toggleTagReceipt(Integer receiptID, Integer tagID) {
//        // Finds record in tags table
//        List<TagsRecord> tagsRecords = dsl.selectFrom(TAGS)
//                .where(TAGS.RECEIPT_ID.eq(receiptID).and(TAGS.RECEIPT_ID.eq(tagID))).fetch();
//
//        // Delete if entry exists
//        if (tagsRecords.size() > 0) {
//            dsl.delete(TAGS)
//                    .where(TAGS.RECEIPT_ID.eq(receiptID))
//                    .and(TAGS.RECEIPT_ID.eq(tagID))
//                    .execute();
//        }
//
//        // Otherwise create new entry with receipt and tag info
//        else {
//            dsl.insertInto(TAGS, TAGS.RECEIPT_ID, TAGS.RECEIPT_ID)
//                    .values(receiptID, tagID).execute();
//        }
//    }
//
//    public List<ReceiptsRecord> getReceiptsForTag(Integer tagID) {
//        Result<Record3<Integer, String, BigDecimal>> result = dsl.select(RECEIPTS.ID, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT).from(RECEIPTS)
//                .innerJoin(TAGS).on(RECEIPTS.ID.eq(TAGS.RECEIPT_ID))
//                .where(TAGS.RECEIPT_ID.eq(tagID))
//                .fetch();
//
//        List<ReceiptsRecord> receiptsRecords = new ArrayList<>();
//
//        for (Record3 r: result) {
//            ReceiptsRecord receiptsRecord = new ReceiptsRecord();
//            receiptsRecord.setId((Integer)r.getValue(0));
//            receiptsRecord.setMerchant((String)r.getValue(1));
//            receiptsRecord.setAmount((BigDecimal) r.getValue(2));
//            receiptsRecords.add(receiptsRecord);
//        }
//
//        return receiptsRecords;
//    }
}
