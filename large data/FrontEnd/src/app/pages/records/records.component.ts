import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService, DataRecord, Upload } from '../../services/api.service';

@Component({
  selector: 'app-records',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './records.component.html',
  styleUrl: './records.component.css'
})
export class RecordsComponent implements OnInit {
  upload?: Upload;
  uploadId!: number;
  headers: string[] = [];
  rows: DataRecord[] = [];
  page = 0;
  size = 25;
  totalPages = 0;
  search = '';
  sortBy = 'id';
  direction: 'ASC' | 'DESC' = 'ASC';

  constructor(private route: ActivatedRoute, private api: ApiService) {}

  ngOnInit() {
    this.uploadId = Number(this.route.snapshot.paramMap.get('uploadId'));
    this.api.listUploads().subscribe(list => {
      this.upload = list.find(u => u.id === this.uploadId);
      if (this.upload) this.headers = this.upload.headerLine.split(',');
      this.load();
    });
  }

  load() {
    this.api.getRecords(this.uploadId, this.page, this.size, this.search || undefined, this.sortBy, this.direction).subscribe(p => {
      this.rows = p.content;
      this.totalPages = p.totalPages;
    });
  }

  prev() { if (this.page>0) { this.page--; this.load(); } }
  next() { if (this.page+1 < this.totalPages) { this.page++; this.load(); } }

  export() {
    this.api.exportExcel(this.uploadId).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export-${this.uploadId}.xlsx`;
      a.click();
      URL.revokeObjectURL(url);
    });
  }
}
