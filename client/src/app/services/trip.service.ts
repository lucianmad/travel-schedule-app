import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { TripRequest } from "../models/trip-request";
import { Trip } from "../models/trip";
import { ReorderRequest } from "../models/reorder-request";
import { Activity } from "../models/activity";
import { ActivityCreateRequest } from "../models/activity-create-request";

@Injectable({
  providedIn: 'root'
})
export class TripService {

    constructor(
    private http: HttpClient,
    private router: Router,
    ) {}
    
    private authService = inject(AuthService);
    private apiUrl = 'http://localhost:8080/api/trips';
    private currentUser = this.authService.getCurrentUser();

    get(): Observable<Trip[]>{
        return this.http.get<Trip[]>(`${this.apiUrl}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    getById(tripId: number): Observable<Trip>{
        return this.http.get<Trip>(`${this.apiUrl}/${tripId}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    create(request: TripRequest): Observable<Trip>{
        return this.http.post<Trip>(`${this.apiUrl}`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    update(request: TripRequest, tripId: number): Observable<Trip>{
        return this.http.put<Trip>(`${this.apiUrl}/${tripId}`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    delete(tripId: number): Observable<any>{
        return this.http.delete(`${this.apiUrl}/${tripId}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    reorderActivities(request: ReorderRequest, tripId: number): Observable<any>{
        return this.http.put(`${this.apiUrl}/${tripId}/activities/reorder`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    createActivity(tripId: number, request: ActivityCreateRequest): Observable<Activity>{
        return this.http.post<Activity>(`${this.apiUrl}/${tripId}/activities`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }
}