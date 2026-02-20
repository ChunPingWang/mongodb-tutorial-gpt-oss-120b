package com.course.mongodb.m09.service;

import com.course.mongodb.m09.domain.Transfer;
import com.course.mongodb.m09.repository.TransferRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository repository;

    public TransferService(TransferRepository repository) {
        this.repository = repository;
    }

    public Transfer createTransfer(String fromAccount, String toAccount, Double amount, String status) {
        Transfer transfer = new Transfer(fromAccount, toAccount, amount, status);
        return repository.save(transfer);
    }

    public List<Transfer> findAll() {
        return repository.findAll();
    }

    public Transfer findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Transfer> findByFromAccount(String fromAccount) {
        return repository.findByFromAccount(fromAccount);
    }

    public List<Transfer> findByToAccount(String toAccount) {
        return repository.findByToAccount(toAccount);
    }

    public List<Transfer> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Transfer updateStatus(String id, String status) {
        Transfer transfer = findById(id);
        if (transfer != null) {
            transfer.setStatus(status);
            return repository.save(transfer);
        }
        return null;
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
