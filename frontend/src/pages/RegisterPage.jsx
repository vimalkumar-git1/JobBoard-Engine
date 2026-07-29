import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function RegisterPage() {
  const [form, setForm] = useState({ fullName: "", email: "", password: "", phone: "", location: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await register(form);
      navigate("/jobs");
    } catch (err) {
      const fieldErrors = err.response?.data?.fieldErrors;
      const msg = fieldErrors
        ? Object.values(fieldErrors).join(", ")
        : err.response?.data?.message || "Registration failed.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-navy via-primary to-accent p-4">
      <div className="w-full max-w-md">
        {/* Logo/Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-white rounded-full mb-4 shadow-lg">
            <span className="text-3xl">💼</span>
          </div>
          <h1 className="text-4xl font-bold text-white mb-2">Career Portal</h1>
          <p className="text-blue-100 font-medium">Start your job search journey</p>
        </div>

        {/* Register Card */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 border border-gray-100">
          <h2 className="text-2xl font-bold text-navy mb-2">Create Account</h2>
          <p className="text-gray-600 text-sm mb-6">Join thousands tracking their job search</p>

          {error && (
            <div className="mb-6 p-4 text-sm text-danger bg-danger/10 border border-danger rounded-lg flex items-start gap-3">
              <span className="text-lg">⚠️</span>
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-semibold text-navy mb-2">Full Name</label>
              <input
                placeholder="John Doe"
                type="text"
                required
                value={form.fullName}
                onChange={update("fullName")}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-navy mb-2">Email Address</label>
              <input
                placeholder="you@example.com"
                type="email"
                required
                value={form.email}
                onChange={update("email")}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-navy mb-2">Password</label>
              <input
                placeholder="Minimum 8 characters"
                type="password"
                required
                value={form.password}
                onChange={update("password")}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-navy mb-2">Phone (Optional)</label>
              <input
                placeholder="+1 (555) 000-0000"
                value={form.phone}
                onChange={update("phone")}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-navy mb-2">Location (Optional)</label>
              <input
                placeholder="City, Country"
                value={form.location}
                onChange={update("location")}
                className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent transition"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-navy to-primary text-white rounded-lg py-3 text-sm font-bold hover:shadow-lg transition disabled:opacity-60 mt-6"
            >
              {loading ? "Creating account..." : "Create Account"}
            </button>
          </form>

          <div className="mt-6 pt-6 border-t border-gray-200">
            <p className="text-center text-sm text-gray-600">
              Already have an account?{" "}
              <Link to="/login" className="text-accent font-semibold hover:text-primary transition">
                Sign in
              </Link>
            </p>
          </div>
        </div>

        {/* Footer Info */}
        <div className="mt-6 text-center text-white text-sm">
          <p className="opacity-90">© 2026 Career Portal. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
}
