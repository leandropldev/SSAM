import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateTokenComponent } from './create-token/create-token.componet';
import { ShareTokenComponent } from './share-token/share-token.componet';
import { TokenService } from '../../services/token.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, CreateTokenComponent, ShareTokenComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  readonly isCreateTokenModalOpen = signal(false);
  readonly isShareTokenModalOpen = signal(false);
  readonly errorMessage = signal<string | null>(null);

  constructor(readonly tokenService: TokenService) {}

  ngOnInit(): void {
    this.tokenService.loadTokens();
  }

  toggleTokenModal(): void {
    this.isCreateTokenModalOpen.update(value => !value);
  }

  toggleShareTokenModal(): void {
    this.isShareTokenModalOpen.update(value => !value);
  }

  onTokenCreateError(error: string): void {
    this.errorMessage.set(error);
  }

  onTokenCreated(): void {
    this.toggleTokenModal();
    this.tokenService.loadTokens();
  }

  onShareTokenSuccess(): void {
    this.toggleShareTokenModal();
    this.tokenService.loadTokens();
  }

  onShareTokenError(error: string): void {
    this.errorMessage.set(error);
  }

  suspendToken(id: string): void {
    this.tokenService.suspendToken(id).subscribe({
      next: () => {
        this.tokenService.loadTokens();
      },
      error: (err) => {
        const errorMessage = err?.error?.message || 'Failed to suspend token';
        this.errorMessage.set(errorMessage);
      }
    });
  }

  terminateToken(id: string): void {
    this.tokenService.terminateToken(id).subscribe({
      next: () => {
        this.tokenService.loadTokens();
      },
      error: (err) => {
        const errorMessage = err?.error?.message || 'Failed to terminate token';
        this.errorMessage.set(errorMessage);
      }
    });
  }

  isTokenTerminated(status: string): boolean {
    return status === 'TERMINATED';
  }

  closeErrorPopup(): void {
    this.errorMessage.set(null);
  }
}
