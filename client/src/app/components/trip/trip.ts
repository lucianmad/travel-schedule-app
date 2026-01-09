import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { Observable, Subject, switchMap, startWith, map, interval, tap, takeWhile } from 'rxjs';

import { TripService } from '../../services/trip.service';
import { ActivityService } from '../../services/activity.service';
import { Trip } from '../../models/trip';
import { TripRequest } from '../../models/trip-request';
import { Activity } from '../../models/activity';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { ReorderRequest } from '../../models/reorder-request';
import { ActivityUpdateRequest } from '../../models/activity-update-request';
import { ActivityCreateRequest } from '../../models/activity-create-request';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-trip',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon, DragDropModule],
  templateUrl: './trip.html',
  styleUrl: './trip.css'
})
export class TripComponent {

  constructor(
    private tripService: TripService,
    private activityService: ActivityService,
    private authService: AuthService
  ) {
    this.authService.currentUser$.subscribe(user => {
      this.closeModals();
      this.refresh.next();
    });
  }

  private refresh = new Subject<void>();

  trips: Observable<Trip[]> = this.refresh.pipe(
    startWith(void 0),
    switchMap(() => this.tripService.get().pipe(
      map(trips => trips.map(t => ({
        ...t,
        activities: [...t.activities].sort((a, b) => {
          if (a.dayNumber !== b.dayNumber)
            return a.dayNumber - b.dayNumber;
          
          return a.orderIndex - b.orderIndex;
        })
      })))
    ))
  );

  selectedTrip: Trip | null = null;
  isCreateOpen = false;
  isDetailOpen = false;

  isEditTripOpen = false;
  isEditActivityOpen = false;

  isAddActivityOpen = false;
  addActivityDay!: number;

  isOrderDirty = false;

  newTrip: TripRequest = {
    destination: '',
    numberOfDays: 1
  };

  editTrip: TripRequest = {
    destination: '',
    numberOfDays: 1
  };

  editActivity: ActivityUpdateRequest = {
    duration: '',
    description: ''
  };
  editActivityId: number | null = null;

  newActivity: ActivityCreateRequest = {
    dayNumber: 1,
    duration: '',
    description: ''
  };

  activitiesByDay = new Map<number, Activity[]>();

  groupedActivities: {
    day: number;
    activities: Activity[] 
  }[] = [];

  saveTrip(): void {
    this.tripService.create(this.newTrip).subscribe({
      next: (createdTrip) => {
        this.isCreateOpen = false;
        this.newTrip = {
          destination: '',
          numberOfDays: 1 
        };
        this.refresh.next();

        this.pollTripUntilCompleted(createdTrip.id);
      },
      error: err => console.error(err)
    });
  }

  private pollTripUntilCompleted(tripId: number): void {
    interval(2000)
      .pipe(
        switchMap(() => this.tripService.getById(tripId)),
        tap(trip => {
          if (trip.status !== 'PENDING' && trip.status !== 'PROCESSING') {
            this.refresh.next();
          }
        }),
        takeWhile(trip => trip.status === 'PENDING' || trip.status === 'PROCESSING', true)
      )
      .subscribe({
        complete: () => {
          console.log(`Trip ${tripId} completed`);
        },
        error: err => console.error(err)
      });
  }

  selectTrip(trip: Trip) {
    this.selectedTrip = trip;
    this.isDetailOpen = true;

    this.buildGroupedActivities(trip);
    this.isOrderDirty = false;
  }

  deleteTrip(id: number): void {
    this.tripService.delete(id).subscribe({
      next: () => {
        this.closeModals();
        this.refresh.next();
      },
      error: err => console.error(err)
    });
  }

  deleteActivity(activityId: number) {
    if (!this.selectedTrip) return;

    this.activityService.delete(activityId).subscribe(() => {
      this.groupedActivities.forEach(group => {
        group.activities = group.activities.filter(a => a.id !== activityId);
      });

      this.selectedTrip!.activities =
        this.groupedActivities.flatMap(g => g.activities);

      this.isOrderDirty = true;
    });
  }

  openEditTrip(trip: Trip): void {
    this.editTrip = {
      destination: trip.destination,
      numberOfDays: trip.numberOfDays
    };
    this.selectedTrip = trip;
    this.isEditTripOpen = true;
  }

  saveEditTrip(): void {
    if (!this.selectedTrip) return;

    this.tripService.update(this.editTrip, this.selectedTrip.id).subscribe({
      next: updatedTrip => {
        this.isEditTripOpen = false;
        this.refresh.next();

        this.selectedTrip = updatedTrip;
      },
      error: err => console.error(err)
    });
  }

