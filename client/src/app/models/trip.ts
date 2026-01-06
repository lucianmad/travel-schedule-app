import { Activity } from "./activity";

export interface Trip {
    id: number;
    destination: string;
    numberOfDays: number;
    status: string;
    activities: Activity[];
}