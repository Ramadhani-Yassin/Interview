import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService, Upload } from '../../services/api.service';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent {
  file?: File;
  charset = 'UTF-8';
  uploads: Upload[] = [];
  uploading = false;

  constructor(private api: ApiService) { this.refresh(); }

  onFileChange(e: Event) {
    const input = e.target as HTMLInputElement;
    if (input.files && input.files.length) this.file = input.files[0];
  }

  onSubmit(e: Event) {
    e.preventDefault();
    if (!this.file) return;
    this.uploading = true;
    this.api.uploadCsv(this.file, this.charset).subscribe({
      next: _ => { this.uploading = false; this.refresh(); },
      error: _ => { this.uploading = false; }
    });
  }

  refresh() {
    this.api.listUploads().subscribe(u => this.uploads = u);
  }
}
