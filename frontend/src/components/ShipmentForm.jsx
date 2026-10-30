import { useState } from "react";
import { createShipment, getErrorInfo } from "../api";

const emptyForm = {
  trackingNumber: "",
  customerName: "",
  customerMobile: "",
  deliveryAddress: "",
  destinationPincode: "",
};

const fields = [
  { name: "trackingNumber", label: "Tracking number", placeholder: "TRK3001" },
  { name: "customerName", label: "Customer name", placeholder: "Full name" },
  { name: "customerMobile", label: "Customer mobile", placeholder: "10-digit number" },
  { name: "destinationPincode", label: "Destination pincode", placeholder: "6-digit pincode" },
];

export default function ShipmentForm({ onCreated }) {
  const [form, setForm] = useState(emptyForm);
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setFormError("");
    setFieldErrors({});
    try {
      const created = await createShipment(form);
      setForm(emptyForm);
      onCreated(created);
    } catch (err) {
      const info = getErrorInfo(err);
      setFieldErrors(info.fieldErrors);
      setFormError(info.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="card">
      <h2>Create shipment</h2>
      {formError && <div className="alert error">{formError}</div>}
      <form onSubmit={handleSubmit} className="form-grid" noValidate>
        {fields.map((f) => (
          <label key={f.name}>
            {f.label}
            <input
              name={f.name}
              value={form[f.name]}
              onChange={handleChange}
              placeholder={f.placeholder}
              className={fieldErrors[f.name] ? "invalid" : ""}
            />
            {fieldErrors[f.name] && (
              <span className="field-error">{fieldErrors[f.name]}</span>
            )}
          </label>
        ))}
        <label className="full">
          Delivery address
          <textarea
            name="deliveryAddress"
            value={form.deliveryAddress}
            onChange={handleChange}
            placeholder="Door no, street, city"
            rows={2}
            className={fieldErrors.deliveryAddress ? "invalid" : ""}
          />
          {fieldErrors.deliveryAddress && (
            <span className="field-error">{fieldErrors.deliveryAddress}</span>
          )}
        </label>
        <div className="full">
          <button type="submit" disabled={submitting}>
            {submitting ? "Creating..." : "Create shipment"}
          </button>
        </div>
      </form>
    </section>
  );
}