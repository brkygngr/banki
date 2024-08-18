import { useNavigate } from 'react-router-dom';
import { RegisterRequest, useRegisterMutation } from '../../services/auth/authApi';
import { RegisterForm } from './RegisterForm';

export function RegisterContainer() {
  const [register, { isLoading }] = useRegisterMutation();

  const navigate = useNavigate();

  const handleRegister = async (request: RegisterRequest) => {
    await register(request).unwrap();
  };

  return <RegisterForm onRegister={handleRegister} isRegistering={isLoading} navigate={navigate} />;
}
