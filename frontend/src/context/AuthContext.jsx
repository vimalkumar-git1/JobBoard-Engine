import { createContext, useContext, useState } from "react";
import client from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("cp_user");
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email, password) => {
    const res = await client.post("/auth/login", { email, password });
    persistSession(res.data);
  };

  const register = async (payload) => {
    const res = await client.post("/auth/register", payload);
    persistSession(res.data);
  };

  const persistSession = (authResponse) => {
    localStorage.setItem("cp_token", authResponse.token);
    const userData = { fullName: authResponse.fullName, email: authResponse.email };
    localStorage.setItem("cp_user", JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem("cp_token");
    localStorage.removeItem("cp_user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
