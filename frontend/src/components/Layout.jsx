import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const navItems = [
  { to: "/jobs", label: "Jobs" },
  { to: "/resume", label: "Resume & ATS Match" },
  { to: "/applications", label: "Applications" },
  { to: "/cover-letter", label: "Cover Letter" },
];

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-surface">
      <header className="bg-navy text-white">
        <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
          <div>
            <h1 className="text-lg font-bold">Career Portal</h1>
            {user && <p className="text-xs text-blue-200">{user.fullName}</p>}
          </div>
          <nav className="flex gap-1">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `px-3 py-2 rounded text-sm font-medium transition ${
                    isActive ? "bg-accent text-white" : "text-blue-100 hover:bg-white/10"
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <button
            onClick={handleLogout}
            className="text-sm text-blue-100 hover:text-white border border-blue-300/40 rounded px-3 py-1.5"
          >
            Log out
          </button>
        </div>
      </header>
      <main className="max-w-6xl mx-auto px-6 py-8">{children}</main>
    </div>
  );
}
