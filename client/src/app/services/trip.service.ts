import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { User } from "../models/user";
import { AuthService } from "./auth.service";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { TripRequest } from "../models/trip-request";
import { Trip } from "../models/trip";

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
        return this.http.get<Trip[]>(`${this.apiUrl}`);
    }

    getById(id: number): Observable<Trip>{
        return this.http.get<Trip>(`${this.apiUrl}/${id}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    create(request: TripRequest): Observable<Trip>{
        return this.http.post<Trip>(`${this.apiUrl}`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    update(request: TripRequest, id: number): Observable<Trip>{
        return this.http.put<Trip>(`${this.apiUrl}/${id}`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    delete(id: number): Observable<any>{
        return this.http.delete(`${this.apiUrl}/${id}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

}