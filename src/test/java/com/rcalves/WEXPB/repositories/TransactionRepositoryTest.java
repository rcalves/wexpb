package com.rcalves.WEXPB.repositories;

import com.rcalves.WEXPB.entities.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    @BeforeEach
    public void setup() {
        transaction = new Transaction();
        transaction.setDescription("Test Transaction");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPurchaseAmount(new BigDecimal("99.99"));
    }

    @Test
    public void whenSaved_thenFindsById() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertTrue(retrievedTransaction.isPresent());
        assertEquals(savedTransaction.getDescription(), retrievedTransaction.get().getDescription());
    }

    @Test
    public void whenSaved_thenFindsAll() {
        transactionRepository.save(transaction);
        assertFalse(transactionRepository.findAll().isEmpty());
    }

    @Test
    public void whenDeleted_thenNotFindsById() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionRepository.deleteById(savedTransaction.getId());
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertFalse(retrievedTransaction.isPresent());
    }
}
