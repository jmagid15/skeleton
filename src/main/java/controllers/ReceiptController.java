package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptController {
    final ReceiptDao receipts;
    final TagDao tags;

    public ReceiptController(ReceiptDao receipts, TagDao tags) {
        this.receipts = receipts;
        this.tags = tags;
    }

    @POST
    @Path("/receipts")
    public int createReceipt(@Valid @NotNull CreateReceiptRequest receipt) {
        return receipts.insert(receipt.merchant, receipt.amount);
    }

    @GET
    @Path("/receipts")
    public List<ReceiptResponse> getReceipts() {
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();
        return receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
    }

    @PUT
    @Path("/tags/{tag}")
    public void toggleTag(@PathParam("tag") String tagName, Integer receiptID) {
        if (tags.exists(receiptID, tagName)) {
            tags.delete(receiptID, tagName);
        } else {
            tags.insert(tagName, receiptID);
        }

    }

    @GET
    @Path("/tags/{tag}")
    public List<ReceiptResponse> getReceipts(@PathParam("tag") String tagName) {
        return tags.getAllReceiptsForTag(tagName)
                .stream()
                .map(ReceiptResponse::new)
                .collect(toList());

    }

}
