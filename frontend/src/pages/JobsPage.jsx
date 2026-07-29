import { useEffect, useState } from "react";
import client from "../api/client";
import Layout from "../components/Layout";

export default function JobsPage() {
  const [filters, setFilters] = useState({ keyword: "", location: "", workMode: "", techStack: "" });
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [savingJobId, setSavingJobId] = useState(null);
  const [selectedJob, setSelectedJob] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");

  const search = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const params = Object.fromEntries(
        Object.entries(filters).filter(([, v]) => v !== "")
      );
      const res = await client.get("/jobs/search", { params });
      setJobs(res.data.content || []);
    } catch (err) {
      setError("Could not load jobs. Is the backend running on localhost:8080?");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    search();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Prevent background scroll when modal is open
  useEffect(() => {
    if (selectedJob) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [selectedJob]);

  const saveJob = async (jobId) => {
    setSavingJobId(jobId);
    try {
      await client.post("/applications", { jobId });
      setSuccessMessage("Job saved to applications! 🎉");
      setTimeout(() => setSuccessMessage(""), 3000);
      setSelectedJob(null);
    } catch (err) {
      if (err.response?.status === 409) {
        setSuccessMessage("Already saved to applications ✓");
      }
      setTimeout(() => setSuccessMessage(""), 3000);
    } finally {
      setSavingJobId(null);
    }
  };

  const workModeColors = {
    REMOTE: "bg-emerald-50 text-emerald-700 border-emerald-200",
    HYBRID: "bg-blue-50 text-blue-700 border-blue-200",
    ONSITE: "bg-orange-50 text-orange-700 border-orange-200",
  };

  return (
    <Layout>
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-navy mb-2">Find Your Next Opportunity</h1>
        <p className="text-gray-600">Search and save jobs from top companies</p>
      </div>

      {successMessage && (
        <div className="mb-6 p-4 bg-success/10 border border-success text-success rounded-lg flex items-center gap-2 animate-in">
          ✓ {successMessage}
        </div>
      )}

      <form onSubmit={search} className="bg-white rounded-xl shadow-md p-6 mb-8 border border-gray-100">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-4">
          <input
            placeholder="Keyword (e.g. Java, React)"
            value={filters.keyword}
            onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
            className="border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent"
          />
          <input
            placeholder="Tech stack (Java,Spring Boot)"
            value={filters.techStack}
            onChange={(e) => setFilters({ ...filters, techStack: e.target.value })}
            className="border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent"
          />
          <input
            placeholder="Location (City/Remote)"
            value={filters.location}
            onChange={(e) => setFilters({ ...filters, location: e.target.value })}
            className="border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent"
          />
          <select
            value={filters.workMode}
            onChange={(e) => setFilters({ ...filters, workMode: e.target.value })}
            className="border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-accent/50 focus:border-accent"
          >
            <option value="">Any work mode</option>
            <option value="REMOTE">🌍 Remote</option>
            <option value="HYBRID">🏢 Hybrid</option>
            <option value="ONSITE">📍 Onsite</option>
          </select>
          <button className="bg-gradient-to-r from-navy to-primary text-white rounded-lg px-6 py-2.5 text-sm font-semibold hover:shadow-lg transition-all duration-200">
            🔍 Search
          </button>
        </div>
        <p className="text-xs text-gray-500">💡 Tip: Leave filters empty to see all jobs</p>
      </form>

      {error && (
        <div className="mb-6 p-4 bg-danger/10 border border-danger text-danger rounded-lg">
          ⚠️ {error}
        </div>
      )}

      {loading && (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin">⏳</div>
          <p className="text-gray-600 ml-3">Loading jobs...</p>
        </div>
      )}

      {!loading && jobs.length === 0 && !error && (
        <div className="text-center py-12 bg-white rounded-xl border border-dashed border-gray-300">
          <p className="text-gray-500">📭 No jobs found. Try adjusting your filters or search again.</p>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {jobs.map((job) => (
          <div
            key={job.id}
            className="bg-white border border-gray-200 rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 p-5 cursor-pointer hover:border-accent/50 group"
          >
            <div className="mb-3 flex items-start justify-between">
              <div className="flex-1">
                <h3 className="font-bold text-navy text-lg group-hover:text-accent transition mb-1">
                  {job.title}
                </h3>
                <p className="text-sm text-secondary font-semibold">{job.companyName}</p>
              </div>
              <span className={`text-xs px-3 py-1 rounded-full font-semibold whitespace-nowrap ml-2 border ${workModeColors[job.workMode] || "bg-gray-50 text-gray-600 border-gray-200"}`}>
                {job.workMode === "REMOTE" && "🌍"} {job.workMode === "HYBRID" && "🏢"} {job.workMode === "ONSITE" && "📍"} {job.workMode}
              </span>
            </div>

            <div className="flex gap-3 text-xs text-gray-600 mb-3 pb-3 border-b border-gray-100">
              <span>📍 {job.location}</span>
              {job.techStack && <span>🛠️ {job.techStack.split(",")[0]}</span>}
            </div>

            <p className="text-sm text-gray-700 mb-4 line-clamp-2">
              {job.description}
            </p>

            {job.techStack && (
              <div className="flex flex-wrap gap-1.5 mb-4">
                {job.techStack.split(",").slice(0, 3).map((skill, i) => (
                  <span key={i} className="bg-accent/10 text-accent text-xs px-2.5 py-1 rounded-full font-medium">
                    {skill.trim()}
                  </span>
                ))}
                {job.techStack.split(",").length > 3 && (
                  <span className="text-xs text-gray-500 px-2.5 py-1">+{job.techStack.split(",").length - 3}</span>
                )}
              </div>
            )}

            <div className="flex gap-2">
              <button
                onClick={() => saveJob(job.id)}
                disabled={savingJobId === job.id}
                className="flex-1 text-sm bg-gradient-to-r from-navy to-primary text-white rounded-lg px-3 py-2.5 hover:shadow-md transition disabled:opacity-60 font-semibold"
              >
                {savingJobId === job.id ? "💾 Saving..." : "💾 Save"}
              </button>
              <button
                onClick={() => setSelectedJob(job)}
                className="flex-1 text-sm bg-accent/10 text-accent rounded-lg px-3 py-2.5 hover:bg-accent/20 transition font-semibold"
              >
                View Details →
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Job Detail Modal */}
      {selectedJob && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
            {/* Header */}
            <div className="sticky top-0 bg-gradient-to-r from-navy to-primary text-white p-6 flex justify-between items-start">
              <div>
                <h2 className="text-3xl font-bold mb-1">{selectedJob.title}</h2>
                <p className="text-lg text-blue-100">{selectedJob.companyName}</p>
              </div>
              <button
                onClick={() => setSelectedJob(null)}
                className="text-3xl hover:bg-white/20 w-10 h-10 flex items-center justify-center rounded-lg transition"
              >
                ✕
              </button>
            </div>

            <div className="p-6 space-y-6">
              {/* Key Info Grid */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="bg-blue-50 rounded-lg p-4 border border-blue-100">
                  <p className="text-xs text-secondary uppercase font-bold mb-1">📍 Location</p>
                  <p className="text-sm font-semibold text-navy">{selectedJob.location}</p>
                </div>
                <div className="bg-emerald-50 rounded-lg p-4 border border-emerald-100">
                  <p className="text-xs text-secondary uppercase font-bold mb-1">🏢 Work Mode</p>
                  <p className="text-sm font-semibold text-navy">{selectedJob.workMode}</p>
                </div>
                {selectedJob.salaryMin && selectedJob.salaryMax && (
                  <div className="bg-orange-50 rounded-lg p-4 border border-orange-100">
                    <p className="text-xs text-secondary uppercase font-bold mb-1">💰 Salary</p>
                    <p className="text-sm font-semibold text-navy">₹{selectedJob.salaryMin.toLocaleString()} - ₹{selectedJob.salaryMax.toLocaleString()}</p>
                  </div>
                )}
                {selectedJob.minExperience && selectedJob.maxExperience && (
                  <div className="bg-purple-50 rounded-lg p-4 border border-purple-100">
                    <p className="text-xs text-secondary uppercase font-bold mb-1">👤 Experience</p>
                    <p className="text-sm font-semibold text-navy">{selectedJob.minExperience} - {selectedJob.maxExperience} yrs</p>
                  </div>
                )}
              </div>

              {/* Tech Stack */}
              {selectedJob.techStack && (
                <div>
                  <h3 className="text-sm font-bold text-navy mb-3 uppercase">🛠️ Tech Stack</h3>
                  <div className="flex flex-wrap gap-2">
                    {selectedJob.techStack.split(",").map((skill, i) => (
                      <span key={i} className="bg-accent/10 text-accent text-sm px-4 py-2 rounded-full font-semibold">
                        {skill.trim()}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {/* Description */}
              <div>
                <h3 className="text-sm font-bold text-navy mb-3 uppercase">📝 Job Description</h3>
                <div className="bg-gray-50 rounded-lg p-4 border border-gray-200 max-h-64 overflow-y-auto text-sm text-gray-700 whitespace-pre-wrap leading-relaxed">
                  {selectedJob.description}
                </div>
              </div>

              {/* Source Link */}
              {selectedJob.sourceUrl && (
                <div className="pt-4 border-t border-gray-200">
                  <a
                    href={selectedJob.sourceUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-2 text-accent hover:text-primary font-semibold text-sm hover:underline"
                  >
                    🔗 View original job posting
                  </a>
                </div>
              )}
            </div>

            {/* Footer Actions */}
            <div className="sticky bottom-0 bg-gray-50 border-t border-gray-200 p-6 flex gap-3">
              <button
                onClick={() => {
                  saveJob(selectedJob.id);
                }}
                disabled={savingJobId === selectedJob.id}
                className="flex-1 bg-gradient-to-r from-navy to-primary text-white rounded-lg px-6 py-3 hover:shadow-lg transition disabled:opacity-60 font-bold text-lg"
              >
                {savingJobId === selectedJob.id ? "💾 Saving..." : "💾 Save to Applications"}
              </button>
              <button
                onClick={() => setSelectedJob(null)}
                className="px-6 py-3 bg-gray-200 text-navy rounded-lg hover:bg-gray-300 transition font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}
