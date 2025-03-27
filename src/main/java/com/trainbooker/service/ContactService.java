package com.trainbooker.service;

import com.trainbooker.dto.ContactMessageDTO;
import com.trainbooker.model.ContactMessage;
import com.trainbooker.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    @Autowired
    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public void saveContactMessage(ContactMessageDTO messageDTO) {
        ContactMessage message = new ContactMessage();
        message.setName(messageDTO.getName());
        message.setEmail(messageDTO.getEmail());
        message.setPhone(messageDTO.getPhone());
        message.setSubject(messageDTO.getSubject());
        message.setMessage(messageDTO.getMessage());
        message.setSubmittedAt(LocalDateTime.now());

        contactMessageRepository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAll();
    }
}

