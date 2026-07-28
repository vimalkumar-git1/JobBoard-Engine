import { useState } from "react";
import client from "../api/client";
import Layout from "../components/Layout";

export default function CoverLetterPage() {
  const [jobId, setJobId] = useState("");
  const [matchedSkills, setMatchedSkills] = useState("");
  const [letter, setLetter] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const generate = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setLetter("");
    try {
      const params = { jobId };
      if (matchedSkills) params.matchedSkills = matchedSkills;
      const res = await client.get("/cover-letters/generate", { params });
      setLetter(res.data.coverLetter);
    } catch (err) {
      setError(err.response?.data?.message || "Could not generate cover letter. Check the Job ID.");
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(letter);
  };

  return (
    <Layout>
      <h2 className="text-xl font-bold text-navy mb-4">Cover Letter Generator</h2>

      <form onSubmit={generate} className="bg-white border border-gray-200 rounded-lg p-5 mb-6 flex flex-col gap-3 max-w-lg">
        <div>
          <label className="block text-sm text-gray-600 mb-1">Job ID</label>
          <input
            value={jobId}
            onChange={(e) => setJobId(e.target.value)}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
            placeholder="e.g. 3"
          />
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">
            Matched skills (optional — from your ATS match result)
          </label>
          <input
            value={matchedSkills}
            onChange={(e) => setMatchedSkills(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
            placeholder="Java, Spring Boot, MySQL"
          />
        </div>
        <button
          disabled={loading}
          className="bg-navy text-white rounded px-4 py-2 text-sm font-semibold hover:bg-accent transition disabled:opacity-60 self-start"
        >
          {loading ? "Generating..." : "Generate Cover Letter"}
        </button>
      </form>

      {error && <p className="text-red-600 text-sm mb-4">{error}</p>}

      {letter && (
        <div className="bg-white border border-gray-200 rounded-lg p-5 max-w-2xl">
          <div className="flex justify-between items-center mb-3">
            <h3 className="font-semibold text-navy">Generated Letter</h3>
            <button onClick={copyToClipboard} className="text-sm text-accent font-medium">
              Copy
            </button>
          </div>
          <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans">{letter}</pre>
        </div>
      )}
    </Layout>
  );
}
