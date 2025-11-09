package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.registration.RegistrationCreateDto;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.entity.Registration;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.repositories.RegistrationRepository;
import com.br.eventmanagement.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventService eventService;
    private final UserRepository userRepository;

    public Registration getById(UUID registrationId){
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));
    }

    public List<Registration> listAll(){
        return registrationRepository.findAll();
    }

    public List<Registration> findAllByUserId(UUID userId){
        return registrationRepository.findAllByUserId(userId);
    }

    public List<Registration> findAllByEventId(UUID eventId){
        return registrationRepository.findAllByEventId(eventId);
    }

    @Transactional
    public Registration create(RegistrationCreateDto createDto){
        if(eventService.isAvailableFreeSpot(createDto.eventId()) &&
                !registrationRepository.existsRegistrationByUserIdAndEventId(createDto.userId(), createDto.eventId())){

            //after create, adding one more participant ... //
            Event event = eventService.getById(createDto.eventId());
            event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);

            User user = userRepository.findById(createDto.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return registrationRepository.save(new Registration(user, event));
        }
        throw new EntityAlreadyExistsException("There is no spot available in this event or You are already registered");
    }

    @Transactional
    public void deleteByUserIdAndEventId(UUID userId, UUID eventId){
        registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no registration of this user to this event"));

        registrationRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    @Transactional
    public void deleteAllByUserId(UUID userId){
        List<Registration> allByUserId = findAllByUserId(userId);

        for(Registration currentRegister : allByUserId){
            registrationRepository.delete(currentRegister);
            currentRegister.getEvent().setRegisteredParticipants
                    (currentRegister.getEvent().getRegisteredParticipants() - 1);
        }
    }

    @Transactional
    public void deleteAllByEventId(UUID eventId){
        registrationRepository.deleteAllByEventId(eventId);
        Event event = eventService.getById(eventId);
        event.setRegisteredParticipants(0);
    }

    @Transactional
    public void deleteById(UUID registrationId){
        Registration registration = getById(registrationId);
        registration.getEvent().setRegisteredParticipants(registration.getEvent().getRegisteredParticipants() - 1);
        registrationRepository.delete(registration);
    }
}
