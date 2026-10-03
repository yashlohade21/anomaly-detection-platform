import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || "/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor: attach JWT from localStorage
api.interceptors.request.use(
  (config) => {
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("token");
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: on 401 clear token and redirect
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      if (typeof window !== "undefined") {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

// ---------- Auth ----------

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  full_name: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  full_name: string;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const res = await api.post<AuthResponse>("/auth/login", data);
  return res.data;
}

export async function register(data: RegisterRequest): Promise<void> {
  await api.post("/auth/register", data);
}

// ---------- Anomalies ----------

export interface Anomaly {
  id: string;
  timestamp: string;
  source_id: string;
  metric_type: string;
  value: number;
  severity: string;
  anomaly_score: number;
  confidence: number;
  description?: string;
}

export interface AnomalyListResponse {
  anomalies: Anomaly[];
  total: number;
  page: number;
  size: number;
}

export interface AnomalyFilters {
  page?: number;
  size?: number;
  severity?: string;
  source_id?: string;
}

export async function getAnomalies(
  filters: AnomalyFilters = {}
): Promise<AnomalyListResponse> {
  const params = new URLSearchParams();
  if (filters.page) params.set("page", String(filters.page));
  if (filters.size) params.set("size", String(filters.size));
  if (filters.severity) params.set("severity", filters.severity);
  if (filters.source_id) params.set("source_id", filters.source_id);
  const res = await api.get<AnomalyListResponse>(`/anomalies?${params.toString()}`);
  return res.data;
}

export async function getAnomaly(id: string): Promise<Anomaly> {
  const res = await api.get<Anomaly>(`/anomalies/${id}`);
  return res.data;
}

// ---------- Dashboard ----------

export interface DashboardStats {
  total_anomalies: number;
  critical_count: number;
  high_count: number;
  medium_count: number;
  low_count: number;
  recent_anomalies: Anomaly[];
  anomaly_timeline: TimelinePoint[];
}

export interface TimelinePoint {
  timestamp: string;
  count: number;
}

export async function getDashboardStats(): Promise<DashboardStats> {
  const res = await api.get<DashboardStats>("/dashboard/stats");
  return res.data;
}

// ---------- Sources ----------

export interface Source {
  id: string;
  name: string;
  source_id: string;
  type: string;
  hostname: string;
  region: string;
  created_at?: string;
}

export interface CreateSourceRequest {
  name: string;
  source_id: string;
  type: string;
  hostname: string;
  region: string;
}

export async function getSources(): Promise<Source[]> {
  const res = await api.get<Source[]>("/sources");
  return res.data;
}

export async function createSource(data: CreateSourceRequest): Promise<Source> {
  const res = await api.post<Source>("/sources", data);
  return res.data;
}

export async function deleteSource(id: string): Promise<void> {
  await api.delete(`/sources/${id}`);
}

export default api;
