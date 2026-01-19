import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TokenService } from '../../../services/token.service';

@Component({
  selector: 'app-share-token',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './share-token.component.html'
})
export class ShareTokenComponent {
  @Output() cancel = new EventEmitter<void>();
  @Output() success = new EventEmitter<void>();
  @Output() error = new EventEmitter<string>();

  readonly parentId = signal('');
  readonly friendEmail = signal('');
  readonly isLoading = signal(false);

  constructor(private tokenService: TokenService) {}

  onCancel(): void {
    this.cancel.emit();
  }

  onConfirm(): void {
    if (!this.parentId().trim() || !this.friendEmail().trim()) {
      this.error.emit('Please fill in all fields');
      return;
    }

    this.isLoading.set(true);
    this.tokenService.shareToken(this.parentId(), this.friendEmail()).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.success.emit();
      },
      error: (err) => {
        this.isLoading.set(false);
        const errorMessage = err?.error?.message || 'Failed to share token';
        this.error.emit(errorMessage);
      }
    });
  }
}
