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
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            TreeNode reference = treeNode.get("reference");
            TreeNode accountIban = treeNode.get("account_iban");
            TreeNode date = treeNode.get("date");
            TreeNode amount = treeNode.get("amount");
            TreeNode fee = treeNode.get("fee");
            TreeNode description = treeNode.get("description");
            return Transaction.builder()
                    .reference(getStringFromNode(reference))
                    .account(getStringFromNode(accountIban))
                    .date(getDateFromNode(date))
                    .amount(getDoubleFromNode(amount))
                    .fee(getDoubleFromNode(fee))
                    .description(getStringFromNode(description))
                    .build();
        }

        /**
         * Gets the date from the given {@link TreeNode}
         * @param dateNode the Node
         * @return date value if the node is not empty and it is in the format.
         */
        private Date getDateFromNode(final TreeNode dateNode) {
            if (dateNode instanceof TextNode) {
                try {
                    String dateNodeValue = (dateNode != null) ? ((TextNode)dateNode).asText() : null;
                    return StringUtils.isNotEmpty(dateNodeValue) ? formatter.parse(dateNodeValue) : null;
                } catch (ParseException ex) {
                    logger.error(ex.getMessage());
                    throw new InvalidParameterException("Date could not be parsed, use the format: yyyy-MM-dd'T'kk:mm:ss.SSS'Z'");
                }
            }
            return null;
        }

        /**
         * Gets the double value from the given {@link TreeNode}
         * @param node the node
         * @return double value if the node is not empty and it is in not null.
         */
        private Double getDoubleFromNode(final TreeNode node) {
            Double doubleValue = null;
            if (node instanceof DoubleNode) {
                doubleValue = ((DoubleNode) node).doubleValue();
            } else if (node instanceof IntNode) {
                doubleValue = ((IntNode) node).doubleValue();
            }
            return doubleValue;
        }

        /**
         * Gets the String value from the given {@link TreeNode}
         * @param node the node
         * @return String value if the node is not empty and it is not null.
         */
        private String getStringFromNode(final TreeNode node) {
            String value = null;
            if (node instanceof TextNode) {
                value = ((TextNode) node).textValue();
            }
            return value;
        }
    }
}