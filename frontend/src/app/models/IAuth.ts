// Interface for the data sent during login/registration
export interface AuthRequest {
  username: string;
  password: string;
}

// Simple interface for a server response (since it only send success/error messages)
export interface AuthResponse {
  message: string;
}
