import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TokenService } from '../../../services/token.service';

@Component({
  selector: 'app-create-token',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-token.component.html'
})
export class CreateTokenComponent {
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  @Output() error = new EventEmitter<string>();

  readonly ownerId = signal('');
  readonly confidentialData = signal('');
  readonly isLoading = signal(false);

  constructor(private tokenService: TokenService) {}

  onConfirm(): void {
    this.isLoading.set(true);

    const request = {
      ownerId: this.ownerId(),
      confidentialData: this.confidentialData()
    };

    this.tokenService.createToken(request).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.resetForm();
        this.confirm.emit();
      },
      error: (err) => {
        this.isLoading.set(false);
        const errorMessage = err?.error?.message || 'Failed to create token';
        this.error.emit(errorMessage);
      }
    });
  }

  onCancel(): void {
    this.cancel.emit();
    this.resetForm();
  }

  private resetForm(): void {
    this.ownerId.set('');
    this.confidentialData.set('');
  }
}
