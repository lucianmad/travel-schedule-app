package com.travelscheduleapp.worker.Service;

import com.travelscheduleapp.worker.Entity.Activity;
import com.travelscheduleapp.worker.Entity.Trip;
import com.travelscheduleapp.worker.Entity.TripStatus;
import com.travelscheduleapp.worker.Repository.ActivityRepo;
import com.travelscheduleapp.worker.Repository.TripRepo;
import com.travelscheduleapp.worker.Config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripProcessorService {

    private final TripRepo tripRepository;
    private final ActivityRepo activityRepository;
    private final AiGenerationService aiService;

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
                    trip.getNumberOfDays()
            );

            Map<Integer, List<AiActivityDto>> activitiesByDay = generatedActivities.stream()
                    .collect(Collectors.groupingBy(AiActivityDto::getDay));

            for (Map.Entry<Integer, List<AiActivityDto>> entry : activitiesByDay.entrySet()) {
                List<AiActivityDto> dayActivities = entry.getValue();
                int dayIndex = 0;

                for (AiActivityDto dto : dayActivities) {
                    Activity activity = new Activity();

                    activity.setDescription(dto.getDescription());
                    activity.setDuration(dto.getDuration());
                    activity.setDayNumber(dto.getDay());

                    activity.setOrderIndex(dayIndex++);

                    activity.setTrip(trip);
                    activityRepository.save(activity);
                }
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