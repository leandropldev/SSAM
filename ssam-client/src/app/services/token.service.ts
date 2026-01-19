import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {
  AssetToken,
  AssetTokenResponse,
  CreateAssetToken
} from '../models/token.model';

@Injectable({
  providedIn: 'root',
})
export class TokenService {

  private readonly api = `${environment.apiUrl}/tokens`;

  // Reactive state signals
  readonly tokens = signal<AssetTokenResponse[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  // Computed signals
  readonly hasTokens = computed(() => this.tokens().length > 0);
  readonly isLoadingOrError = computed(() => this.loading() || this.error() !== null);

  constructor(private http: HttpClient) {}

  // GET TOKENS
  loadTokens(): void {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<AssetTokenResponse[]>(this.api).subscribe({
      next: (data) => {
        this.tokens.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load tokens');
        this.loading.set(false);
      }
    });
  }

  // CREATE TOKEN
  createToken(request: CreateAssetToken) {
    return this.http.post<string>(this.api, request);
  }

  // GET TOKEN BY ID
  getToken(id: string) {
    return this.http.get<AssetToken>(`${this.api}/${id}`);
  }

  // SHARE TOKEN
  shareToken(parentId: string, friendEmail: string) {
    return this.http.post<string>(
      `${this.api}/${parentId}/share`,
      null,
      { params: { friendEmail } }
    );
  }

  // SUSPEND TOKEN
  suspendToken(id: string) {
    return this.http.patch<void>(`${this.api}/${id}/suspend`, {});
  }

  // TERMINATE TOKEN
  terminateToken(id: string) {
    return this.http.post<void>(`${this.api}/${id}/terminate`, {});
  }
}
