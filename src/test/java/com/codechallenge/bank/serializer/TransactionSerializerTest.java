package com.codechallenge.bank.serializer;

import com.codechallenge.bank.model.Transaction;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Carlos Rodriguez
 * @since 26/07/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionSerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @Before
    public void setup() {
    }

    @Test
    public void serialize_dontThrowException() {
        try {
            Transaction transaction = Transaction.builder().account("ABC123").amount(100).build();
            TransactionSerializer.TransactionJsonSerializer jsonSerializer = new TransactionSerializer.TransactionJsonSerializer();
            jsonSerializer.serialize(transaction, jsonGenerator, serializerProvider);
        } catch (IOException ex) {
            fail("Not expecting exception");
        }
    }

    @Test
    public void deserialize_emptyTransaction() throws IOException {
        String expectedReference = "12345A";
        String expectedIban = "ES9820385778983000760236";
        double expectedAmount = 100;
        double expectedFee = 1.2;

        ObjectCodec objectCodec = Mockito.mock(ObjectCodec.class);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        when(jsonParser.getCodec()).thenReturn(objectCodec);
        when(objectCodec.readTree(jsonParser)).thenReturn(treeNode);

        TextNode referenceNode = new TextNode(expectedReference);
        TextNode accountIbanNode = new TextNode(expectedIban);
        DoubleNode amountNode = new DoubleNode(expectedAmount);
        DoubleNode feeNode = new DoubleNode(expectedFee);

        when(treeNode.get("reference")).thenReturn(referenceNode);
        when(treeNode.get("account_iban")).thenReturn(accountIbanNode);
        when(treeNode.get("amount")).thenReturn(amountNode);
        when(treeNode.get("fee")).thenReturn(feeNode);

        TransactionSerializer.TransactionJsonDeserializer deserializer = new TransactionSerializer.TransactionJsonDeserializer();
        Transaction transaction = deserializer.deserialize(jsonParser, deserializationContext);
        assertEquals(expectedReference, transaction.getReference());
        assertEquals(expectedIban, transaction.getAccount().getIban());
        assertEquals(expectedAmount, transaction.getAmount(), 0);
        assertEquals(expectedFee, transaction.getFee(), 0);
        assertNull(transaction.getDescription());
    }
}