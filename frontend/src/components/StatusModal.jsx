import { useState } from "react";
import { updateShipmentStatus, getErrorInfo } from "../api";

const NEXT = {
  ASSIGNED: ["OUT_FOR_DELIVERY"],
  OUT_FOR_DELIVERY: ["DELIVERED", "DELIVERY_FAILED"],
  DELIVERED: [],
  DELIVERY_FAILED: [],
};

export default function StatusModal({ shipment, onClose, onUpdated }) {
  const options = NEXT[shipment.status] || [];
  const [status, setStatus] = useState(options[0] || "");
  const [reason, setReason] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    if (status === "DELIVERY_FAILED" && !reason.trim()) {
      setError("Please enter a failure reason.");
      return;
    }
    setSaving(true);
    setError("");
    try {
      const updated = await updateShipmentStatus(shipment.id, {
        status,
        failureReason: status === "DELIVERY_FAILED" ? reason.trim() : null,
      });
      onUpdated(updated);
    } catch (err) {
      setError(getErrorInfo(err).message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>Update status</h3>
        <p>
          {shipment.trackingNumber} · currently{" "}
          <strong>{shipment.status.replaceAll("_", " ")}</strong>
        </p>
        {error && <div className="alert error">{error}</div>}

        <label>
          New status
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            {options.map((o) => (
              <option key={o} value={o}>
                {o.replaceAll("_", " ")}
              </option>
            ))}
          </select>
        </label>

        {status === "DELIVERY_FAILED" && (
          <label>
            Failure reason
            <textarea
              rows={2}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="e.g. Customer not available"
            />
          </label>
        )}

        <div className="modal-actions">
          <button className="secondary" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button onClick={handleSave} disabled={saving}>
            {saving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}