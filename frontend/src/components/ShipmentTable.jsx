const FINAL = ["DELIVERED", "DELIVERY_FAILED"];

export default function ShipmentTable({ shipments, onUpdate }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Tracking no.</th>
            <th>Customer</th>
            <th>Pincode</th>
            <th>Address</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {shipments.map((s) => (
            <tr key={s.id}>
              <td>{s.trackingNumber}</td>
              <td>{s.customerName}</td>
              <td>{s.destinationPincode}</td>
              <td>{s.deliveryAddress}</td>
              <td>
                <span className={`badge ${s.status}`}>
                  {s.status.replaceAll("_", " ")}
                </span>
                {s.failureReason && (
                  <div className="reason">Reason: {s.failureReason}</div>
                )}
              </td>
              <td>
                <button
                  className="small"
                  disabled={FINAL.includes(s.status)}
                  onClick={() => onUpdate(s)}
                >
                  Update status
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}