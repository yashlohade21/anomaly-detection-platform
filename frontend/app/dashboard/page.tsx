"use client";

import { useState, useEffect, useCallback } from "react";
import Layout from "@/components/Layout";
import StatsCard from "@/components/StatsCard";
import AnomalyChart from "@/components/AnomalyChart";
import AnomalyTable from "@/components/AnomalyTable";
import { getDashboardStats, DashboardStats } from "@/lib/api";

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const fetchStats = useCallback(async () => {
    try {
      const data = await getDashboardStats();
      setStats(data);
      setError("");
    } catch {
      setError("Failed to load dashboard data");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchStats();
    const interval = setInterval(fetchStats, 10000);
    return () => clearInterval(interval);
  }, [fetchStats]);

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-1">
            Real-time anomaly monitoring overview
          </p>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
            {error}
          </div>
        )}

        {loading ? (
          <div className="flex items-center justify-center py-20">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <>
            {/* Stats Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <StatsCard
                title="Total Anomalies"
                value={stats?.total_anomalies ?? 0}
                color="blue"
              />
              <StatsCard
                title="Critical"
                value={stats?.critical_count ?? 0}
                color="red"
              />
              <StatsCard
                title="High"
                value={stats?.high_count ?? 0}
                color="orange"
              />
              <StatsCard
                title="Medium"
                value={stats?.medium_count ?? 0}
                color="yellow"
              />
            </div>

            {/* Timeline Chart */}
            <AnomalyChart data={stats?.anomaly_timeline ?? []} />

            {/* Recent Anomalies */}
            <div>
              <h2 className="text-lg font-semibold text-gray-900 mb-3">
                Recent Anomalies
              </h2>
              <AnomalyTable
                anomalies={(stats?.recent_anomalies ?? []).slice(0, 10)}
              />
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}
