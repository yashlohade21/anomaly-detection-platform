"use client";

import { useAuth } from "@/lib/auth";

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <div>
        <h2 className="text-lg font-semibold text-gray-800">
          Welcome back{user?.fullName ? `, ${user.fullName}` : ""}
        </h2>
      </div>
      <div className="flex items-center gap-4">
        {user && (
          <span className="text-sm text-gray-500">{user.email}</span>
        )}
        <button
          onClick={logout}
          className="text-sm text-gray-600 hover:text-red-600 font-medium transition-colors px-3 py-1.5 rounded-lg hover:bg-red-50"
        >
          Logout
        </button>
      </div>
    </header>
  );
}
