"use client";

import { useState, useEffect, useCallback } from "react";
import Layout from "@/components/Layout";
import AnomalyTable from "@/components/AnomalyTable";
import { getAnomalies, Anomaly } from "@/lib/api";

export default function AnomaliesPage() {
  const [anomalies, setAnomalies] = useState<Anomaly[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Filters
  const [severity, setSeverity] = useState("");
  const [sourceId, setSourceId] = useState("");

  // Pagination
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 20;

  const fetchAnomalies = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getAnomalies({
        page,
        size: pageSize,
        severity: severity || undefined,
        source_id: sourceId || undefined,
      });
      setAnomalies(data.anomalies || []);
      const total = data.total || 0;
      setTotalPages(Math.max(1, Math.ceil(total / pageSize)));
      setError("");
    } catch {
      setError("Failed to load anomalies");
    } finally {
      setLoading(false);
    }
  }, [page, severity, sourceId]);

  useEffect(() => {
    fetchAnomalies();
  }, [fetchAnomalies]);

  const handleFilterApply = () => {
    setPage(1);
    fetchAnomalies();
  };

  const handleClearFilters = () => {
    setSeverity("");
    setSourceId("");
    setPage(1);
  };

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Anomalies</h1>
          <p className="text-sm text-gray-500 mt-1">
            Browse and filter detected anomalies
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex flex-wrap items-end gap-4">
            <div className="flex-1 min-w-[180px]">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Severity
              </label>
              <select
                value={severity}
                onChange={(e) => setSeverity(e.target.value)}
                className="select"
              >
                <option value="">All Severities</option>
                <option value="CRITICAL">Critical</option>
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
            </div>

            <div className="flex-1 min-w-[180px]">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Source ID
              </label>
              <input
                type="text"
                value={sourceId}
                onChange={(e) => setSourceId(e.target.value)}
                placeholder="e.g., server-01"
                className="input"
              />
            </div>

            <div className="flex gap-2">
              <button onClick={handleFilterApply} className="btn-primary">
                Apply
              </button>
              <button
                onClick={handleClearFilters}
                className="px-4 py-2 text-sm font-medium text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Clear
              </button>
            </div>
          </div>
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
          <AnomalyTable
            anomalies={anomalies}
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
            showPagination
          />
        )}
      </div>
    </Layout>
  );
}
