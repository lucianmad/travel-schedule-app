import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "./auth.service";
import { Observable } from "rxjs";
import { Activity } from "../models/activity";
import { ActivityUpdateRequest } from "../models/activity-update-request";

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
    
    constructor(
    private http: HttpClient,
    private router: Router,
    ) {}

    private authService = inject(AuthService);
    private apiUrl = 'http://localhost:8080/api/activities';
    private currentUser = this.authService.getCurrentUser();

    get(id: number): void{
        this.http.get(`${this.apiUrl}/${id}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    update(request: ActivityUpdateRequest, id: number): Observable<Activity>{
        return this.http.put<Activity>(`${this.apiUrl}/${id}`, request, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

    delete(id: number): Observable<any>{
        return this.http.delete(`${this.apiUrl}/${id}`, {headers: {'X-User-Id': `${this.currentUser?.id}`}});
    }

}