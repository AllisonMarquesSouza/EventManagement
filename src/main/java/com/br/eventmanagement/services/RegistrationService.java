package com.br.eventmanagement.services;

import com.br.eventmanagement.dtos.registration.RegistrationCreateDto;
import com.br.eventmanagement.entity.Event;
import com.br.eventmanagement.entity.Registration;
import com.br.eventmanagement.entity.User;
import com.br.eventmanagement.exceptions.BadRequestException;
import com.br.eventmanagement.exceptions.EntityAlreadyExistsException;
import com.br.eventmanagement.repositories.RegistrationRepository;
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
    private final UserService userService;

    public Registration getById(UUID registrationId){
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));
    }

    public List<Registration> listAll(){
        return registrationRepository.findAll();
    }

    public List<Registration> findAllByUserId(UUID userId){
        userService.getById(userId);//checking if user exists and everything is fine
        return registrationRepository.findAllByUserId(userId);
    }

    public List<Registration> findAllByEventId(UUID eventId){
        eventService.getById(eventId);//checking if event exists and everything is fine
        return registrationRepository.findAllByEventId(eventId);
    }

    @Transactional
    public Registration create(RegistrationCreateDto createDto){
        if(registrationRepository.existsRegistrationByUserIdAndEventId(createDto.userId(), createDto.eventId())){
            throw new EntityAlreadyExistsException("This user is already registered");
        }

        if(!eventService.isAvailableFreeSpot(createDto.eventId())){
            throw new BadRequestException("There is no spot available in this event");
        }

        //after create, adding one more participant ... //
        Event event = eventService.getById(createDto.eventId());
        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);
        User user = userService.getById(createDto.userId());
        return registrationRepository.save(new Registration(user, event));

    }

    @Transactional
    public void deleteByUserIdAndEventId(UUID userId, UUID eventId){
        //do more verifications before, see if exists user and event
        Registration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no registration of this user to this event"));

        registrationRepository.delete(registration);
    }

    @Transactional
    public void deleteAllByUserId(UUID userId){
        List<Registration> allByUserId = this.findAllByUserId(userId);
        if(allByUserId.isEmpty()) return; //just to return if is empty, but the 'for' already does it (better to read)

        for(Registration currentRegister : allByUserId){
            registrationRepository.delete(currentRegister);
            currentRegister.getEvent().setRegisteredParticipants
                    (currentRegister.getEvent().getRegisteredParticipants() - 1);
        }
    }

    @Transactional
    public void deleteAllByEventId(UUID eventId){
        Event event = eventService.getById(eventId);
        registrationRepository.deleteAllByEventId(eventId);
        event.setRegisteredParticipants(0);
    }

    @Transactional
    public void deleteById(UUID registrationId){
        Registration registration = this.getById(registrationId);
        registration.getEvent().setRegisteredParticipants(registration.getEvent().getRegisteredParticipants() - 1);
        registrationRepository.delete(registration);
    }
}
