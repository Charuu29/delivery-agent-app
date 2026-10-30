import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
});

export const getShipments = (status, search) =>
  api
    .get("/shipments", {
      params: { status: status || undefined, search: search || undefined },
    })
    .then((res) => res.data);

export const createShipment = (data) =>
  api.post("/shipments", data).then((res) => res.data);

export const updateShipmentStatus = (id, data) =>
  api.patch(`/shipments/${id}/status`, data).then((res) => res.data);

// Turns any Axios error into a message and per-field errors for the UI
export function getErrorInfo(err) {
  if (!err.response) {
    return {
      message: "Cannot reach the server. Please check that the backend is running.",
      fieldErrors: {},
    };
  }
  const data = err.response.data || {};
  return {
    message: data.message || "Something went wrong. Please try again.",
    fieldErrors: data.fieldErrors || {},
  };
}