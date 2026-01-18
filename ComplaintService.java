package com.example.ccms_1.Services;


import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Repositories.ComplaintRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Transactional
    public Complaint markAssigned(Complaint complaint) {
        complaint.markAssigned();
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint markEscalated(Complaint complaint) {
        complaint.markEscalated();
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint markResolved(Complaint complaint) {
        complaint.markResolved();
        return complaintRepository.save(complaint);
    }

}

