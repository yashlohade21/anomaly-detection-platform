"use client";

import SeverityBadge from "./SeverityBadge";
import { Anomaly } from "@/lib/api";

interface AnomalyTableProps {
  anomalies: Anomaly[];
  page?: number;
  totalPages?: number;
  onPageChange?: (page: number) => void;
  showPagination?: boolean;
}

export default function AnomalyTable({
  anomalies,
  page = 1,
  totalPages = 1,
  onPageChange,
  showPagination = false,
}: AnomalyTableProps) {
  const formatTime = (timestamp: string) => {
    try {
      return new Date(timestamp).toLocaleString();
    } catch {
      return timestamp;
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Time
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Source
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Metric
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Value
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Severity
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Score
              </th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Confidence
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {anomalies.length === 0 ? (
              <tr>
                <td
                  colSpan={7}
                  className="px-6 py-12 text-center text-gray-400"
                >
                  No anomalies found
                </td>
              </tr>
            ) : (
              anomalies.map((anomaly, idx) => (
                <tr
                  key={anomaly.id || idx}
                  className="hover:bg-gray-50 transition-colors"
                >
                  <td className="px-6 py-4 text-sm text-gray-600 whitespace-nowrap">
                    {formatTime(anomaly.timestamp)}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900 font-medium whitespace-nowrap">
                    {anomaly.source_id}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600 whitespace-nowrap">
                    {anomaly.metric_type}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900 font-mono whitespace-nowrap">
                    {typeof anomaly.value === "number"
                      ? anomaly.value.toFixed(2)
                      : anomaly.value}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <SeverityBadge severity={anomaly.severity} />
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900 font-mono whitespace-nowrap">
                    {typeof anomaly.anomaly_score === "number"
                      ? anomaly.anomaly_score.toFixed(3)
                      : anomaly.anomaly_score}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900 font-mono whitespace-nowrap">
                    {typeof anomaly.confidence === "number"
                      ? `${(anomaly.confidence * 100).toFixed(1)}%`
                      : anomaly.confidence}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showPagination && totalPages > 1 && (
        <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex items-center justify-between">
          <p className="text-sm text-gray-500">
            Page {page} of {totalPages}
          </p>
          <div className="flex gap-2">
            <button
              onClick={() => onPageChange?.(page - 1)}
              disabled={page <= 1}
              className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Previous
            </button>
            <button
              onClick={() => onPageChange?.(page + 1)}
              disabled={page >= totalPages}
              className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
