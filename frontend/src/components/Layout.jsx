import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const navItems = [
  { to: "/jobs", label: "🔍 Jobs", icon: "🔍" },
  { to: "/resume", label: "📄 Resume & ATS", icon: "📄" },
  { to: "/applications", label: "📋 Applications", icon: "📋" },
  { to: "/cover-letter", label: "✍️ Cover Letter", icon: "✍️" },
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
      <header className="bg-gradient-to-r from-navy via-primary to-accent text-white shadow-lg sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-6 py-5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center text-xl font-bold">
              💼
            </div>
            <div>
              <h1 className="text-2xl font-bold tracking-tight">Career Portal</h1>
              {user && <p className="text-xs text-blue-100 font-medium">{user.fullName}</p>}
            </div>
          </div>
          
          <nav className="hidden md:flex gap-2">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `px-4 py-2.5 rounded-lg text-sm font-semibold transition-all duration-200 ${
                    isActive 
                      ? "bg-white text-primary shadow-md" 
                      : "text-white hover:bg-white/20"
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <button
            onClick={handleLogout}
            className="text-sm font-semibold px-4 py-2 rounded-lg bg-white/20 hover:bg-white/30 text-white transition-all duration-200 border border-white/30"
          >
            Log out
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-10">{children}</main>
    </div>
  );
}
