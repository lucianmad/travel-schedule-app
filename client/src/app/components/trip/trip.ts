import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TripService } from '../../services/trip.service';
import { ActivityService } from '../../services/activity.service';
import { Trip } from '../../models/trip';
import { TripRequest } from '../../models/trip-request';
import { ActivityRequest } from '../../models/activity-request';

@Component({
  selector: 'app-trip',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trip.html',
  styleUrl: './trip.css'
})
export class TripComponent implements OnInit {
  private tripService = inject(TripService);
  private activityService = inject(ActivityService);

  trips = signal<Trip[]>([]);
  selectedTrip = signal<Trip | null>(null);
  
  isCreateOpen = signal(false);
  isDetailOpen = signal(false);

  newTrip: TripRequest = { destination: '', numberOfDays: 1 };

  ngOnInit() {
    this.loadTrips();
  }

  loadTrips() {
    this.tripService.get().subscribe({
      next: (data) => this.trips.set(data),
      error: (e) => console.error(e)
    });
  }

  // --- Create Trip ---
  saveTrip() {
    this.tripService.create(this.newTrip).subscribe(createdTrip => {
      this.trips.update(list => [...list, createdTrip]);
      this.isCreateOpen.set(false);
      this.newTrip = { destination: '', numberOfDays: 1 };
    });
  }

  // --- View Trip Details ---
  selectTrip(trip: Trip) {
    this.tripService.getById(trip.id).subscribe(fullTrip => {
      this.selectedTrip.set(fullTrip);
      this.isDetailOpen.set(true);
    });
  }

  deleteTrip(id: number) {
    this.tripService.delete(id).subscribe(() => {
      this.trips.update(list => list.filter(t => t.id !== id));
      this.closeModals();
    });
  }


  deleteActivity(activityId: number) {
    this.activityService.delete(activityId).subscribe(() => {
      this.selectedTrip.update(t => {
        if (!t) return null;
        return { ...t, activities: t.activities.filter(a => a.id !== activityId) };
      });
    });
  }

  closeModals() {
    this.isCreateOpen.set(false);
    this.isDetailOpen.set(false);
    this.selectedTrip.set(null);
  }
}