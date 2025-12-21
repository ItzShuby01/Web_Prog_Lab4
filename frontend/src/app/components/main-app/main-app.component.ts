import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  AfterViewInit,
  HostListener,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AreaService } from '../../services/area.service';
import { IPoint, PointRequest } from '../../models/IPoint';

@Component({
  selector: 'app-main-app',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './main-app.component.html',
  styleUrls: ['./main-app.component.css']
})
export class MainAppComponent implements OnInit, AfterViewInit {
  @ViewChild('canvas', { static: false }) canvas!: ElementRef<HTMLCanvasElement>;

  // Allowed R Options and X options
  xOptions: number[] = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
  rOptions: number[] = [-3, -2, -1, 0, 1, 2, 3, 4, 5];

  point: PointRequest = { x: 0, y: 0, r: 3, source : 'form' };
  results: IPoint[] = [];

  constructor(
    private authService: AuthService,
    private areaService: AreaService,
    private router: Router,
    private cdr: ChangeDetectorRef // Injected ChangeDetector
  ) {}

  ngOnInit(): void {
    // Initial load
    this.areaService.loadHistory().subscribe();

    // Listen for any changes (Add or Clear)
    this.areaService.results$.subscribe(data => {
      this.results = data;

      // Force Angular to update the HTML table immediately
      this.cdr.detectChanges();

      // Redraw the canvas with the new (or empty) data
      if (this.canvas) {
        this.drawCanvas();
      }
    });
  }

  ngAfterViewInit(): void {
    this.drawCanvas();
  }

  @HostListener('window:resize')
  onResize() {
    this.drawCanvas();
  }

  submitForm(): void {
    if (!this.areaService.validateR(this.point.r)) {
      alert("Invalid R value. Please select a positive radius.");
      return;
    }
    if (!this.areaService.validateY(this.point.y)) {
      alert("Y must be a number between -3 and 3.");
      return;
    }

     // Ensure source is 'form' for button clicks
    const request: PointRequest = { ...this.point, source: 'form' };
    this.areaService.checkPoint(this.point).subscribe();
  }

  clearHistory(): void {
    this.areaService.clearHistory().subscribe({
      next: () => {
        console.log('History cleared successfully');
      },
      error: (err) => console.error('Failed to clear history', err)
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  drawCanvas(): void {
    const ctx = this.canvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const width = this.canvas.nativeElement.width;
    const height = this.canvas.nativeElement.height;
    const centerX = width / 2;
    const centerY = height / 2;
    const step = width / 10;

    ctx.clearRect(0, 0, width, height);

    const rVal = this.point.r;
    if (rVal > 0) {
      ctx.fillStyle = "rgba(0, 123, 255, 0.5)";

      // Q1: Rectangle [0, R/2] x [0, R]
      ctx.fillRect(centerX, centerY - (rVal * step), (rVal / 2) * step, rVal * step);

      // Q2: Triangle connecting (0,0), (-R/2, 0), (0, R/2)
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.lineTo(centerX - (rVal / 2) * step, centerY);
      ctx.lineTo(centerX, centerY - (rVal / 2) * step);
      ctx.closePath();
      ctx.fill();

      // Q4: Quarter Circle - R/2 radius
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.arc(centerX, centerY, (rVal / 2) * step, 0, Math.PI / 2);
      ctx.fill();
    }

    // Axes
    ctx.strokeStyle = "black";
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(0, centerY); ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0); ctx.lineTo(centerX, height);
    ctx.stroke();

    // Labels
    ctx.fillStyle = "black";
    ctx.font = "10px Arial";
    if (rVal > 0) {
      ctx.fillText("R/2", centerX + (rVal / 2) * step - 5, centerY + 15);
      ctx.fillText("-R/2", centerX - (rVal / 2) * step - 10, centerY + 15);
      ctx.fillText("R", centerX + 5, centerY - (rVal * step) + 5);
      ctx.fillText("R/2", centerX + 5, centerY - (rVal / 2) * step + 5);
    }

    // Points - Drawing points from the this.results array
    this.results.forEach(p => {
      ctx.fillStyle = p.hit ? "green" : "red";
      ctx.beginPath();
      ctx.arc(centerX + p.x * step, centerY - p.y * step, 3, 0, Math.PI * 2);
      ctx.fill();
    });
  }

  handleCanvasClick(event: MouseEvent): void {
    const rect = this.canvas.nativeElement.getBoundingClientRect();
    const step = this.canvas.nativeElement.width / 10;
    const x = (event.clientX - rect.left - this.canvas.nativeElement.width / 2) / step;
    const y = (this.canvas.nativeElement.height / 2 - (event.clientY - rect.top)) / step;

    if (this.point.r <= 0) {
      alert("Please select a positive R before clicking the canvas.");
      return;
    }
    this.areaService.checkPoint({ x, y, r: this.point.r, source: 'canvas'}).subscribe();
  }
}
