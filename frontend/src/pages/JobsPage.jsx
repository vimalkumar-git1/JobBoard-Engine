import { useEffect, useState } from "react";
import client from "../api/client";
import Layout from "../components/Layout";

export default function JobsPage() {
  const [filters, setFilters] = useState({ keyword: "", location: "", workMode: "", techStack: "" });
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [savingJobId, setSavingJobId] = useState(null);

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

  const saveJob = async (jobId) => {
    setSavingJobId(jobId);
    try {
      await client.post("/applications", { jobId });
    } catch (err) {
      // 409 = already saved, which is fine to ignore silently here
    } finally {
      setSavingJobId(null);
    }
  };

  return (
    <Layout>
      <h2 className="text-xl font-bold text-navy mb-4">Search Jobs</h2>

      <form onSubmit={search} className="bg-white rounded-lg border border-gray-200 p-4 mb-6 grid grid-cols-1 md:grid-cols-5 gap-3">
        <input
          placeholder="Keyword (e.g. Java)"
          value={filters.keyword}
          onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        />
        <input
          placeholder="Tech stack (Java,Spring Boot)"
          value={filters.techStack}
          onChange={(e) => setFilters({ ...filters, techStack: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        />
        <input
          placeholder="Location"
          value={filters.location}
          onChange={(e) => setFilters({ ...filters, location: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        />
        <select
          value={filters.workMode}
          onChange={(e) => setFilters({ ...filters, workMode: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        >
          <option value="">Any work mode</option>
          <option value="REMOTE">Remote</option>
          <option value="HYBRID">Hybrid</option>
          <option value="ONSITE">Onsite</option>
        </select>
        <button className="bg-navy text-white rounded px-4 py-2 text-sm font-semibold hover:bg-accent transition">
          Search
        </button>
      </form>

      {error && <p className="text-red-600 text-sm mb-4">{error}</p>}
      {loading && <p className="text-gray-500 text-sm">Loading jobs...</p>}

      {!loading && jobs.length === 0 && !error && (
        <p className="text-gray-500 text-sm">
          No jobs found. Add some via the backend's <code>POST /api/jobs</code> endpoint (Swagger UI) to see them here.
        </p>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {jobs.map((job) => (
          <div key={job.id} className="bg-white border border-gray-200 rounded-lg p-4">
            <div className="flex justify-between items-start">
              <div>
                <h3 className="font-semibold text-navy">{job.title}</h3>
                <p className="text-sm text-gray-600">{job.companyName}</p>
              </div>
              <span className="text-xs bg-blue-50 text-accent px-2 py-1 rounded font-medium">
                {job.workMode}
              </span>
            </div>
            <p className="text-sm text-gray-500 mt-2">{job.location}</p>
            <p className="text-xs text-gray-500 mt-1">{job.techStack}</p>
            <p className="text-sm text-gray-700 mt-2 line-clamp-3">{job.description}</p>
            <button
              onClick={() => saveJob(job.id)}
              disabled={savingJobId === job.id}
              className="mt-3 text-sm bg-navy text-white rounded px-3 py-1.5 hover:bg-accent transition disabled:opacity-60"
            >
              {savingJobId === job.id ? "Saving..." : "Save to Applications"}
            </button>
          </div>
        ))}
      </div>
    </Layout>
  );
}
