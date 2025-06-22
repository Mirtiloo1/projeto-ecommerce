import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken");

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const getProdutos = async () => {
  try {
    const response = await api.get("/produtos");
    return response.data.content;
  } catch (error) {
    console.log("Erro ao buscar produtos:", error);
    return [];
  }
};

export { api };