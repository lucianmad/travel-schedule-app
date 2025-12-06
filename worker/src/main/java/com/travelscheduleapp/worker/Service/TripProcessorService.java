package com.travelscheduleapp.worker.Service;

import com.travelscheduleapp.worker.Entity.Activity;
import com.travelscheduleapp.worker.Entity.Trip;
import com.travelscheduleapp.worker.Repository.ActivityRepo;
import com.travelscheduleapp.worker.Repository.TripRepo;
import com.travelscheduleapp.worker.Config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TripProcessorService {

    @Autowired
    private TripRepo tripRepository;

    @Autowired
    private ActivityRepo activityRepository;

    @Autowired
    private AiGenerationService aiService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void processTripRequest(Long tripId) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(" [Worker] Received Trip ID: " + tripId);

        var tripOptional = tripRepository.findById(tripId);
        if (tripOptional.isEmpty()) {
            System.out.println(" [Worker] Trip ID " + tripId + " not found. Ignoring.");
            return;
        }

        Trip trip = tripOptional.get();

        trip.setTripStatus("PROCESSING");
        tripRepository.save(trip);

        try {
            List<String> generatedActivities = aiService.generateItinerary(trip.getDestination(), "Standard");

            int index = 1;
            for (String actText : generatedActivities) {
                Activity activity = new Activity();

                activity.setDescription(actText);       // Textul de la AI
                activity.setDuration("2 hours");        // Valoare default obligatorie
                activity.setDayNumber(1);               // Valoare default obligatorie
                activity.setOrderIndex(index++);        // Ordinea (1, 2, 3...)
                activity.setTrip(trip);                 // Legătura cu Trip

                activityRepository.save(activity);
            }

            trip.setTripStatus("COMPLETED");
            tripRepository.save(trip);
            System.out.println(" [Worker] Trip ID " + tripId + " finished successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            trip.setTripStatus("FAILED");
            tripRepository.save(trip);
        }
    }
}