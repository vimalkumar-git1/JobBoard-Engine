import { useState } from "react";
import client from "../api/client";
import Layout from "../components/Layout";

export default function CoverLetterPage() {
  const [jobId, setJobId] = useState("");
  const [jobTitle, setJobTitle] = useState("");
  const [jobDescription, setJobDescription] = useState("");
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
      let res;

      if (jobDescription.trim()) {
        if (!jobTitle.trim()) {
          throw new Error("Please enter a job title when generating from a job description.");
        }

        res = await client.post("/cover-letters/generate-from-description", {
          jobTitle,
          jobDescription,
        });
      } else {
        if (!jobId.trim()) {
          throw new Error("Please enter a numeric Job ID when not using a job description.");
        }

        const numericJobId = Number(jobId);
        if (Number.isNaN(numericJobId)) {
          throw new Error("Job ID must be a number.");
        }

        const params = { jobId: numericJobId };
        if (matchedSkills) params.matchedSkills = matchedSkills;
        res = await client.get("/cover-letters/generate", { params });
      }

      setLetter(res.data.coverLetter);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Could not generate cover letter.");
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
          <label className="block text-sm text-gray-600 mb-1">Job ID (numeric)</label>
          <input
            value={jobId}
            onChange={(e) => setJobId(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
            placeholder="e.g. 3"
          />
          <p className="text-xs text-gray-500 mt-1">Leave blank when generating from a job description.</p>
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">Job title</label>
          <input
            value={jobTitle}
            onChange={(e) => setJobTitle(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
            placeholder="e.g. Java Backend Developer"
          />
          <p className="text-xs text-gray-500 mt-1">Required only when using a job description.</p>
        </div>
        <div>
          <label className="block text-sm text-gray-600 mb-1">Job description</label>
          <textarea
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm min-h-[120px]"
            placeholder="Paste the full job description here to generate a custom cover letter"
          />
          <p className="text-xs text-gray-500 mt-1">If filled, this will generate a letter from the pasted job description instead of using a job ID.</p>
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
