package com.bank.repository;

import com.bank.model.Account;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * File-based implementation of AccountRepository.
 * Serializes the account database to disk so state persists across application restarts.
 */
public class FileAccountRepository implements AccountRepository {
    private static final String DEFAULT_DATA_DIR = "data";
    private static final String DEFAULT_FILE_NAME = "accounts.dat";

    private final Path storagePath;
    private final Map<String, Account> accountMap;
    private final AtomicInteger accountSequence;

    public FileAccountRepository() {
        this(Paths.get(DEFAULT_DATA_DIR, DEFAULT_FILE_NAME));
    }

    public FileAccountRepository(Path storagePath) {
        this.storagePath = storagePath;
        this.accountMap = new ConcurrentHashMap<>();
        this.accountSequence = new AtomicInteger(1001);
        loadAccounts();
    }

    @Override
    public synchronized Account save(Account account) {
        accountMap.put(account.getAccountNumber(), account);
        persistChanges();
        return account;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        return Optional.ofNullable(accountMap.get(accountNumber.trim()));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accountMap.values());
    }

    @Override
    public synchronized boolean delete(String accountNumber) {
        if (accountNumber != null && accountMap.remove(accountNumber.trim()) != null) {
            persistChanges();
            return true;
        }
        return false;
    }

    @Override
    public synchronized String generateNextAccountNumber() {
        int nextNum = accountSequence.getAndIncrement();
        return String.format("ACC%06d", nextNum);
    }

    @Override
    public synchronized void persistChanges() {
        try {
            if (storagePath.getParent() != null && !Files.exists(storagePath.getParent())) {
                Files.createDirectories(storagePath.getParent());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(storagePath)))) {
                oos.writeInt(accountSequence.get());
                oos.writeObject(new HashMap<>(accountMap));
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to persist bank data to disk: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadAccounts() {
        if (!Files.exists(storagePath)) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(storagePath)))) {
            int savedSequence = ois.readInt();
            accountSequence.set(savedSequence);

            Map<String, Account> loaded = (Map<String, Account>) ois.readObject();
            accountMap.putAll(loaded);
        } catch (Exception e) {
            System.err.println("Notice: Initializing new database (" + e.getMessage() + ")");
        }
    }
}
