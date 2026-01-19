export interface AssetTokenResponse {
  id: string;
  ownerId: string;
  status: 'ACTIVE' | 'SUSPENDED' | 'IN_TERMINATION' | 'TERMINATED';
  updateCounter: number | null;
}

export interface AssetToken {
  id: string;
  ownerId: string;
  status: string;
  updateCounter: number | null;
  lastStatusChange: string;
}

export interface CreateAssetToken {
  ownerId: string;
  confidentialData: string;
}