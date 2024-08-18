import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { LoginRequest, useLoginMutation } from "../../services/auth/authApi";
import { setToken } from "../../services/auth/authSlice";
import { LoginForm } from "./LoginForm";

export function LoginContainer() {
  const [login, { isLoading }] = useLoginMutation();
  
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = async (request: LoginRequest) => {
    const response = await login(request).unwrap();
      
    dispatch(setToken({ token: response.accessToken }));
  }
  
  return <LoginForm onLogin={handleLogin} isLogging={isLoading} navigate={navigate} />
}