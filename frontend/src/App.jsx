import { useCallback, useEffect, useState } from "react";
import "./App.css";
import { getShipments, getErrorInfo } from "./api";
import ShipmentForm from "./components/ShipmentForm";
import ShipmentTable from "./components/ShipmentTable";
import StatusModal from "./components/StatusModal";

const STATUSES = ["ASSIGNED", "OUT_FOR_DELIVERY", "DELIVERED", "DELIVERY_FAILED"];

export default function App() {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState(null);
  const [statusFilter, setStatusFilter] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState(null);

  // Wait 400 ms after typing stops before searching
  useEffect(() => {
    const timer = setTimeout(() => setSearch(searchInput), 400);
    return () => clearTimeout(timer);
  }, [searchInput]);

  const loadShipments = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setShipments(await getShipments(statusFilter, search));
    } catch (err) {
      setError(getErrorInfo(err).message);
    } finally {
      setLoading(false);
    }
  }, [statusFilter, search]);

  useEffect(() => {
    loadShipments();
  }, [loadShipments]);

  // Hide success message after 4 seconds
  useEffect(() => {
    if (!notice) return;
    const timer = setTimeout(() => setNotice(null), 4000);
    return () => clearTimeout(timer);
  }, [notice]);

  const handleCreated = (shipment) => {
    setNotice({ type: "success", text: `Shipment ${shipment.trackingNumber} created.` });
    loadShipments();
  };

  const handleUpdated = (shipment) => {
    setSelected(null);
    setNotice({
      type: "success",
      text: `Shipment ${shipment.trackingNumber} is now ${shipment.status.replaceAll("_", " ")}.`,
    });
    loadShipments();
  };

  return (
    <div className="container">
      <header>
        <h1>Delivery Agent</h1>
        <p>Create shipments and update their delivery status</p>
      </header>

      {notice && <div className={`alert ${notice.type}`}>{notice.text}</div>}

      <ShipmentForm onCreated={handleCreated} />

      <section className="card">
        <h2>Shipments</h2>
        <div className="toolbar">
          <input
            type="search"
            placeholder="Search by tracking number or customer name"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All statuses</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s.replaceAll("_", " ")}
              </option>
            ))}
          </select>
        </div>

        {loading && <p className="state">Loading shipments...</p>}

        {!loading && error && (
          <div className="alert error">
            {error} <button className="small" onClick={loadShipments}>Retry</button>
          </div>
        )}

        {!loading && !error && shipments.length === 0 && (
          <p className="state">No shipments found.</p>
        )}

        {!loading && !error && shipments.length > 0 && (
          <ShipmentTable shipments={shipments} onUpdate={setSelected} />
        )}
      </section>

      {selected && (
        <StatusModal
          shipment={selected}
          onClose={() => setSelected(null)}
          onUpdated={handleUpdated}
        />
      )}
    </div>
  );
}