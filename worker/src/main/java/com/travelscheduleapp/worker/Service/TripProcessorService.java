package com.travelscheduleapp.worker.Service;

import com.travelscheduleapp.worker.Entity.Activity;
import com.travelscheduleapp.worker.Entity.Trip;
import com.travelscheduleapp.worker.Entity.TripStatus;
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

        trip.setTripStatus(TripStatus.PROCESSING);
        tripRepository.save(trip);

        try {
            List<AiActivityDto> generatedActivities = aiService.generateItinerary(
                    trip.getDestination(),
                    trip.getNumberOfDays(),
                    "Standard"
            );

            int index = 1;
            for (AiActivityDto dto : generatedActivities) {
                Activity activity = new Activity();

                activity.setDescription(dto.getDescription());
                activity.setDuration(dto.getDuration());
                activity.setDayNumber(dto.getDay());

                activity.setOrderIndex(index++);
                activity.setTrip(trip);

                activityRepository.save(activity);
            }

            trip.setTripStatus(TripStatus.COMPLETED);
            tripRepository.save(trip);
            System.out.println(" [Worker] Trip ID " + tripId + " finished successfully with " + generatedActivities.size() + " activities!");

        } catch (Exception e) {
            e.printStackTrace();
            trip.setTripStatus(TripStatus.FAILED);
            tripRepository.save(trip);
        }
    }
}