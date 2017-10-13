package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.regex.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = "";
            BigDecimal amount = null;

            String[] lines = res.getTextAnnotationsList().get(0).getDescription().split("[\t\n\r]");

            merchantName = lines[0];
            String amtRegExp = "\\d+\\.\\d+";
            out.printf("Merchant name %s\n",merchantName);

            for (int i=lines.length-1; i>=0; i--){
                // Reference: https://stackoverflow.com/questions/9991750/how-to-split-a-decimal-number-from-a-string-in-java
                String tempLine = lines[i].trim();
                Pattern p = Pattern.compile(amtRegExp);
                Matcher m = p.matcher(tempLine);
                if (m.find()) {
                    amount = new BigDecimal(m.group());
                    break;
                }
            }

            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }
}
