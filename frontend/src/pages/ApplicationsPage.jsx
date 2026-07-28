import { useEffect, useState } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import client from "../api/client";
import Layout from "../components/Layout";

const COLUMNS = [
  { key: "SAVED", label: "Saved" },
  { key: "APPLIED", label: "Applied" },
  { key: "INTERVIEWING", label: "Interviewing" },
  { key: "OFFERED", label: "Offered" },
  { key: "REJECTED", label: "Rejected" },
];

export default function ApplicationsPage() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {
    setLoading(true);
    try {
      const res = await client.get("/applications");
      setApplications(res.data);
    } catch (err) {
      setError("Could not load applications.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const grouped = COLUMNS.reduce((acc, col) => {
    acc[col.key] = applications.filter((a) => a.status === col.key);
    return acc;
  }, {});

  const onDragEnd = async (result) => {
    const { destination, draggableId } = result;
    if (!destination) return;

    const newStatus = destination.droppableId;
    const appId = Number(draggableId);

    // optimistic update
    setApplications((prev) =>
      prev.map((a) => (a.id === appId ? { ...a, status: newStatus } : a))
    );

    try {
      await client.patch(`/applications/${appId}/status`, { status: newStatus });
    } catch (err) {
      setError("Could not update status — reverting.");
      load();
    }
  };

  if (loading) {
    return (
      <Layout>
        <p className="text-gray-500 text-sm">Loading applications...</p>
      </Layout>
    );
  }

  return (
    <Layout>
      <h2 className="text-xl font-bold text-navy mb-4">Application Tracker</h2>
      {error && <p className="text-red-600 text-sm mb-4">{error}</p>}

      {applications.length === 0 ? (
        <p className="text-sm text-gray-500">
          No applications yet. Go to the Jobs tab and click "Save to Applications" on a job.
        </p>
      ) : (
        <DragDropContext onDragEnd={onDragEnd}>
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
            {COLUMNS.map((col) => (
              <Droppable droppableId={col.key} key={col.key}>
                {(provided) => (
                  <div
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                    className="bg-white border border-gray-200 rounded-lg p-3 min-h-[300px]"
                  >
                    <h3 className="text-sm font-semibold text-navy mb-3">
                      {col.label} <span className="text-gray-400">({grouped[col.key].length})</span>
                    </h3>
                    <div className="space-y-2">
                      {grouped[col.key].map((app, index) => (
                        <Draggable draggableId={String(app.id)} index={index} key={app.id}>
                          {(dragProvided) => (
                            <div
                              ref={dragProvided.innerRef}
                              {...dragProvided.draggableProps}
                              {...dragProvided.dragHandleProps}
                              className="bg-surface border border-gray-200 rounded p-3 shadow-sm"
                            >
                              <p className="text-sm font-medium text-navy">{app.jobTitle}</p>
                              <p className="text-xs text-gray-500">{app.companyName}</p>
                              {app.resumeVersionLabel && (
                                <span className="inline-block mt-2 text-xs bg-blue-50 text-accent px-2 py-0.5 rounded">
                                  {app.resumeVersionLabel}
                                </span>
                              )}
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </div>
                  </div>
                )}
              </Droppable>
            ))}
          </div>
        </DragDropContext>
      )}
    </Layout>
  );
}
