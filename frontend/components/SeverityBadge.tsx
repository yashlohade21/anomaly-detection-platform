"use client";

interface SeverityBadgeProps {
  severity: string;
}

const severityStyles: Record<string, string> = {
  CRITICAL: "bg-red-100 text-red-800 border-red-200",
  HIGH: "bg-orange-100 text-orange-800 border-orange-200",
  MEDIUM: "bg-yellow-100 text-yellow-800 border-yellow-200",
  LOW: "bg-green-100 text-green-800 border-green-200",
};

export default function SeverityBadge({ severity }: SeverityBadgeProps) {
  const normalized = severity.toUpperCase();
  const style = severityStyles[normalized] || "bg-gray-100 text-gray-800 border-gray-200";

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${style}`}
    >
      {normalized}
    </span>
  );
}
