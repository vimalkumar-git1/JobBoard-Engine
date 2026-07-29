import { useEffect, useState } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import client from "../api/client";
import Layout from "../components/Layout";

const COLUMNS = [
  { key: "SAVED", label: "📌 Saved", color: "from-blue-50 to-blue-100", badge: "bg-blue-200" },
  { key: "APPLIED", label: "📤 Applied", color: "from-amber-50 to-amber-100", badge: "bg-amber-200" },
  { key: "INTERVIEWING", label: "💬 Interviewing", color: "from-purple-50 to-purple-100", badge: "bg-purple-200" },
  { key: "OFFERED", label: "🎉 Offered", color: "from-emerald-50 to-emerald-100", badge: "bg-emerald-200" },
  { key: "REJECTED", label: "❌ Rejected", color: "from-red-50 to-red-100", badge: "bg-red-200" },
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
        <p className="text-gray-500 text-sm">⏳ Loading applications...</p>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-navy mb-2">Application Tracker</h1>
        <p className="text-gray-600">Track your job applications through the hiring process. Drag cards to update status.</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-danger/10 border border-danger text-danger rounded-lg">
          ⚠️ {error}
        </div>
      )}

      {applications.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-xl border border-dashed border-gray-300">
          <p className="text-gray-600 text-lg mb-2">📭 No applications yet</p>
          <p className="text-gray-500 text-sm">Go to the <strong>Jobs</strong> tab and click "💾 Save" on a job to get started.</p>
        </div>
      ) : (
        <DragDropContext onDragEnd={onDragEnd}>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            {COLUMNS.map((col) => (
              <Droppable droppableId={col.key} key={col.key}>
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                    className={`rounded-xl p-4 min-h-[400px] transition-all duration-200 border-2 ${
                      snapshot.isDraggingOver
                        ? `border-accent bg-accent/5`
                        : `border-gray-200 bg-white hover:shadow-md`
                    }`}
                  >
                    <div className={`flex items-center gap-2 mb-4 pb-3 border-b-2 border-gray-200`}>
                      <h3 className="text-sm font-bold text-navy flex-1">
                        {col.label}
                      </h3>
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${col.badge}`}>
                        {grouped[col.key].length}
                      </span>
                    </div>
                    <div className="space-y-2">
                      {grouped[col.key].map((app, index) => (
                        <Draggable
                          draggableId={String(app.id)}
                          index={index}
                          key={app.id}
                        >
                          {(dragProvided, dragSnapshot) => (
                            <div
                              ref={dragProvided.innerRef}
                              {...dragProvided.draggableProps}
                              {...dragProvided.dragHandleProps}
                              className={`bg-gradient-to-br from-white to-gray-50 border border-gray-200 rounded-lg p-3 shadow-sm hover:shadow-md transition-all cursor-grab active:cursor-grabbing ${
                                dragSnapshot.isDragging ? "shadow-lg ring-2 ring-accent" : ""
                              }`}
                            >
                              <p className="text-sm font-bold text-navy leading-snug">
                                {app.jobTitle}
                              </p>
                              <p className="text-xs text-gray-600 font-medium mt-1">
                                {app.companyName}
                              </p>
                              {app.resumeVersionLabel && (
                                <span className="inline-block mt-2 text-xs bg-accent/20 text-accent px-2.5 py-0.5 rounded-full font-semibold">
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

      <div className="mt-10 bg-blue-50 border border-blue-200 rounded-xl p-6 text-sm">
        <p className="text-blue-900 font-semibold mb-2">💡 Pro Tips:</p>
        <ul className="text-blue-800 space-y-1 text-xs">
          <li>• Drag jobs between columns to track their status</li>
          <li>• Start with "📌 Saved" when you find a job you like</li>
          <li>• Move to "📤 Applied" once you submit your application</li>
          <li>• Track progress through "💬 Interviewing" and "🎉 Offered"</li>
        </ul>
      </div>
    </Layout>
  );
}
