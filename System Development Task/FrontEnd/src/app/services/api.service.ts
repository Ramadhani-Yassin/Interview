import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Upload {
  id: number;
  fileName: string;
  uploadedAt: string;
  numberOfRows: number;
  numberOfColumns: number;
  headerLine: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface DataRecord {
  id: number;
  [key: string]: any;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  listUploads(): Observable<Upload[]> {
    return this.http.get<Upload[]>(`${this.baseUrl}/uploads`);
    }

  uploadCsv(file: File, charset?: string): Observable<Upload> {
    const formData = new FormData();
    formData.append('file', file);
    const params = charset ? new HttpParams().set('charset', charset) : undefined;
    return this.http.post<Upload>(`${this.baseUrl}/uploads`, formData, { params });
  }

  getRecords(uploadId: number, page = 0, size = 50, search?: string, sortBy = 'id', direction: 'ASC' | 'DESC' = 'ASC') {
    let params = new HttpParams().set('page', page).set('size', size).set('sortBy', sortBy).set('direction', direction);
    if (search) params = params.set('search', search);
    return this.http.get<Page<DataRecord>>(`${this.baseUrl}/uploads/${uploadId}/records`, { params });
  }

  exportExcel(uploadId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/uploads/${uploadId}/export`, { responseType: 'blob' });
  }
} 