package com.codechallenge.bank.serializer;

import com.codechallenge.bank.exception.InvalidParameterException;
import com.codechallenge.bank.model.Transaction;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Custom serializer/deserializer for {@link Transaction} objects
 *
 * @author Carlos Rodriguez
 * @since 26/07/2019
 */
@JsonComponent
public class TransactionSerializer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSerializer.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS'Z'");

    /**
     * Custom serializer for {@link Transaction} objects
     *
     * @author Carlos Rodriguez
     * @since 26/07/2019
     */
    public static class TransactionJsonSerializer extends JsonSerializer<Transaction> {

        @Override
        public void serialize(Transaction transaction, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("reference", transaction.getReference());
            jsonGenerator.writeStringField("account_iban", transaction.getAccount().getIban());
            jsonGenerator.writeStringField("date", transaction.getDate() != null ? formatter.format(transaction.getDate()) : null);
            jsonGenerator.writeNumberField("amount", transaction.getAmount());
            if (transaction.getFee() != null) {
                jsonGenerator.writeNumberField("fee", transaction.getFee());
            } else {
                jsonGenerator.writeStringField("fee", null);
            }
            jsonGenerator.writeStringField("description", transaction.getDescription());
            jsonGenerator.writeEndObject();
        }
    }

    /**
     * Custom deserializer for {@link Transaction} objects
     *
     * @author Carlos Rodriguez
     * @since 26/07/2019
     */
    public static class TransactionJsonDeserializer extends JsonDeserializer<Transaction> {

        @Override
        public Transaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
            TextNode reference = (TextNode) treeNode.get("reference");
            TextNode accountIban = (TextNode) treeNode.get("account_iban");
            TextNode date = (TextNode) treeNode.get("date");
            DoubleNode amount = (DoubleNode) treeNode.get("amount");
            DoubleNode fee = (DoubleNode) treeNode.get("fee");
            TextNode description = (TextNode) treeNode.get("description");
            try {
                return Transaction.builder()
                        .reference(reference != null ? reference.asText() : null)
                        .account(accountIban != null ? accountIban.asText() : null)
                        .date(date != null ? formatter.parse(date.asText()) : null)
                        .amount(amount != null ? amount.asDouble() : 0)
                        .fee(fee != null ? fee.asDouble() : null)
                        .description(description != null ? description.asText() : null)
                        .build();
            } catch (ParseException ex) {
                logger.error(ex.getMessage());
                throw new InvalidParameterException("Transaction could not be deserialized, error=" + ex.getMessage());
            }
        }
    }
}