import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ResumeService } from '../resume/resume.service';
import { AuthService } from '../auth/auth.service';
import { FileUploadModule } from 'primeng/fileupload';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog'; 
import { TooltipModule } from 'primeng/tooltip';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FileUploadModule, TableModule, ButtonModule, ToastModule, CardModule, DialogModule, TooltipModule, ProgressSpinnerModule],
  providers: [MessageService],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  resumes: any[] = [];
  isLoading = false;
  
  // New States
  isDarkMode = false;
  isAnalyzing = false;
  stagedFile: File | null = null;
  
  displayAnalysis: boolean = false;
  selectedResume: any = null;

  constructor(
    private resumeService: ResumeService, 
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // Check system preference or saved preference for dark mode
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      this.toggleTheme();
    }
    this.fetchResumes();
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    if (this.isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  onFileSelect(event: any) {
    if (event.files && event.files.length > 0) {
      this.stagedFile = event.files[0];
      this.cdr.detectChanges();
    }
  }

  onFileRemove() {
    this.stagedFile = null;
    this.cdr.detectChanges();
  }

  startAnalysis(fileUploader: any) {
    if (!this.stagedFile) return;

    this.isAnalyzing = true; 
    this.cdr.detectChanges();

    this.resumeService.uploadResume(this.stagedFile).subscribe({
      next: (httpResponse: any) => {
        const response = httpResponse.body || httpResponse;

        if (response && (response.success === true || response.resumeId)) {
          this.messageService.add({ severity: 'success', summary: 'Analysis Complete', detail: 'Resume processed successfully!' });
          this.stagedFile = null;
          fileUploader.clear(); 
          this.fetchResumes(); 
        } else {
          this.messageService.add({ severity: 'error', summary: 'Processing Failed', detail: response.message || 'Unknown error' });
        }
        this.isAnalyzing = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('HTTP Error', err);
        this.messageService.add({ severity: 'error', summary: 'Network Error', detail: 'Analysis failed. Please try again.' });
        this.isAnalyzing = false;
        this.cdr.detectChanges();
      }
    });
  }

  showAnalysis(resume: any) {
    this.selectedResume = resume;
    this.displayAnalysis = true;
  }

  fetchResumes() {
    this.isLoading = true;
    this.cdr.detectChanges();
    
    this.resumeService.getMyResumes().subscribe({
      next: (response: any) => {
        if (response && response.success === true) {
            this.resumes = [...response.data]; 
        }
        this.isLoading = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Failed to fetch resumes', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}