  openEditActivity(activity: Activity) {
    this.editActivity = {
      duration: activity.duration,
      description: activity.description
    };
    this.editActivityId = activity.id;
    this.isEditActivityOpen = true;
  }

  saveEditActivity() {
    this.activityService.update(this.editActivity, this.editActivityId!).subscribe(updated => {
      this.groupedActivities.forEach(group => {
        group.activities = group.activities.filter(a => a.id !== updated.id);
      });

      const targetGroup = this.groupedActivities.find(
        g => g.day === updated.dayNumber
      );

      if (targetGroup) {
        targetGroup.activities.push(updated);
        targetGroup.activities.sort((a, b) => a.orderIndex - b.orderIndex);
      }

      this.selectedTrip!.activities =
        this.groupedActivities.flatMap(g => g.activities);

      this.isEditActivityOpen = false;

      this.refresh.next();
    });
  }

  getActivitiesByDay(trip: Trip): { day: number; activities: Activity[] }[] {
    if (!trip?.activities) return [];

    const map = new Map<number, Activity[]>();

    trip.activities.forEach(act => {
      if (!map.has(act.dayNumber)) {
        map.set(act.dayNumber, []);
      }
      map.get(act.dayNumber)!.push(act);
    });

    const result = Array.from(map.entries()).map(([day, activities]) => ({
      day,
      activities: activities.sort((a, b) => a.orderIndex - b.orderIndex)
    }));

    return result.sort((a, b) => a.day - b.day);
  }

  onDrop(event: CdkDragDrop<Activity[]>, targetDay: number) {
    if (event.previousContainer === event.container) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );

      event.container.data[event.currentIndex].dayNumber = targetDay;
    }

    event.container.data.forEach((act, idx) => act.orderIndex = idx);

    this.isOrderDirty = true;
  }

  getConnectedDropLists(): string[] {
    return this.groupedActivities.map(g => `cdk-drop-list-${g.day}`);
  }

  saveOrder() {
    if (!this.selectedTrip) return;

    this.groupedActivities.forEach(group => {
      const request: ReorderRequest = {
        dayNumber: group.day,
        activityIds: group.activities.map(a => a.id)
      };

      this.tripService
        .reorderActivities(request, this.selectedTrip!.id)
        .subscribe();
    });

    this.selectedTrip.activities = this.groupedActivities.flatMap(g => g.activities);

    this.isOrderDirty = false;
  }

  private buildGroupedActivities(trip: Trip): void {
    const map = new Map<number, Activity[]>();

    for (let day = 1; day <= trip.numberOfDays; day++) {
      map.set(day, []);
    }

    trip.activities.forEach(act => {
      if (!map.has(act.dayNumber)) {
        map.set(act.dayNumber, []);
      }
      map.get(act.dayNumber)!.push(act);
    });

    this.groupedActivities = Array.from(map.entries()).map(
      ([day, activities]) => ({
        day,
        activities: activities.sort((a, b) => a.orderIndex - b.orderIndex)
      })
    );
  }

  openAddActivity(dayNumber: number): void {
    this.addActivityDay = dayNumber;

    this.newActivity = {
      dayNumber,
      duration: '',
      description: ''
    };

    this.isAddActivityOpen = true;
  }

  sortActivities(): void {
    if (!this.selectedTrip) return;

    this.selectedTrip.activities = [...this.selectedTrip.activities].sort(
      (a, b) =>
        a.dayNumber - b.dayNumber ||
        a.orderIndex - b.orderIndex
    );
  }

  groupActivities(): void {
    if (!this.selectedTrip) return;

    const map = new Map<number, Activity[]>();

    for (let day = 1; day <= this.selectedTrip.numberOfDays; day++) {
      map.set(day, []);
    }

    for (const act of this.selectedTrip.activities) {
      map.get(act.dayNumber)!.push(act);
    }

    this.groupedActivities = Array.from(map.entries()).map(
      ([day, activities]) => ({
        day,
        activities
      })
    );
  }

  saveAddActivity(): void {
    if (!this.selectedTrip) return;

    this.tripService
      .createActivity(this.selectedTrip.id, this.newActivity)
      .subscribe(created => {
        this.selectedTrip = {
          ...this.selectedTrip!,
          activities: [...this.selectedTrip!.activities, created]
        };

        this.sortActivities();
        this.groupActivities();

        this.isAddActivityOpen = false;
        this.refresh.next();
      });
  }

  closeModals(): void {
    this.isCreateOpen = false;
    this.isDetailOpen = false;
    this.selectedTrip = null;
    this.isEditTripOpen = false;
    this.isEditActivityOpen = false;
  }
}