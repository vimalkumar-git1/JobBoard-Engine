import { useEffect, useState } from "react";
import client from "../api/client";
import Layout from "../components/Layout";

export default function ResumePage() {
  const [file, setFile] = useState(null);
  const [resumeId, setResumeId] = useState(null);
  const [skills, setSkills] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [jobId, setJobId] = useState("");
  const [jobDescription, setJobDescription] = useState("");
  const [jobTitle, setJobTitle] = useState("");
  const [matchResult, setMatchResult] = useState(null);
  const [matching, setMatching] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [versions, setVersions] = useState([]);
  const [error, setError] = useState("");
  const [activeTab, setActiveTab] = useState("jobId"); // "jobId" or "description"
  const [selectedMissingKeywords, setSelectedMissingKeywords] = useState([]);

  const loadVersions = async () => {
    try {
      const res = await client.get("/resumes/versions");
      setVersions(res.data);
    } catch (err) {
      // non-fatal: version history is a nice-to-have on this screen
    }
  };

  const loadResumes = async () => {
    try {
      const res = await client.get("/resumes");
      if (res.data.length > 0) {
        setResumeId(res.data[0].resumeId);
        setSkills(res.data[0].extractedSkills || []);
      }
    } catch (err) {
      // ignore
    }
  };

  useEffect(() => {
    loadVersions();
    loadResumes();
  }, []);

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;
    setUploading(true);
    setError("");
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await client.post("/resumes/upload", formData);
      setResumeId(res.data.resumeId);
      setSkills(res.data.extractedSkills);
    } catch (err) {
      setError(err.response?.data?.message || "Upload failed.");
    } finally {
      setUploading(false);
    }
  };

  const handleMatch = async () => {
    if (!resumeId) return;
    if (activeTab === "description" && !jobDescription) {
      setError("Paste a job description first.");
      return;
    }
    if (activeTab === "jobId" && !jobId) {
      setError("Enter a job ID first.");
      return;
    }

    setMatching(true);
    setMatchResult(null);
    setSelectedMissingKeywords([]);
    setError("");
    try {
      let res;
      if (activeTab === "description") {
        res = await client.post(`/resumes/${resumeId}/match-description`, {
          jobDescription,
          jobTitle,
        });
      } else {
        res = await client.get(`/resumes/${resumeId}/match`, { params: { jobId } });
      }
      setMatchResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Could not compute match.");
    } finally {
      setMatching(false);
    }
  };

  const handleGenerateVersion = async () => {
    if (!resumeId) return;
    if (activeTab === "jobId" && !jobId) return;
    if (activeTab === "description" && !jobDescription) return;

    setGenerating(true);
    setError("");
    try {
      if (activeTab === "description") {
        await client.post(`/resumes/${resumeId}/versions-from-description`, {
          jobDescription,
          jobTitle,
          selectedKeywords: selectedMissingKeywords,
        });
      } else {
        await client.post(`/resumes/${resumeId}/versions`, null, { params: { jobId } });
      }
      await loadVersions();
    } catch (err) {
      setError(err.response?.data?.message || "Could not generate a tailored version.");
    } finally {
      setGenerating(false);
    }
  };

  const handleDownloadVersion = async (version) => {
    try {
      const res = await client.get(version.downloadUrl, {
        responseType: "blob",
      });
      const blob = new Blob([res.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `${version.versionLabel}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError(err.response?.data?.message || "Could not download the resume PDF.");
    }
  };

  return (
    <Layout>
      <h2 className="text-xl font-bold text-navy mb-4">Resume &amp; ATS Match</h2>

      <div className="bg-white border border-gray-200 rounded-lg p-5 mb-6">
        <h3 className="font-semibold text-navy mb-3">1. Upload your resume (PDF)</h3>
        <form onSubmit={handleUpload} className="flex items-center gap-3">
          <input
            type="file"
            accept="application/pdf"
            onChange={(e) => setFile(e.target.files[0])}
            className="text-sm"
          />
          <button
            disabled={!file || uploading}
            className="bg-navy text-white rounded px-4 py-2 text-sm font-semibold hover:bg-accent transition disabled:opacity-60"
          >
            {uploading ? "Uploading..." : "Upload"}
          </button>
        </form>

        {skills.length > 0 && (
          <div className="mt-4">
            <p className="text-sm text-gray-600 mb-1">Skills detected in your resume:</p>
            <div className="flex flex-wrap gap-1.5">
              {skills.map((s) => (
                <span key={s} className="text-xs bg-blue-50 text-accent px-2 py-1 rounded font-medium">
                  {s}
                </span>
              ))}
            </div>
          </div>
        )}
      </div>

      {resumeId && (
        <div className="bg-white border border-gray-200 rounded-lg p-5 mb-6">
          <h3 className="font-semibold text-navy mb-3">2. Compare against a job</h3>

          <div className="flex gap-2 mb-4">
            <button
              onClick={() => setActiveTab("jobId")}
              className={`px-3 py-2 rounded ${activeTab === "jobId" ? "bg-navy text-white" : "bg-gray-100 text-gray-700"}`}>
              Job ID
            </button>
            <button
              onClick={() => setActiveTab("description")}
              className={`px-3 py-2 rounded ${activeTab === "description" ? "bg-navy text-white" : "bg-gray-100 text-gray-700"}`}>
              Job Description
            </button>
          </div>

          {activeTab === "jobId" ? (
            <div className="flex items-center gap-3">
              <input
                placeholder="Job ID (see it in the Jobs tab)"
                value={jobId}
                onChange={(e) => setJobId(e.target.value)}
                className="border border-gray-300 rounded px-3 py-2 text-sm w-64"
              />
              <button
                onClick={handleMatch}
                disabled={!jobId || matching}
                className="bg-navy text-white rounded px-4 py-2 text-sm font-semibold hover:bg-accent transition disabled:opacity-60"
              >
                {matching ? "Scoring..." : "Compute Match Score"}
              </button>
            </div>
          ) : (
            <div className="space-y-3">
              <input
                placeholder="Target role / job title"
                value={jobTitle}
                onChange={(e) => setJobTitle(e.target.value)}
                className="border border-gray-300 rounded px-3 py-2 text-sm w-full"
              />
              <textarea
                rows={6}
                placeholder="Paste the job description here"
                value={jobDescription}
                onChange={(e) => setJobDescription(e.target.value)}
                className="border border-gray-300 rounded px-3 py-2 text-sm w-full resize-none"
              />
              <button
                onClick={handleMatch}
                disabled={!jobDescription || matching}
                className="bg-navy text-white rounded px-4 py-2 text-sm font-semibold hover:bg-accent transition disabled:opacity-60"
              >
                {matching ? "Scoring..." : "Compute Match Score"}
              </button>
            </div>
          )}

          {matchResult && (
            <div className="mt-4">
              <p className="text-2xl font-bold text-navy">{matchResult.matchScorePercent}% match</p>
              <div className="grid grid-cols-2 gap-4 mt-3">
                <div>
                  <p className="text-sm font-medium text-green-700 mb-1">Matched skills</p>
                  <div className="flex flex-wrap gap-1.5">
                    {matchResult.matchedSkills.map((s) => (
                      <span key={s} className="text-xs bg-green-50 text-green-700 px-2 py-1 rounded">{s}</span>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="text-sm font-medium text-red-700 mb-1">Missing skills</p>
                  <div className="flex flex-wrap gap-1.5">
                    {matchResult.missingSkills.map((s) => (
                      <span key={s} className="text-xs bg-red-50 text-red-700 px-2 py-1 rounded">{s}</span>
                    ))}
                  </div>
                </div>
              </div>

              {activeTab === "description" && matchResult.missingSkills.length > 0 && (
                <div className="mt-4">
                  <p className="text-sm font-medium text-gray-800 mb-2">Select missing keywords you want included:</p>
                  <div className="grid grid-cols-2 gap-2">
                    {matchResult.missingSkills.map((skill) => (
                      <label key={skill} className="inline-flex items-center gap-2 text-sm border border-gray-200 rounded px-3 py-2">
                        <input
                          type="checkbox"
                          value={skill}
                          checked={selectedMissingKeywords.includes(skill)}
                          onChange={(e) => {
                            const value = e.target.value;
                            setSelectedMissingKeywords((current) =>
                              current.includes(value)
                                ? current.filter((item) => item !== value)
                                : [...current, value]
                            );
                          }}
                        />
                        {skill}
                      </label>
                    ))}
                  </div>
                </div>
              )}

              <button
                onClick={handleGenerateVersion}
                disabled={generating}
                className="mt-4 bg-accent text-white rounded px-4 py-2 text-sm font-semibold hover:bg-navy transition disabled:opacity-60"
              >
                {generating ? "Generating..." : "Generate Tailored Resume Version"}
              </button>
            </div>
          )}
        </div>
      )}

      {error && <p className="text-red-600 text-sm mb-4">{error}</p>}

      <div className="bg-white border border-gray-200 rounded-lg p-5">
        <h3 className="font-semibold text-navy mb-3">Resume Version History</h3>
        {versions.length === 0 && <p className="text-sm text-gray-500">No tailored versions generated yet.</p>}
        <div className="space-y-2">
          {versions.map((v) => (
            <div key={v.id} className="flex justify-between items-center border-b border-gray-100 pb-2">
              <div>
                <p className="text-sm font-medium text-navy">{v.versionLabel}</p>
                <p className="text-xs text-gray-500">{v.targetRole}</p>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-accent">{v.matchScore}%</span>
                <button
                  onClick={() => handleDownloadVersion(v)}
                  className="text-sm text-navy underline"
                >
                  Download
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}
