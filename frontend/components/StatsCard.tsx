"use client";

interface StatsCardProps {
  title: string;
  value: number | string;
  color: "red" | "orange" | "yellow" | "blue";
}

const colorMap: Record<string, string> = {
  red: "border-l-red-500",
  orange: "border-l-orange-500",
  yellow: "border-l-yellow-500",
  blue: "border-l-blue-500",
};

const textColorMap: Record<string, string> = {
  red: "text-red-600",
  orange: "text-orange-600",
  yellow: "text-yellow-600",
  blue: "text-blue-600",
};

export default function StatsCard({ title, value, color }: StatsCardProps) {
  return (
    <div
      className={`bg-white rounded-lg shadow-sm border border-gray-200 border-l-4 ${colorMap[color]} p-6`}
    >
      <p className="text-sm font-medium text-gray-500 uppercase tracking-wide">
        {title}
      </p>
      <p className={`mt-2 text-3xl font-bold ${textColorMap[color]}`}>
        {typeof value === "number" ? value.toLocaleString() : value}
      </p>
    </div>
  );
}
