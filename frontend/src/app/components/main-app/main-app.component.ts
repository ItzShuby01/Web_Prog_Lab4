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

  xOptions: number[] = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
  rOptions: number[] = [-3, -2, -1, 0, 1, 2, 3, 4, 5];

  point: PointRequest = { x: 0, y: 0, r: 3, source: 'form' };
  results: IPoint[] = [];
  currentPage = 0;
  pageSize = 5;
  totalElements = 0;

  constructor(
    private authService: AuthService,
    private areaService: AreaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Subscribe to results and trigger redraw
    this.areaService.results$.subscribe(data => {
      this.results = data;
      // check if canvas exists because ngOnInit runs before ViewChild is ready
      if (this.canvas) {
        this.drawCanvas();
      }
      this.cdr.detectChanges();
    });

    // Subscribe to pagination total
    this.areaService.totalElements.subscribe(total => {
      this.totalElements = total;
      this.cdr.detectChanges();
    });

    // Initial Load
    this.areaService.loadHistory(this.currentPage, this.pageSize).subscribe();
  }

  ngAfterViewInit(): void {
    this.drawCanvas();
  }

  @HostListener('window:resize')
  onResize() {
    this.drawCanvas();
  }

  changePage(delta: number) {
    this.currentPage += delta;
    this.areaService.loadHistory(this.currentPage, this.pageSize).subscribe();
  }

  submitForm(): void {
    if (!this.areaService.validateR(this.point.r)) {
      alert("Please select a positive radius.");
      return;
    }
    if (!this.areaService.validateY(this.point.y)) {
      alert("Y must be between -3 and 3.");
      return;
    }

    const request: PointRequest = { ...this.point, source: 'form' };
    this.areaService.checkPoint(request).subscribe();
  }

  handleCanvasClick(event: MouseEvent): void {
    if (this.point.r <= 0) {
      alert("Please select a positive R first.");
      return;
    }

    const rect = this.canvas.nativeElement.getBoundingClientRect();
    const step = this.canvas.nativeElement.width / 10;
    const x = (event.clientX - rect.left - this.canvas.nativeElement.width / 2) / step;
    const y = (this.canvas.nativeElement.height / 2 - (event.clientY - rect.top)) / step;

    this.areaService.checkPoint({ x, y, r: this.point.r, source: 'canvas' }).subscribe();
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
      // Q1 Rectangle
      ctx.fillRect(centerX, centerY - (rVal * step), (rVal / 2) * step, rVal * step);
      // Q2 Triangle
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.lineTo(centerX - (rVal / 2) * step, centerY);
      ctx.lineTo(centerX, centerY - (rVal / 2) * step);
      ctx.fill();
      // Q4 Quarter Circle
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.arc(centerX, centerY, (rVal / 2) * step, 0, Math.PI / 2);
      ctx.fill();
    }

    // Axes
    ctx.strokeStyle = "black";
    ctx.beginPath();
    ctx.moveTo(0, centerY); ctx.lineTo(width, centerY);
    ctx.moveTo(centerX, 0); ctx.lineTo(centerX, height);
    ctx.stroke();

    // Draw saved points
    this.results.forEach(p => {
      ctx.fillStyle = p.hit ? "green" : "red";
      ctx.beginPath();
      ctx.arc(centerX + p.x * step, centerY - p.y * step, 3, 0, Math.PI * 2);
      ctx.fill();
    });
  }

clearHistory(): void {
  // Get current username
  const currentUsername = this.authService.getUsername();

  this.areaService.clearHistory(currentUsername).subscribe({
    next: () => {
      console.log('User history cleared locally and on server');
    },
    error: (err) => console.error('Failed to clear history', err)
  });
}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
